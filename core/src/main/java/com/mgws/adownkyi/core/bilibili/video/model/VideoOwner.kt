package com.mgws.adownkyi.core.bilibili.video.model

import kotlinx.serialization.Serializable

/**
 * 用户信息
 */
@Serializable
data class VideoOwner(
    val mid: Long,
    val name: String,
    //用户头像url
    val face: String
)
