package com.mgws.adownkyi.core.bilibili.video.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UgcSeason(
    val id: Long,
    val title: String,
    val cover: String,
    val mid: Long,
    val intro: String,
    @SerialName("sign_state") val signState: Int,
    val attribute: Int,
    val sections: List<UgcSection>,
    val stat: UgcStat,
    @SerialName("ep_count") val epCount: Int,
    @SerialName("season_type") val seasonType: Int,
)
