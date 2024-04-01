package com.mgws.adownkyi.core.bilibili.videoStream.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlayUrl(
    @SerialName("accept_description") val acceptDescription: List<String>,
    @SerialName("accept_quality") val acceptQuality: List<Int>,
    val durl: List<PlayUrlDurl>? = emptyList(),
    val dash: PlayUrlDash,
    @SerialName("support_formats") val supportFormats: List<PlayUrlSupportFormat>,
)
