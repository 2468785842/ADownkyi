package com.mgws.adownkyi.core.bilibili.videoStream.model

import kotlinx.serialization.SerialName

data class Subtitle(
    val id: Long,
    val lan: String,
    @SerialName("lan_doc") val lanDoc: Boolean,
    @SerialName("is_lock") val isLock: Boolean,
    @SerialName("author_mid") val authorMid: Long,
    @SerialName("subtitle_url") val subtitleUrl: String,
    val type: Int,
    @SerialName("id_str") val idStr: String,
)
