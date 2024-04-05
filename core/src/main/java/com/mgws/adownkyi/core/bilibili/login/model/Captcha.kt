package com.mgws.adownkyi.core.bilibili.login.model

import kotlinx.serialization.Serializable

/**
 * @property geetest    极验captcha数据
 * @property token    登录 API token	与 captcha 无关，与登录接口有关
 * @property type    验证方式 用于判断使用哪一种验证方式，目前所见只有极验
 */
@Serializable
data class Captcha(
    val geetest: Geetest,
    val token: String,
    val type: String,
) {
    /**
     * @property gt 极验id	一般为固定值
     * @property challenge 极验KEY	由B站后端产生用于人机验证
     */
    @Serializable
    data class Geetest(
        val gt: String,
        val challenge: String,
    )
}
