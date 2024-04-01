package com.mgws.adownkyi.core.bilibili.video.model

import com.mgws.adownkyi.core.bilibili.model.Dimension
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoPage(
    val cid: Long,
    val page: Int,
    val from: String,
    val part: String,
    val duration: Long,
    val vid: String,
    val weblink: String,
    val dimension: Dimension,
    @SerialName("first_frame") val firstFrame: String? = null,
)
