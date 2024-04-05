package com.mgws.adownkyi.core.bilibili.login

import com.mgws.adownkyi.core.bilibili.HttpClient
import com.mgws.adownkyi.core.bilibili.HttpConfig
import com.mgws.adownkyi.core.bilibili.Result
import com.mgws.adownkyi.core.bilibili.WbiSign
import com.mgws.adownkyi.core.bilibili.login.model.Captcha
import com.mgws.adownkyi.core.bilibili.login.model.WebSmsLogin
import com.mgws.adownkyi.core.bilibili.utils.Constant.AUTH_ERROR
import com.mgws.adownkyi.core.utils.logD
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.BufferedInputStream
import java.io.IOException

class LoginService {

    @Serializable
    data class CaptchaKey(
        @SerialName("captcha_key") val captchaKey: String,
    )

    /**
     * 网页登录, 返回Cookie
     */
    suspend fun smsLogin(tel: String, code: String, captchaKey: String): Result<List<String>> {
        val url = "https://passport.bilibili.com/x/passport-login/web/login/sms"
        val connect = HttpClient.getHttpConnection(
            "POST", url, HttpConfig.BiliBiliHttpConfig
        )
        val result: Result<List<String>> = withContext(Dispatchers.IO) {
            try {
                // 写入表单数据
                connect.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                connect.doOutput = true
                connect.outputStream.use {
                    it.write(
                        WbiSign.getUrlParam(
                            WbiSign.encodeWbi(
                                "cid" to 86,
                                "tel" to tel,
                                "code" to code,
                                "source" to "main-fe-header",
                                "captcha_key" to captchaKey,
                                "keep" to true
                            )
                        ).toByteArray()
                    )
                    it.flush()
                }

                // 获取Cookie
                val cookies =
                    connect.headerFields.filter { it.key == "Set-Cookie" }.flatMap { it.value }

                if (cookies.isNotEmpty()) {
                    return@withContext Result.Success(cookies)
                }

                connect.inputStream.use {
                    val encodeStream = HttpClient.getInputStreamAsEncoding(connect)
                    val responseBody =
                        String(BufferedInputStream(encodeStream).readBytes(), Charsets.UTF_8)

                    logD("responseBody: $responseBody")

                    val pojo: HttpClient.HttpResult<WebSmsLogin> = try {
                        HttpClient.json.decodeFromString(responseBody)
                    } catch (e: Exception) {
                        return@withContext Result.Failure(e)
                    } finally {
                        connect.disconnect()
                    }

                    AUTH_ERROR.find { it.id == pojo.code }?.let { info ->
                        logD("Auth Error: ${info.name}")
                        return@withContext Result.Failure(Exception(info.name))
                    }
                }

            } catch (e: IOException) {
                return@withContext Result.Failure(e)
            } finally {
                connect.disconnect()
            }
            return@withContext Result.Failure(Exception("未知错误"))
        }
        return result
    }

    suspend fun sendSms(
        tel: String, recaptchaToken: String, geeChallenge: String,
        geeValidate: String, geeSeccode: String,
    ): Result<CaptchaKey> {

        val url = "https://passport.bilibili.com/x/passport-login/web/sms/send"
        return HttpClient.post<CaptchaKey>(
            url,
            WbiSign.encodeWbi(
                "cid" to 86,
                "tel" to tel,
                "source" to "main-fe-header",
                "token" to recaptchaToken,
                "challenge" to geeChallenge,
                "validate" to geeValidate,
                "seccode" to geeSeccode,
            )
        )
    }

    suspend fun getCaptcha(): Result<Captcha> {
        val url = "https://passport.bilibili.com/x/passport-login/captcha?source=main-fe-header"
        return HttpClient.get<Captcha>(url)
    }

}