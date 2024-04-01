package com.mgws.adownkyi.core.bilibili.videoStream.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlayUrlDurl(
    val order: Int,
    val length: Long,
    val size: Long,
    val url: String,
    @SerialName("backup_url") val backupUrl: List<String>?,
)
