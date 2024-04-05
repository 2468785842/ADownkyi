package com.mgws.adownkyi.core.bilibili.video.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoSubtitle(
    @SerialName("allow_submit") val allowSubmit: Boolean,
    val list: List<Subtitle>? = emptyList(),
)

@Serializable
data class Subtitle(
    val id: Long,
    val lan: String,
    @SerialName("lan_doc") val lanDoc: String,
    @SerialName("is_lock") val isLock: Boolean,
    @SerialName("author_mid") val authorMid: Long? = 0,
    @SerialName("subtitle_mid") val subtitleMid: Long? = 0,
    @SerialName("subtitle_url") val subtitleUrl: String,
    val author: SubtitleAuthor,
)

@Serializable
data class SubtitleAuthor(
    val mid: Long,
    val name: String,
    val sex: String,
    val face: String,
    val sign: String,
)
