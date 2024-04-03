package com.mgws.adownkyi.core.downloader

import java.util.UUID

interface Downloader {

    /**
     * 添加一个任务,调用[start]开始处理任务
     *
     * @param url 下载链接
     * @param fileName 文件名
     *
     * @return 任务id
     */
    fun addTask(
        url: String, fileName: String,
    ): UUID

    /**
     * 添加一组任务, 计算进度为一组任务,调用[start]开始处理任务
     * urls的长度必须等于fileNames的长度
     *
     * @param urls 一组下载链接
     * @param fileNames 一组文件名
     *
     * @return 任务id
     */
    fun addTasks(
        urls: List<String>, fileNames: List<String>,
    ): UUID

    /**
     * 启动服务,只有调用此方法才会开始执行下载任务
     *
     * @param serializeDownloadTask 启动之后立即添加任务序列化字符串,
     * [Downloader.serializeDownloadTask]保存任务列表,可以在此恢复
     */
    suspend fun start(serializeDownloadTask: String? = null)

    /**
     * 关闭服务
     */
    fun stop()

    /**
     * 检查任务状态
     *
     * @param taskId 任务id
     * @param status 状态
     *
     * @return 当前任务与status匹配返回true,否则false
     */
    fun checkStatusWith(taskId: UUID, status: Int): Boolean

    /**
     * 暂停任务
     *
     * @param taskId 任务id
     */
    fun pauseTask(taskId: UUID)

    /**
     * 恢复任务
     *
     * @param taskId 任务id
     * @param refreshUrl 新的下载连接, 必须与添加任务时顺序一致[addTasks], [addTask]
     */
    fun resumeTask(taskId: UUID, vararg refreshUrl: String)

    /**
     * 取消任务,删除任务
     *
     * @param taskId 任务id
     */
    fun cancelTask(taskId: UUID)

    /**
     * 删除所有任务
     */
    fun clearAllTask()

    fun setListener(listener: DownloadListener)

    fun taskExists(taskId: UUID): Boolean

    /**
     * 将当前下载任务信息序列化为JSON string
     *
     * @return 序列化后的字符串, 不包括成功的任务
     */
    fun serializeDownloadTask(): String
}
