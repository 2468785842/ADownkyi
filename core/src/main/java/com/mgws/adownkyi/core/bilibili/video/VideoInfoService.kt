package com.mgws.adownkyi.core.bilibili.video

import com.mgws.adownkyi.core.bilibili.HttpClient
import com.mgws.adownkyi.core.bilibili.Result
import com.mgws.adownkyi.core.bilibili.WbiSign
import com.mgws.adownkyi.core.bilibili.video.model.SeasonsArchivesList
import com.mgws.adownkyi.core.bilibili.video.model.VideoPage
import com.mgws.adownkyi.core.bilibili.video.model.VideoView
import com.mgws.adownkyi.core.utils.logE
import com.mgws.adownkyi.core.utils.logW

class VideoInfoService {

    /* 获取视频详细信息(web端) */
    suspend fun videoViewInfo(bvid: String = "", aid: Long = -1L) =
        fetchData<VideoView>(
            "https://api.bilibili.com/x/web-interface/wbi/view",
            bvid, aid
        )

    /* 获取视频简介 */
    suspend fun videoDescription(bvid: String = "", aid: Long = -1L) =
        fetchData<String>(
            "https://api.bilibili.com/x/web-interface/archive/desc",
            bvid, aid
        )

    /* 查询视频分P列表 (avid/bvid转cid) */
    suspend fun videoPageList(bvid: String = "", aid: Long = -1L) =
        fetchData<List<VideoPage>>(
            "https://api.bilibili.com/x/player/pagelist",
            bvid, aid
        )

    suspend fun videoSeasonList(mid: Long, seasonId: Long): SeasonsArchivesList? {
        val url = "https://api.bilibili.com/x/polymer/web-space/seasons_archives_list"

        val result = HttpClient.get<SeasonsArchivesList>(
            url, WbiSign.encodeWbi(
                "mid" to mid,
                "season_id" to seasonId,
                "sort_reverse" to false,
            )
        )
        return when (result) {
            is Result.Success -> result.data
            is Result.Failure -> {
                logE("videoSeasonList", result.throwable)
                null
            }
        }
    }


    private suspend inline fun <reified T> fetchData(
        url: String,
        bvid: String,
        aid: Long,
    ): T? {
        if (bvid.isEmpty() && aid == -1L) {
            logW("only one of bvid and aid cant be empty")
            return null
        }

        val result = HttpClient.get<T>(
            url, WbiSign.encodeWbi(
                "bvid" to bvid,
                "aid" to aid.toString()
            )
        )

        return when (result) {
            is Result.Success -> result.data
            is Result.Failure -> {
                logE("fetchData", result.throwable)
                null
            }
        }

    }

}