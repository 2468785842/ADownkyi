package com.mgws.adownkyi

import com.geetest.sdk.GT3ConfigBean
import com.geetest.sdk.GT3ErrorBean
import com.geetest.sdk.GT3GeetestUtils
import com.geetest.sdk.GT3Listener
import com.mgws.adownkyi.core.utils.logD
import com.mgws.adownkyi.repo.LoginRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject


class CustomGT3Listener(
    private val loginRepository: LoginRepository,
    private val gt3ConfigBean: GT3ConfigBean,
    private val gt3GeetestUtils: GT3GeetestUtils,
) : GT3Listener(), CoroutineScope by CoroutineScope(Dispatchers.IO) {

    /**
     * 验证码加载完成
     * @param duration 加载时间和版本等信息，为json格式
     */
    override fun onReceiveCaptchaCode(duration: Int) {
        logD("the captcha is loaded")
    }

    override fun onStatistics(code: String) {
    }

    /**
     * 验证码被关闭
     * @param num 1 点击验证码的关闭按钮来关闭验证码, 2 点击屏幕关闭验证码, 3 点击返回键关闭验证码
     */
    override fun onClosed(num: Int) {
        logD("the captcha is closed")
    }

    /**
     * 验证成功回调
     * @param result
     */
    override fun onSuccess(result: String) {
        logD("the captcha validate success: $result")
    }

    /**
     * 验证失败回调
     * @param errorBean 版本号，错误码，错误描述等信息
     */
    override fun onFailed(errorBean: GT3ErrorBean) {
        logD("the captcha validate failed: ${errorBean.errorDesc}")
    }

    override fun onDialogResult(result: String) {
        val jsonObject = JSONObject(result)
        val challenge: String? = jsonObject.optString("geetest_challenge")
        val seccode: String? = jsonObject.optString("geetest_seccode")
        val validate: String? = jsonObject.optString("geetest_validate")
        if (challenge == null || seccode == null || validate == null) {
            loginRepository.verification.value = false
            gt3GeetestUtils.showFailedDialog()
        } else {
            loginRepository.machineVerification = Triple(challenge, validate, seccode)
            loginRepository.verification.value = true
            gt3GeetestUtils.showSuccessDialog()
        }
    }

    /**
     * 自定义api1回调
     */
    override fun onButtonClick() {
        launch {
            val captcha = loginRepository.getCaptcha()
            if (captcha != null) {
                gt3ConfigBean.api1Json = JSONObject(
                    """{
                            "success": 1,
                            "challenge": "${captcha.geetest.challenge}",
                            "gt": "${captcha.geetest.gt}",
                            "new_captcha": true
                        }""".trimIndent()
                )
            } else {
                gt3ConfigBean.api1Json = null
            }
            gt3GeetestUtils.getGeetest()
        }
    }

}