package com.mgws.adownkyi.core.downloader

import android.util.Log
import com.mgws.adownkyi.core.bilibili.utils.ParseEntrance
import com.mgws.adownkyi.core.bilibili.video.VideoInfoService
import com.mgws.adownkyi.core.bilibili.videoStream.VideoStreamService
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.util.UUID
import kotlin.concurrent.thread

@RunWith(PowerMockRunner::class)
@PrepareForTest(Log::class)
class BuiltinDownloaderTest {

    @Before
    fun before() {
        PowerMockito.mockStatic(Log::class.java)

        PowerMockito.`when`(Log.i(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
            .then {
                println("${it.arguments[0]}, ${it.arguments[1]}")
                null
            }
        PowerMockito.`when`(Log.e(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
            .then {
                println("${it.arguments[0]}, ${it.arguments[1]}")
                null
            }
        PowerMockito.`when`(
            Log.e(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(Throwable::class.java)
            )
        ).then {
            println("${it.arguments[0]}, ${it.arguments[1]}")
            null
        }
        PowerMockito.`when`(Log.w(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
            .then {
                println("${it.arguments[0]}, ${it.arguments[1]}")
                null
            }

    }
    fun Number.byteAbbrev(): Pair<Float, String?> = this.toFloat().byteAbbrev()

    fun Float.byteAbbrev(): Pair<Float, String?> = when {
        // < M
        (this / (1024 * 1024)).toInt() != 0 -> this.byteToMByte() to "MB"
        // < K
        (this / 1024).toInt() != 0 -> this.byteToKByte() to "KB"
        else -> this to ""
    }

    fun Float.byteToKByte(): Float = (this / 1024).toFixed(1)
    fun Float.byteToMByte(): Float = (this.byteToKByte() / 1024).toFixed(1)

    /**
     * @param bit 保留小数点后bit位
     */
    fun Float.toFixed(bit: Int): Float = (this * (10 * bit)).toInt() / (10F * bit)
    @Test
    fun testDownload() {

        val builtinDownloader = BuiltinDownloader("D:\\temp")

        builtinDownloader.setListener(object : DownloadListener {
            override fun progress(id: UUID, current: Long, total: Long) {
                println("$id: ${current.toFloat().byteAbbrev()}/${total.toFloat().byteAbbrev()}")
            }

            override fun success(id: UUID) {
                println("$id: success")
            }

            override fun error(id: UUID) {
                println("$id: error")
            }

            override fun pause(id: UUID) {
                println("$id: pause")
            }

            override fun cancel(id: UUID) {
                println("$id: cancel")
            }
        })

        val service = thread {
            runBlocking {
                launch {
                    builtinDownloader.start()
                }
            }
        }

        thread {

            runBlocking {

                val aid: String = ParseEntrance.getBvId(
                    "https://www.bilibili.com/video/BV1u741177K9/?p=4&vd_source=18d2497a065f7d879d1602ff0caf8cf5"
                )
                val videoView = VideoInfoService().videoViewInfo(aid)!!
                val stream = VideoStreamService()

                for (video in videoView.pages.subList(7, 8)) {
                    val playUrl = stream.getVideoPlayUrl(videoView.aid, videoView.bvid, video.cid)!!

                    val taskId = builtinDownloader.addTask(
                        playUrl.dash.video[0].baseUrl,
                        "${video.part}.mp4"
                    )
                }
            }
        }
        service.join()
    }

}