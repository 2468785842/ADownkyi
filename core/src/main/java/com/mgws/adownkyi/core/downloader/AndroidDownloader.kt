package com.mgws.adownkyi.core.downloader

import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.core.net.toUri
import com.mgws.adownkyi.core.bilibili.BiliBiliHttpConfig
import com.mgws.adownkyi.core.utils.logI
import kotlinx.coroutines.coroutineScope
import java.io.File
import java.io.IOException
import java.util.UUID
import kotlin.concurrent.thread


/**
 * TODO
 * 不支持断点续传,暂停,恢复
 * 不支持任务组
 *
 * @param context the [Context] to use in determining the external
 *            files directory
 * @param dirType the directory type to pass to [Context.getExternalFilesDir]
 * @param subPath the path within the external directory
 */
class AndroidDownloader(
    private val context: Context,
    private val dirType: String,
    private val subPath: String = "",
) : AbstractDownloader(
    Uri.withAppendedPath(Uri.fromFile(context.getExternalFilesDir(dirType)), subPath).path!!,
    5
) {

    private val downloadManager = context.getSystemService(DownloadManager::class.java)
    private val uuidMap = LinkedHashMap<Long, UUID>()
    private val idMap = LinkedHashMap<UUID, Long>()

    private var monitorThread: Thread? = null

    override suspend fun startDownload(info: DownloadInfo) = coroutineScope {
        // 启动一个线程实时监听下载进度,状态
        if (monitorThread == null) {
            monitorThread = thread {
                Looper.prepare()
                context.contentResolver.registerContentObserver(
                    Uri.parse("content://downloads/my_downloads"),
                    true,
                    DownloadChangeObserver(Handler(Looper.myLooper()!!))
                )
                Looper.loop()
            }
        }

        val file = File("$savePath${File.separator}${info.fileName}")
        if (file.exists()) {
            val delete = file.delete()
            logI("this file is exists delete result: $delete")
        }

        val request = DownloadManager.Request(info.url.toUri())
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
        request.setTitle(info.fileName)
        request.setDescription(info.fileName)
        BiliBiliHttpConfig.addHeader(request::addRequestHeader)
        request.setDestinationInExternalFilesDir(
            context, dirType,
            Uri.withAppendedPath(subPath.toUri(), info.fileName).path
        )

        val taskId = downloadManager.enqueue(request)
        uuidMap[taskId] = info.id
        idMap[info.id] = taskId
        info.status = DownloadInfo.RUNNING
    }

    private fun getTaskProgress(cursor: Cursor): Pair<Long, Long> {
        var columnIndex =
            cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
        val currentSize = cursor.getLong(columnIndex)

        columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
        val totalSize = cursor.getLong(columnIndex)

        if (currentSize < 0 || totalSize < 0) return 0L to 0L
        return currentSize to totalSize
    }


    override fun getTaskForStatus(status: Int) = taskQueue.values.filter {
        it.status == status
    }

    override fun checkStatusWith(taskId: UUID, status: Int): Boolean {
        val info = taskQueue[taskId] ?: throw Throwable("error this task is not exist")

        return info.status == status
    }

    override fun pauseTask(taskId: UUID) {
        val values = ContentValues()
        values.put(DownloadManager.COLUMN_STATUS, DownloadManager.STATUS_PAUSED)
        context.contentResolver.update(
            Uri.parse("content://downloads/my_downloads"),
            values,
            "${DownloadManager.COLUMN_ID} = ?",
            arrayOf(idMap[taskId]!!.toString())
        )
    }

    override fun resumeTask(taskId: UUID, vararg refreshUrl: String) {
        TODO("Not yet implemented")
    }

    override fun cancelTask(taskId: UUID) {
        TODO("Not yet implemented")
    }


    override fun changeDownloadInfoState(info: DownloadInfo, status: Int) {
        if (info.status == status) return
        // 已经成功,就不不能改状态了
        if (info.status == DownloadInfo.SUCCESS) return

        info.status = status

        when (status) {
            DownloadInfo.PAUSE -> listener?.pause(info.id)
            DownloadInfo.CANCEL -> listener?.cancel(info.id)
            DownloadInfo.SUCCESS -> listener?.success(info.id)
            DownloadInfo.ERROR -> listener?.error(info.id, IOException("error"))
        }
    }

    /**
     * _id
     * local_filename
     * mediaprovider_uri
     * destination
     * title
     * description
     * uri
     * status
     * hint
     * media_type
     * total_size
     * last_modified_timestamp
     * bytes_so_far
     * allow_write
     * local_uri
     * reason
     */
    private inner class DownloadChangeObserver(handler: Handler) :
        ContentObserver(handler) {
        override fun onChange(selfChange: Boolean) {
            if (uuidMap.isEmpty()) return

            val filter = DownloadManager.Query().setFilterById(*uuidMap.keys.toLongArray())
            val cursor = downloadManager.query(filter)
            var columnIndex: Int

            while (cursor.moveToNext()) {
                columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_ID)
                val taskId = uuidMap[cursor.getLong(columnIndex)]!!
                columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)

                when (cursor.getInt(columnIndex)) {
                    DownloadManager.STATUS_RUNNING,
                    DownloadManager.STATUS_SUCCESSFUL,
                    -> {
                        val (current, total) = getTaskProgress(cursor)
                        if (total < 1) break
                        listener?.progress(taskId, current, total)

                        if (current == total) {
                            changeDownloadInfoState(taskQueue[taskId]!!, DownloadInfo.SUCCESS)
                        }
                    }

                    DownloadManager.STATUS_FAILED -> {
                        changeDownloadInfoState(taskQueue[taskId]!!, DownloadInfo.ERROR)
                    }

                    DownloadManager.STATUS_PAUSED -> {
                        changeDownloadInfoState(taskQueue[taskId]!!, DownloadInfo.PAUSE)
                    }
                }

            }
            cursor.close()
        }

    }
}