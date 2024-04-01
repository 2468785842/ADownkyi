package com.mgws.adownkyi.service

import android.os.Binder
import com.mgws.adownkyi.core.downloader.DownloadListener
import com.mgws.adownkyi.core.downloader.Downloader
import java.util.UUID

class DownloadServiceBinder(private val downloader: Downloader) : Binder() {
    var listener: DownloadListener? = null

    fun addTask(url: String, fileName: String) =
        downloader.addTask(url, fileName)

    fun addTasks(urls: List<String>, fileNames: List<String>) =
        downloader.addTasks(urls, fileNames)

    fun pauseTask(taskId: UUID) = downloader.pauseTask(taskId)

    fun resumeTask(taskId: UUID, vararg refreshUrl: String) =
        downloader.resumeTask(taskId, *refreshUrl)

    fun cancelTask(taskId: UUID) = downloader.cancelTask(taskId)

    fun taskExists(taskId: UUID) = downloader.taskExists(taskId)
}