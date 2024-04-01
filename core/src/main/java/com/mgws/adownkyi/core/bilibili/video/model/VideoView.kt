package com.mgws.adownkyi.core.bilibili.video.model

import com.mgws.adownkyi.core.bilibili.model.Dimension
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class VideoView(
    val bvid: String,
    val aid: Long,
    val videos: Int,
    val tid: Int,
    val tname: String,
    val copyright: Int,
    //封面cover url
    val pic: String,
    val title: String,
    val pubdate: Long,
    val ctime: Long,
    val desc: String,
    val state: Int,
    val duration: Long,
    @SerialName("redirect_url") val redirectUrl: String? = null,
    @SerialName("mission_id") val missionId: Long? = null,
    val owner: VideoOwner,
    val stat: VideoStat,
    val dynamic: String,
    val cid: Long,
    val dimension: Dimension,
    @SerialName("season_id") val seasonId: Long? = null,
    @SerialName("festival_jump_url") val festivalJumpUrl: String? = null,
    val pages: List<VideoPage>,
    val subtitle: VideoSubtitle?,
    @SerialName("ugc_season") val ugcSeason: UgcSeason? = null,
)
