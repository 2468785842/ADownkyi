package com.mgws.adownkyi.core.bilibili.video.model

import com.mgws.adownkyi.core.bilibili.model.Dimension
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UgcArc(
    val aid: Long,
    val videos: Int,
    @SerialName("type_id") val typeId: Int,
    @SerialName("type_name") val typeName: String,
    val copyright: Int,
    val pic: String,
    val title: String,
    val pubdate: Long,
    val ctime: Long,
    val desc: String,
    val state: Int,
    val duration: Long,
    val author: VideoOwner,
    val stat: VideoStat,
    val dynamic: String,
    val dimension: Dimension,
)
