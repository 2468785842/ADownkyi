package com.mgws.adownkyi.core.bilibili.videoStream.model

import kotlinx.serialization.Serializable

@Serializable
data class PlayUrlDash(
    val duration: Long,
    val video: List<PlayUrlDashVideo>,
    val audio: List<PlayUrlDashVideo>,
    val dolby: PlayUrlDashDolby,
    val flac: PlayUrlDashFlac?,
)
