package com.mgws.adownkyi.repo

import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import com.mgws.adownkyi.core.bilibili.videoStream.VideoStreamService
import com.mgws.adownkyi.core.downloader.DownloadListener
import com.mgws.adownkyi.core.media.AndroidMediaHelper
import com.mgws.adownkyi.core.utils.logE
import com.mgws.adownkyi.core.utils.logI
import com.mgws.adownkyi.core.utils.logW
import com.mgws.adownkyi.model.download.DownloadItemUiState
import com.mgws.adownkyi.model.download.DownloadUiState
import com.mgws.adownkyi.service.DownloadServiceBinder
import com.mgws.adownkyi.utils.copyFile
import com.mgws.adownkyi.utils.deleteFile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * TODO 添加空指针检查
 */
@Singleton
class DownloadRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appCacheRepository: AppCacheRepository,
    private val settingsRepository: SettingsRepository,
    private val videoStreamService: VideoStreamService,
) {

    private var mergeMediaJob = LinkedHashMap<UUID, Job>()

    private val path = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!.path

    var downloadTaskMap = LinkedHashMap<UUID, DownloadItemUiState>()
        private set

    /**
     * 会从MainActivity加载,一次
     */
    var downloadServiceBinder: DownloadServiceBinder? = null
        set(value) {
            field = value?.also {
                it.listener = DownloadListenerImpl()
                // 加载缓存
                CoroutineScope(Dispatchers.Default).launch {
                    appCacheRepository.downloadTaskCacheFlow.first().forEach { downloadItem ->
                        downloadItem.isLoadingForSerialize = true
                        downloadTaskMap[downloadItem.id] = downloadItem
                    }
                    _loading.emit(false)
                }
            }
        }

    private var _loading = MutableStateFlow(true)
    val loading = _loading

    private fun isTaskPresent(name: String): Boolean =
        downloadTaskMap.values.find { it.name == name } != null

    fun addTask(name: String, downloadUiState: DownloadUiState) {
        if (isTaskPresent(name)) return

        val videoUrl = downloadUiState.dashVideo.baseUrl
        val audioUrl = downloadUiState.dashAudio.baseUrl

        val taskId = downloadServiceBinder!!.addTasks(
            listOf(audioUrl, videoUrl),
            listOf("$name.a", "$name.v")
        )

        downloadTaskMap[taskId] = DownloadItemUiState(
            _id = taskId.toString(),
            name = name,
            coverUrl = downloadUiState.pageCoverUrl!!,
            avid = downloadUiState.avid,
            bvid = downloadUiState.bvid,
            cid = downloadUiState.cid,
        )

        // 保存任务到缓存
        appCacheRepository.addDownloadTaskCacheList(downloadTaskMap[taskId]!!)
    }

    fun pauseTask(id: UUID) {
        downloadServiceBinder!!.pauseTask(id)
    }

    suspend fun cancelTask(id: UUID) {
        val task = downloadTaskMap[id]!!
        if (task.status == DownloadItemUiState.MEDIA_MERGE) {
            mergeMediaJob[id]!!.cancel()
            val aFile = File("$path${File.separator}${task.name}.a").delete()
            val vFile = File("$path${File.separator}${task.name}.v").delete()
            logW("cancel task: $id, a: $aFile, v: $vFile")
            val mp4File = File("$path${File.separator}${task.name}.mp4")
            if (mp4File.exists()) {
                val result = mp4File.delete()
                logW("delete internal mp4: $result")
            }
        } else if (task.status == DownloadItemUiState.SUCCESS) {
            context.deleteFile(
                settingsRepository.savePath.first().toUri(),
                "${task.name}.mp4"
            )
        } else {
            downloadServiceBinder!!.cancelTask(id)
        }
        downloadTaskMap.remove(id)
        appCacheRepository.removeDownloadTaskCacheList(id)
    }

    /**
     * 为什么有时候会失败??,因为视频连接会过期,
     * 时间长了就不能用了,
     * 需要更新url视频下载连接
     */
    suspend fun resumeTask(id: UUID) {
        val task = downloadTaskMap[id]!!
        if (task.status == DownloadItemUiState.MEDIA_MERGE) return
        if (task.status == DownloadItemUiState.DOWNLOADING) return

        var refreshUrl: Array<String> = emptyArray()

        // 要和添加任务(addTask(..))时顺序一致
        if (task.isLoadingForSerialize) {
            val videoPlayUrl = videoStreamService.getVideoPlayUrl(task.avid, task.bvid, task.cid)
                ?: // TODO 检查网络
                throw Throwable("error videoPlayUrl is null, check network")

            refreshUrl = arrayOf(
                videoPlayUrl.dash.audio[0].baseUrl,
                videoPlayUrl.dash.video[0].baseUrl
            )
            task.isLoadingForSerialize = false
        }
        downloadServiceBinder!!.resumeTask(id, *refreshUrl)
        appCacheRepository.updateDownloadTaskCache(downloadTaskMap[id]!!)
    }

    // -----------------------------------回调-----------------------------------------//
    inner class DownloadListenerImpl : DownloadListener {

        override fun progress(id: UUID, current: Long, total: Long) {
            downloadTaskMap[id]?.let {
                it.current = current.toInt()
                it.total = total.toInt()
                it.status = DownloadItemUiState.DOWNLOADING

                //更新缓存
                appCacheRepository.updateDownloadTaskCache(it)
            }

        }

        override fun success(id: UUID) {
            downloadTaskMap[id]?.let {
                it.status = DownloadItemUiState.MEDIA_MERGE
                val job = CoroutineScope(Dispatchers.IO).launch {
                    try {
                        AndroidMediaHelper.merge(
                            "$path${File.separator}${it.name}.a",
                            "$path${File.separator}${it.name}.v",
                            "$path${File.separator}${it.name}.mp4"
                        )
                    } catch (e: Exception) {
                        it.status = DownloadItemUiState.FAILED
                        logE("merge video error", e)
                    }

                    context.copyFile(
                        settingsRepository.savePath.first().toUri(),
                        "${it.name}.mp4",
                        "$path${File.separator}${it.name}.mp4", "video/mp4"
                    )

                    File("$path${File.separator}${it.name}.mp4").delete()

                    it.status = DownloadItemUiState.SUCCESS

                    //更新缓存
                    appCacheRepository.updateDownloadTaskCache(it)
                }

                mergeMediaJob[id] = job

            }
        }

        override fun error(id: UUID, exception: IOException) {
            //TODO 通知用户异常信息
            downloadTaskMap[id]?.let {
                it.status = DownloadItemUiState.FAILED
                //更新缓存
                appCacheRepository.updateDownloadTaskCache(downloadTaskMap[id]!!)
            }
        }

        override fun pause(id: UUID) {
            logI("pause: $id")
            downloadTaskMap[id]?.let {
                it.status = DownloadItemUiState.PAUSED

                //更新缓存
                appCacheRepository.updateDownloadTaskCache(downloadTaskMap[id]!!)
            }
        }

        override fun cancel(id: UUID) {
            logI("cancel: $id")
        }
    }

}