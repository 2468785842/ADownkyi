package com.mgws.adownkyi.core.bilibili.videoStream.model

import kotlinx.serialization.SerialName

data class SubtitleInfo(
    @SerialName("allow_submit") val allowSubmit: Boolean,
    val lan: String,
    @SerialName("lan_doc") val lanDoc: String,
    val subtitles: List<Subtitle>? = emptyList(),
)
