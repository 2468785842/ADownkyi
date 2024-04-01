package com.mgws.adownkyi.core.bilibili.videoStream.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlayUrlSupportFormat(
    val quality: Int,
    val format: String,
    @SerialName("new_description") val newDescription: String,
    @SerialName("display_desc") val displayDesc: String,
    val superscript: String,
)
