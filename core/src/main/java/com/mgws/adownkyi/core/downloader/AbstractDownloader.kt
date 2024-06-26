package com.mgws.adownkyi.core.downloader

import com.mgws.adownkyi.core.utils.logI
import com.mgws.adownkyi.core.utils.logW
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.util.Collections
import java.util.LinkedList
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

abstract class AbstractDownloader(
    protected val savePath: String,
    private val maxRunningTaskCount: Int,
) : Downloader {

    protected val prepareTasks: MutableList<UUID> = Collections.synchronizedList(LinkedList())
    protected val runningTasks: MutableList<UUID> = LinkedList()

    @JvmField
    protected var listener: DownloadListener? = null

    protected val taskQueue = ConcurrentHashMap<UUID, DownloadInfo>()
    private var isStart = false

    protected val dispatcher = Dispatchers.IO + SupervisorJob() + CoroutineExceptionHandler {
            _, e,
        ->
        taskQueue.values.forEach { changeDownloadInfoState(it, DownloadInfo.ERROR) }
        e.printStackTrace()
    }

    override fun addTask(url: String, fileName: String): UUID {
        val info = DownloadInfo(url = url, fileName = fileName)
        taskQueue[info.id] = info
        prepareTasks.add(info.id)
        return info.id
    }

    /**
     * 添加一个任务,当调用[start]才会开始处理任务
     */
    override fun addTasks(urls: List<String>, fileNames: List<String>): UUID {
        if (fileNames.size != urls.size)
            throw Throwable("fileNames size must be equal to urls size")

        val group = List<UUID>(urls.size) { UUID.randomUUID() }

        for (i in urls.indices) {
            taskQueue[group[i]] = DownloadInfo(
                id = group[i],
                url = urls[i],
                fileName = fileNames[i],
                group = group
            )
        }
        prepareTasks.add(group.first())
        return group.first()
    }

    /**
     * 循环从任务队列获取任务并执行
     */
    override suspend fun start(serializeDownloadTask: String?) = withContext(dispatcher) {

        if (serializeDownloadTask != null) {
            val infoList = Json.decodeFromString<List<DownloadInfo>>(serializeDownloadTask)
            if (infoList.isNotEmpty()) {
                taskQueue.putAll(
                    infoList.associateBy { info ->
                        // 获取主任务
                        if (info.status == DownloadInfo.RUNNING && info.group[0] == info.id) {
                            // 过滤出子任务
                            infoList.filter { info.group.contains(it.id) }.forEach {
                                it.status = DownloadInfo.PREPARE
                            }
                            prepareTasks.add(info.id)
                        }
                        // 不用这个，因为当前任务还未加入到队列,所以用这个不会更新
                        // changeDownloadInfoState(it, DownloadInfo.PREPARE)
                        info.id
                    }
                )
            }
        }

        if (isStart) return@withContext
        isStart = true

        while (isStart) {
            delay(100)
            if (taskQueue.isEmpty()) continue
            if (runningTasks.size >= maxRunningTaskCount) continue

            val count = if (prepareTasks.size > maxRunningTaskCount) {
                maxRunningTaskCount
            } else {
                prepareTasks.size
            }

            synchronized(prepareTasks) {
                for (index in 0..<count) {
                    val task = taskQueue[prepareTasks[index]]!!
                    launch { startDownload(task) }
                }
            }
        }

    }

    protected abstract suspend fun startDownload(info: DownloadInfo)

    protected abstract fun changeDownloadInfoState(info: DownloadInfo, status: Int)

    /**
     * 停止下载器
     */
    override fun stop() {
        if (!isStart) return
        isStart = false
    }

    override fun clearAllTask() {
        if (isStart) {
            logW("downloader is running can't clear task, stop it first")
            return
        }
        taskQueue.keys.forEach(::cancelTask)
    }

    override fun setListener(listener: DownloadListener) {
        this.listener = listener
    }

    override fun pauseTask(taskId: UUID) {
        val info = taskQueue[taskId] ?: throw Throwable("error this task is not exist")

        if (checkStatusWith(taskId, DownloadInfo.PREPARE)) {
            logW("danger!!! this task is prepared, can't pause")
            return
        }

        changeDownloadInfoState(info, DownloadInfo.PAUSE)
    }

    override fun resumeTask(taskId: UUID, vararg refreshUrl: String) {
        val info = taskQueue[taskId] ?: throw Throwable("error this task is not exist")

        if (info.group[0] != info.id)
            throw Throwable("error this task is children")

        if (info.status != DownloadInfo.ERROR &&
            info.status != DownloadInfo.PAUSE
        ) {
            Throwable("this task is not error or pause, can't resume")
        }

        if (info.status == DownloadInfo.ERROR) {
            logW("warning this task state is Error")
            logW("will try resume task, but maybe failed")
        }

        if (refreshUrl.isNotEmpty()) {
            if (info.group.size != refreshUrl.size) {
                throw Throwable("refreshUrl size must be equal to task group size")
            }
            info.group.forEachIndexed { index, it ->
                taskQueue[it]!!.url = refreshUrl[index]
            }
            logI("refreshUrl: ${refreshUrl.toList()}")
        }

        changeDownloadInfoState(info, DownloadInfo.PREPARE)

    }

    override fun taskExists(taskId: UUID): Boolean {
        return taskQueue[taskId] != null
    }

    override fun serializeDownloadTask(): String {
        return Json.encodeToString(
            ListSerializer(DownloadInfo.serializer()),
            taskQueue.values.filter { it.status != DownloadInfo.SUCCESS }
        )
    }
}