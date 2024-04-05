package com.mgws.adownkyi.core.downloader

import com.mgws.adownkyi.core.bilibili.HttpClient
import com.mgws.adownkyi.core.bilibili.HttpConfig
import com.mgws.adownkyi.core.utils.logE
import com.mgws.adownkyi.core.utils.logW
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.util.UUID

class BuiltinDownloader(savePath: String) :
    AbstractDownloader(savePath, 5) {

    private val retryCount: Int = 5
    private val retryDelay: Long = 2000L

    override fun checkStatusWith(taskId: UUID, status: Int): Boolean {
        val info = taskQueue[taskId] ?: throw Throwable("error this task is not exist")

        if (info.group[0] != info.id)
            throw Throwable("error this task is children")

        return info.status == status
    }

    override fun cancelTask(taskId: UUID) {
        logW("cancel task: ${taskQueue[taskId]}")
        taskQueue[taskId]?.let { info ->
            info.group.forEach(taskQueue::remove)
            changeDownloadInfoState(info, DownloadInfo.CANCEL)
        }
    }

    /**
     * 开启一个下载线程
     */
    override suspend fun startDownload(info: DownloadInfo) = coroutineScope {
        var taskRetryCount = retryCount

        do {
            changeDownloadInfoState(info, DownloadInfo.RUNNING)

            if (info.group.size == 1)
                download(info)
            else
                downloads(info.group.map { taskQueue[it]!! })

            if (info.status != DownloadInfo.ERROR) {
                return@coroutineScope
            }
            taskRetryCount--
            delay(retryDelay)
            logW("download task retries remain: $taskRetryCount")
        } while (info.status == DownloadInfo.ERROR && taskRetryCount != 0)
    }

    private suspend fun prepareDownloadStream(info: DownloadInfo): DownloadStream? =
        withContext(dispatcher) {
            val file = File("$savePath${File.separator}${info.fileName}")

            if (!file.exists()) {
                file.createNewFile()
            }

            val fileLength = file.length()

            // 纠正已经下载的字节
            if (info.current <= fileLength) {
                info.current = fileLength
            } else {
                info.current = 0
            }

            if (info.total < info.current) {
                info.current = 0
            }

            val outputStream = if (info.current == 0L) {
                file.outputStream()
            } else {
                FileOutputStream(file, true)
            }

            // TODO: extract BiliBiliHttpConfig
            // TODO: 暂时先这样
            val httpConfig = HttpConfig.BaseHttpConfig(range = "bytes=${info.current}-")
            httpConfig.cookies = HttpConfig.BiliBiliHttpConfig.cookies
            val httpConnect = HttpClient.getHttpConnection("GET", info.url, httpConfig)

            try {
                httpConnect.connect()
                if (httpConnect.responseCode == 416) {
                    logW("http 416: out of range(${info.current},${info.total},$fileLength), ${file.name}")
                    info.total = 0
                    changeDownloadInfoState(info, DownloadInfo.ERROR)
                    return@withContext null
                }
            } catch (e: IOException) {
                logE("connect to server failed", e)
                info.exception = e
                changeDownloadInfoState(info, DownloadInfo.ERROR)
                return@withContext null
            }

            val inputStream = HttpClient.getInputStreamAsEncoding(httpConnect)

            info.total = info.current + httpConnect.contentLengthLong

            return@withContext DownloadStream(
                file to outputStream,
                httpConnect to inputStream
            )
        }

    private fun closeDownloadStream(downloadStream: DownloadStream) {
        val (_, outputStream) = downloadStream.fileP
        val (httpConnect, inputStream) = downloadStream.connectP
        inputStream.close()
        outputStream.close()
        httpConnect.disconnect()
    }

    private suspend fun download(info: DownloadInfo) = coroutineScope {
        val downloadStream = prepareDownloadStream(info) ?: return@coroutineScope

        val (_, outputStream) = downloadStream.fileP
        val (_, inputStream) = downloadStream.connectP

        val bufferSize = 8192
        val buffer = ByteArray(bufferSize)
        var bytes: Int = -1
        try {

            while (inputStream.read(buffer).also { bytes = it } != -1) {
                outputStream.write(buffer, 0, bytes)
                info.current += bytes

                if (info.status != DownloadInfo.RUNNING) break
            }

        } catch (e: IOException) {
            info.exception = e
            changeDownloadInfoState(info, DownloadInfo.ERROR)
            logE("下载失败", e)
        } finally {
            closeDownloadStream(downloadStream)

            //没读完就没字符了, 说明下载失败
            if (bytes == -1 && info.current != info.total) {
                info.exception = IOException("exception stream read finish")
                changeDownloadInfoState(info, DownloadInfo.ERROR)
            }

            if (info.current == info.total) {
                changeDownloadInfoState(info, DownloadInfo.SUCCESS)
            }

            // 下载任务取消删除文件
            if (info.status == DownloadInfo.CANCEL) {
                downloadStream.fileP.first.delete()
            }

        }

    }

    private suspend fun downloads(infoList: List<DownloadInfo>) = coroutineScope {
        val pool: List<DownloadStream> = infoList.map {
            prepareDownloadStream(it) ?: return@coroutineScope
        }

        var index = 0
        var finishCount = infoList.filter { it.current == it.total }.size

        try {

            while (finishCount != infoList.size) {
                val info = infoList[index]
                val downloadStream = pool[index]

                if (info.current == info.total) {
                    closeDownloadStream(downloadStream)
                    finishCount++
                    index++
                    index %= infoList.size
                    continue
                }

                val (_, outputStream) = downloadStream.fileP
                val (_, inputStream) = downloadStream.connectP

                // 设置缓冲区大小8KB
                val bufferSize = 8192
                val buffer = ByteArray(bufferSize)
                var bytes: Int

                if (inputStream.read(buffer).also { bytes = it } != -1) {
                    outputStream.write(buffer, 0, bytes)
                    info.current += bytes
                }

                if (info.status != DownloadInfo.RUNNING) break

                //没读完就没字符了, 说明下载失败
                if (bytes == -1 && info.current != info.total) {
                    infoList[0].exception = IOException("exception stream read finish")
                    changeDownloadInfoState(infoList[0], DownloadInfo.ERROR)
                    break
                }
            }


        } catch (e: IOException) {
            infoList[0].exception = e
            changeDownloadInfoState(infoList[0], DownloadInfo.ERROR)
            logE("下载失败", e)
        } finally {

            val current = infoList.sumOf { it.current }
            val total = infoList.sumOf { it.total }

            if (current == total) {
                changeDownloadInfoState(infoList[0], DownloadInfo.SUCCESS)
            }

            if (infoList[0].status == DownloadInfo.ERROR) {
                pool.forEach(::closeDownloadStream)
            }

            // 下载任务取消删除文件
            if (infoList[0].status == DownloadInfo.CANCEL) {
                pool.forEach { downloadStream ->
                    closeDownloadStream(downloadStream)
                    downloadStream.fileP.first.delete()
                }
            }
        }

    }

    /**
     * @param info 必须是主任务
     * 可以改变任务组状态,包括子任务
     */
    override fun changeDownloadInfoState(info: DownloadInfo, status: Int) {
        // 是子任务
        if (info.group[0] != info.id) return
        if (info.status == status) return
        // 已经成功,就不不能改状态了
        if (info.status == DownloadInfo.SUCCESS) return

        info.group.forEach { taskQueue[it]?.status = status }

        when (status) {
            DownloadInfo.PREPARE -> {
                runningTasks.remove(info.id)
                prepareTasks.add(info.id)
            }

            DownloadInfo.RUNNING -> {
                prepareTasks.remove(info.id)
                runningTasks.add(info.id)
            }

            else -> {
                prepareTasks.remove(info.id)
                runningTasks.remove(info.id)
            }
        }

        when (status) {
            DownloadInfo.RUNNING -> CoroutineScope(Dispatchers.Default).launch {
                progressMonitor(info)
            }

            DownloadInfo.PAUSE -> listener?.pause(info.id)
            DownloadInfo.CANCEL -> listener?.cancel(info.id)
            DownloadInfo.SUCCESS -> {
                val tasks = info.group.map { taskQueue[it]!! }
                val current = tasks.sumOf { it.current }
                val total = tasks.sumOf { it.total }
                listener?.progress(info.id, current, total)

                listener?.success(info.id)
            }

            DownloadInfo.ERROR -> listener?.error(
                info.id,
                info.exception ?: IOException("unknown error")
            )
        }


    }

    private suspend fun progressMonitor(info: DownloadInfo) = coroutineScope {
        val tasks = info.group.map { taskQueue[it]!! }
        var tempCurrent: Long? = null

        while (true) {
            val current = tasks.sumOf { it.current }
            val total = tasks.sumOf { it.total }

            // 进度没改变, 不是运行状态,关闭监听
            if (info.status != DownloadInfo.RUNNING) break

            // 进度没改变,跳过,不通知
            if (tempCurrent == current) {
                continue
            }

            if (total != 0L && current == total) break

            listener?.progress(info.id, current, total)
            tempCurrent = current

            delay(500)
        }
    }

    inner class DownloadStream(
        var fileP: Pair<File, OutputStream>,
        var connectP: Pair<HttpURLConnection, InputStream>,
    )
}
