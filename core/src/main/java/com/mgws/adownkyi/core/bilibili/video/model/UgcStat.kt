package com.mgws.adownkyi.core.bilibili.video.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UgcStat(
    @SerialName("season_id") val seasonId: Long,
    val view: Long,
    val danmaku: Long,
    val favorite: Long? = null,
    val coin: Long,
    val share: Long,
    @SerialName("now_rank") val nowRank: Long,
    @SerialName("his_rank") val hisRank: Long,
    val like: Long,
)
