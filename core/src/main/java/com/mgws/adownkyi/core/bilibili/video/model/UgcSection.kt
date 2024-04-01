package com.mgws.adownkyi.core.bilibili.video.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UgcSection(
    @SerialName("season_id") val seasonId: Long,
    val id: Long,
    val title: String,
    val type: Int,
    val episodes: List<UgcEpisode>,
)
