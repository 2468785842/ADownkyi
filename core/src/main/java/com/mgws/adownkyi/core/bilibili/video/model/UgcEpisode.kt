package com.mgws.adownkyi.core.bilibili.video.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UgcEpisode(
    @SerialName("season_id") val seasonId: Long,
    @SerialName("section_id") val sectionId: Long,
    val id: Long,
    val aid: Long,
    val cid: Long,
    val title: String,
    val attribute: Int,
    val arc: UgcArc,
    val page: VideoPage,
    val bvid: String,
)
