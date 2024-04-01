package com.mgws.adownkyi.service

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import com.mgws.adownkyi.R
import com.mgws.adownkyi.core.downloader.DownloadListener
import com.mgws.adownkyi.core.downloader.Downloader
import com.mgws.adownkyi.core.utils.logI
import com.mgws.adownkyi.repo.AppCacheRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID
import javax.inject.Inject


@AndroidEntryPoint
class DownloadService : Service(), CoroutineScope by CoroutineScope(Dispatchers.Default) {

    @Inject
    lateinit var downloader: Downloader

    @Inject
    lateinit var appCacheRepository: AppCacheRepository

    private lateinit var notificationManager: NotificationManager
    private lateinit var binder: DownloadServiceBinder

    private val nId = 472
    private val nChannelId = "ADownKyi-Notification"

    override fun onBind(intent: Intent?) = binder

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NotificationManager::class.java)
        binder = DownloadServiceBinder(downloader)
        downloader.setListener(DownloadListenerImpl())
        //启动
        launch {
            //从缓存加载任务
            downloader.start(appCacheRepository.downloaderInfoCacheFlow.first())
        }
        //显示通知
        showNotification()
        logI("DownloadService started")
    }

    override fun onDestroy() {
        super.onDestroy()
        downloader.stop()
        notificationManager.cancel(nId)
        logI("DownloaderService stopped")
    }

    private fun showNotification() {
//        val contentIntent = PendingIntent.getActivity(
//            this, 0,
//            Intent(this, ADownKyiApplication::class.java),
//            PendingIntent.FLAG_IMMUTABLE
//        )
//        NotificationManager.IMPORTANCE_
//        val channel = NotificationChannel(
//            nChannelId, "下载通知",
//            NotificationManager.IMPORTANCE_HIGH
//        )
//        channel.importance = NotificationManager.IMPORTANCE_LOW

//            .setContentIntent(contentIntent)
//        notificationManager.createNotificationChannel(channel)
        val notificationBuilder: Notification.Builder = Notification.Builder(
            this@DownloadService,
            nChannelId
        )
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("下载服务")
            .setContentText("正在运行")
        notificationManager.notify(nId, notificationBuilder.build())
        logI("通知栏已出")
    }

    inner class DownloadListenerImpl : DownloadListener {
        override fun progress(id: UUID, current: Long, total: Long) {
            binder.listener?.progress(id, current, total)
            appCacheRepository.updateDownloaderInfoCache(downloader.serializeDownloadTask())

            //TODO 添加下载进度通知
            val notificationBuilder: Notification.Builder = Notification.Builder(
                this@DownloadService,
                nChannelId
            )
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("下载中")
                .setProgress(((current + 1) / (total + 1)).toInt(), 100, false)
            notificationManager.notify(nId, notificationBuilder.build())
        }

        override fun success(id: UUID) {
            binder.listener?.success(id)
            appCacheRepository.updateDownloaderInfoCache(downloader.serializeDownloadTask())
        }

        override fun error(id: UUID, exception: IOException) {
            binder.listener?.error(id, exception)
            appCacheRepository.updateDownloaderInfoCache(downloader.serializeDownloadTask())
        }

        override fun pause(id: UUID) {
            binder.listener?.pause(id)
            appCacheRepository.updateDownloaderInfoCache(downloader.serializeDownloadTask())
        }

        override fun cancel(id: UUID) {
            binder.listener?.cancel(id)
            appCacheRepository.updateDownloaderInfoCache(downloader.serializeDownloadTask())
        }

    }
}