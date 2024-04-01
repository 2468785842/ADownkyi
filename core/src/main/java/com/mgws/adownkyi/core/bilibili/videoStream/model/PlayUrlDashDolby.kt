package com.mgws.adownkyi.core.bilibili.videoStream.model

import kotlinx.serialization.Serializable

@Serializable
data class PlayUrlDashDolby(
    val type: Int,
    val audio: List<PlayUrlDashVideo>?,
)
