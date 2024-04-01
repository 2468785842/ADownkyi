package com.mgws.adownkyi.core.bilibili.videoStream.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlayUrlDashVideo(
    val id: Int,
    @SerialName("base_url") val baseUrl: String,
    @SerialName("backup_url") val backupUrl: List<String>?,
    @SerialName("mime_type") val mimeType: String,
    val codecs: String,
    val width: Int,
    val height: Int,
    @SerialName("frame_rate") val frameRate: String,
    @SerialName("codecid") val codecId: Int? = null,
)
