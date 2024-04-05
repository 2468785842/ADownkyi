package com.mgws.adownkyi.repo

import com.mgws.adownkyi.core.bilibili.HttpConfig
import com.mgws.adownkyi.core.bilibili.Result
import com.mgws.adownkyi.core.bilibili.login.LoginService
import com.mgws.adownkyi.core.bilibili.login.model.Captcha
import com.mgws.adownkyi.core.utils.logD
import com.mgws.adownkyi.core.utils.logE
import com.mgws.adownkyi.core.utils.logI
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginRepository @Inject constructor(
    private val loginService: LoginService,
    private val appCacheRepository: AppCacheRepository,
) {

    /**
     * first challenge
     * second validate
     * three seccode
     */
    var machineVerification: Triple<String, String, String>? = null

    var verification = MutableStateFlow<Boolean?>(null)

    private var token: String? = null

    suspend fun getCaptcha(): Captcha? =
        when (val captcha = loginService.getCaptcha()) {
            is Result.Success -> {
                token = captcha.data.token
                captcha.data
            }
            // Noting
            is Result.Failure -> null
        }

    suspend fun sendSms(phoneNumber: String): String? {
        if (verification.value == null) return null
        if (verification.value!!) {
            val (challenge, validate, seccode) = machineVerification!!
            logD("sendSms: $token, $challenge, $validate, $seccode")
            when (val sendSms =
                loginService.sendSms(phoneNumber, token!!, challenge, validate, seccode)
            ) {
                is Result.Success -> {
                    logI("sendSms: ${sendSms.data}")
                    return sendSms.data.captchaKey
                }

                is Result.Failure -> {
                    logE("sendSms", sendSms.throwable)
                }
            }
        }
        return null
    }

    suspend fun login(phoneNumber: String, smsCode: String, captchaKey: String): Boolean {
        when (val smsLogin = loginService.smsLogin(phoneNumber, smsCode, captchaKey)) {
            is Result.Success -> {
                logD("Cookies: ${smsLogin.data}")
                HttpConfig.BiliBiliHttpConfig.cookies = smsLogin.data
                appCacheRepository.updateCookies(smsLogin.data)
                return true
            }

            is Result.Failure -> {
                return false
            }
        }
    }

}