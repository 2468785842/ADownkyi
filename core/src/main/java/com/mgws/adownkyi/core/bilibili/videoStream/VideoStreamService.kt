package com.mgws.adownkyi.core.bilibili.videoStream

import com.mgws.adownkyi.core.bilibili.HttpClient
import com.mgws.adownkyi.core.bilibili.Result
import com.mgws.adownkyi.core.bilibili.WbiSign
import com.mgws.adownkyi.core.bilibili.videoStream.model.PlayUrl
import com.mgws.adownkyi.core.bilibili.videoStream.model.PlayerV2
import com.mgws.adownkyi.core.utils.logE

class VideoStreamService {

    /* 获取播放器信息（web端） */
    suspend fun playerV2(
        avid: Long, bvid: String, cid: Long,
    ): PlayerV2? {

        val url = "https://api.bilibili.com/x/player/wbi/playurl"

        val result = HttpClient.get<PlayerV2>(
            url, WbiSign.encodeWbi(
                "avid" to avid,
                "bvid" to bvid,
                "cid" to cid,
            )
        )

        return when (result) {
            is Result.Success -> result.data
            is Result.Failure -> {
                logE("playerV2", result.throwable)
                null
            }
        }
    }

    /* 获取所有字幕 */
    fun getSubtitle(
        avid: Long, bvid: String, cid: Long,
    ): Nothing = TODO("获取所有字幕,待实现")

    /* 获取普通视频的视频流 */
    suspend fun getVideoPlayUrl(
        aid: Long,
        bvid: String,
        cid: Long,
        quality: Int = 125,
    ): PlayUrl? {

        val url = "https://api.bilibili.com/x/player/wbi/playurl"

        val result = HttpClient.get<PlayUrl>(
            url, WbiSign.encodeWbi(
                "from_client" to "BROWSER",
                "fourk" to 1,
                "fnver" to 0,
                "fnval" to 4048,
                "cid" to cid,
                "qn" to quality,
                "bvid" to bvid,
                "aid" to aid
            )
        )

        return when (result) {
            is Result.Success -> result.data
            is Result.Failure -> {
                logE("getVideoPlayUrl", result.throwable)
                null
            }
        }
    }


    /* 获取番剧视频流 */
    suspend fun getBangumiPlayUrl(
        aid: Long, bvid: String, cid: Long, quality: Int = 125,
    ): PlayUrl? {
        val url = "https://api.bilibili.com/pgc/player/web/playurl"

        val result = HttpClient.get<PlayUrl>(
            url, WbiSign.encodeWbi(
                "cid" to cid,
                "qn" to quality,
                "fourk" to 1,
                "fnver" to 0,
                "fnval" to 4048,
                "bvid" to bvid,
                "aid" to aid
            )
        )

        return when (result) {
            is Result.Success -> result.data
            is Result.Failure -> {
                logE("getBangumiPlayUrl", result.throwable)
                null
            }
        }
    }

    /* 获取课程的视频流 */
    suspend fun getCheesePlayUrl(
        aid: Long, bvid: String, cid: Long, episodeId: Long, quality: Int = 125,
    ): PlayUrl? {
        val url = "https://api.bilibili.com/pugv/player/web/playurl"

        val result = HttpClient.get<PlayUrl>(
            url, WbiSign.encodeWbi(
                "cid" to cid,
                "qn" to quality,
                "fourk" to 1,
                "fnver" to 0,
                "fnval" to 4048,
                "bvid" to bvid,
                "aid" to aid,
                "ep_id" to episodeId
            )
        )

        return when (result) {
            is Result.Success -> result.data
            is Result.Failure -> {
                logE("getCheesePlayUrl", result.throwable)
                null
            }
        }
    }

}