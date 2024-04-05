package com.mgws.adownkyi.core.bilibili.login.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WebSmsLogin(
    @SerialName("is_new") val isNew: Boolean,
    val status: Int,
    val url: String,
)
