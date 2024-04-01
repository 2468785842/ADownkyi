package com.mgws.adownkyi.core.bilibili.model

import kotlinx.serialization.Serializable

@Serializable
data class Dimension(
    val width: Int,
    val height: Int,
    val rotate: Int,
)
