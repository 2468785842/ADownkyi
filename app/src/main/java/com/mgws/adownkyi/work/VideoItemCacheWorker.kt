package com.mgws.adownkyi.work

import android.annotation.SuppressLint
import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.mgws.adownkyi.core.bilibili.videoStream.VideoStreamService
import com.mgws.adownkyi.core.bilibili.videoStream.model.PlayUrl
import com.mgws.adownkyi.core.utils.logI
import com.mgws.adownkyi.model.home.VideoItemUiState
import com.mgws.adownkyi.model.home.VideoQualityUiState
import com.mgws.adownkyi.repo.AppCacheRepository
import com.mgws.adownkyi.utils.AppMessagePublisher.publish
import com.mgws.adownkyi.utils.ParseUtils
import com.mgws.adownkyi.utils.errorMsg
import com.mgws.adownkyi.utils.infoMsg
import com.mgws.adownkyi.work.VideoItemCacheWorker.Companion.Action.SAVE_VIDEO_ITEM_CACHE
import com.mgws.adownkyi.work.VideoItemCacheWorker.Companion.Action.UPDATE_VIDEO_ITEM_CACHE
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromHexString
import kotlinx.serialization.encodeToHexString
import kotlinx.serialization.protobuf.ProtoBuf

@HiltWorker
class VideoItemCacheWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val videoStreamService: VideoStreamService,
    private val appCacheRepository: AppCacheRepository,
) : CoroutineWorker(context, params) {

    companion object {
        var videoItemUiStateList = MutableStateFlow(emptyList<VideoItemUiState>())

        private const val ACTION = "ACTION"
        private const val VIDEO_ITEM_CACHE_LIST = "VIDEO_ITEM_CACHE_LIST"

        private object Action {
            const val UPDATE_VIDEO_ITEM_CACHE = "UPDATE_$VIDEO_ITEM_CACHE_LIST"
            const val SAVE_VIDEO_ITEM_CACHE = "SAVE_$VIDEO_ITEM_CACHE_LIST"
        }

        @SuppressLint("EnqueueWork")
        @OptIn(ExperimentalSerializationApi::class)
        fun WorkManager.saveVideoItemCache(videoItemCacheList: List<VideoItemUiState>) {
            if (videoItemCacheList.isEmpty()) return

            val clearRequest = OneTimeWorkRequestBuilder<VideoItemCacheWorker>()
                .setInputData(
                    workDataOf(
                        ACTION to SAVE_VIDEO_ITEM_CACHE
                    )
                ).build()

            var workContinuation = this.beginUniqueWork(
                SAVE_VIDEO_ITEM_CACHE,
                ExistingWorkPolicy.REPLACE, clearRequest
            )
            val stringList = mutableListOf<String>()
            var dataSize = 0

            for (videoItemCache in videoItemCacheList) {

                val hexString = ProtoBuf.encodeToHexString(videoItemCache)
                val hexStringSize = hexString.length
                // Worker数据限制每次只能传10kb
                if (dataSize + hexStringSize >= 8 * 1024) {
                    val inputData = workDataOf(
                        ACTION to SAVE_VIDEO_ITEM_CACHE,
                        VIDEO_ITEM_CACHE_LIST to stringList.toTypedArray()
                    )
                    workContinuation = workContinuation.then(
                        OneTimeWorkRequestBuilder<VideoItemCacheWorker>()
                            .setInputData(inputData).build()
                    )
                    stringList.clear()
                    dataSize = 0
                }
                dataSize += hexStringSize
                stringList.add(hexString)
            }


            workContinuation.enqueue()
        }

        /**
         * @return uniqueWorkName
         */
        fun WorkManager.updateVideoItemCache(): String {
            val inputData = workDataOf(
                ACTION to UPDATE_VIDEO_ITEM_CACHE,
            )

            val request = OneTimeWorkRequestBuilder<VideoItemCacheWorker>()
                .setInputData(inputData)
                .build()

            this.enqueueUniqueWork(
                UPDATE_VIDEO_ITEM_CACHE,
                ExistingWorkPolicy.REPLACE, request
            )
            return UPDATE_VIDEO_ITEM_CACHE
        }

        @OptIn(ExperimentalSerializationApi::class)
        private inline fun <reified T> parseObjectFromProto(hexString: String) =
            ProtoBuf.decodeFromHexString<T>(hexString)
    }

    override suspend fun doWork(): Result {
        val action = inputData.getString(ACTION)

        when (action) {
            SAVE_VIDEO_ITEM_CACHE -> {
                val stringList = inputData.getStringArray(VIDEO_ITEM_CACHE_LIST)
                var saveVideoItemCacheList: List<VideoItemUiState>? = null
                if (stringList != null) {
                    saveVideoItemCacheList = stringList.map {
                        parseObjectFromProto<VideoItemUiState>(it)
                    }
                }
                return saveVideoItemCache(saveVideoItemCacheList)
            }

            UPDATE_VIDEO_ITEM_CACHE -> return updateVideoItemCache()
        }

        return Result.failure()
    }

    private fun saveVideoItemCache(saveVideoItemCacheList: List<VideoItemUiState>?): Result {
        if (saveVideoItemCacheList == null) {
            appCacheRepository.clearVideoItemCacheList()
            logI("clean video list cache")
        } else {
            appCacheRepository.addAllVideoItemCacheList(saveVideoItemCacheList)
            logI("add video list cache: ${saveVideoItemCacheList.size}")
        }
        return Result.success()
    }

    private suspend fun updateVideoItemCache(): Result {
        val videoListCache = appCacheRepository.videoListCacheFlow.first()
        val videoItemUiStates = Array<VideoItemUiState?>(videoListCache.size) { null }
        videoListCache.forEachIndexed { index, it ->
            publish(infoMsg("从缓存更新视频列表(${index + 1}/${videoListCache.size})"))

            var retryCount = 5
            var videoPlayUrl: PlayUrl? = null

            while (retryCount != 0) {
                videoPlayUrl = videoStreamService.getVideoPlayUrl(it.avid, it.bvid, it.cid)
                if (videoPlayUrl != null) break
                retryCount--
                publish(errorMsg("网络错误,最后尝试次数!: $retryCount"))
            }

            if (videoPlayUrl == null) {
                publish(errorMsg("网络错误,获取视频播放连接失败"))
                return Result.failure()
            }

            val audioQualityFormatList = ParseUtils.getAudioQualityFormatList(videoPlayUrl)

            val videoQualityList = ParseUtils.getVideoQualityList(videoPlayUrl)
            val videoCodecList = ParseUtils.getVideoCodecList(videoPlayUrl)

            val videoQualityUiStateList = videoQualityList.map { pair ->
                VideoQualityUiState(
                    pair.first,
                    pair.second,
                    videoCodecList,
                    videoCodecList[0]
                )
            }

            videoItemUiStates[index] = VideoItemUiState(
                playUrl = videoPlayUrl,
                avid = it.avid,
                bvid = it.bvid,
                cid = it.cid,
                episodeId = -1,
                firstFrame = it.firstFrame,
                order = it.order,
                name = it.name,
                duration = it.duration,
                publishTime = it.publishTime,
                audioQualityFormat = audioQualityFormatList[0],
                audioQualityFormatList = audioQualityFormatList,
                videoQuality = videoQualityUiStateList[0],
                videoQualityList = videoQualityUiStateList
            )
        }

        videoItemUiStateList.value = videoItemUiStates.filterNotNull()
        return Result.success()
    }

}