package com.mgws.adownkyi.core.bilibili.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Login(
    val isLogin: Boolean,
    @SerialName("wbi_img") val wbiImg: WbiImg,
) {
    @Serializable
    data class WbiImg(
        @SerialName("img_url") val imgUrl: String,
        @SerialName("sub_url") val subUrl: String,
    )

}