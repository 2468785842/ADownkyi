package com.mgws.adownkyi.core.bilibili

import com.mgws.adownkyi.core.utils.byteArrayToHexString
import java.security.MessageDigest


sealed class HttpConfig(
    private val referer: String? = null,
    private val origin: String? = referer,
    private val range: String? = null,
    private val userAgent: String =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36 Edg/121.0.0.0",
    private val acceptLanguage: String = "en-US,en;q=0.9",
    private val acceptEncoding: String = "gzip,deflate,br",
    private val contentType: String? = null,
    var cookies: List<String> = emptyList(),
) {
    open val headers: Map<String, String> = emptyMap()

    data object BiliBiliHttpConfig : HttpConfig(
        "https://www.bilibili.com",
        contentType = "application/json"
    ) {
        private val buvid = buvid()

        override val headers: Map<String, String> = mapOf(
            "APP-KEY" to "android64",
            "Buvid" to buvid,
            "authority" to "android641",
            "env" to "prod"
        )

        private fun buvid(): String {
            val md5 = MessageDigest.getInstance("MD5")
            val idMD5 = md5.digest(generateRandomMacAddress().toByteArray())

            val idE = mutableListOf<Byte>()
            val extract = arrayOf(2, 12, 22)
            for (index in extract) {
                idE.add(idMD5.getOrElse(index) { 0 })
            }
            return "XY${idE.toByteArray().byteArrayToHexString()}${idMD5.byteArrayToHexString()}"
        }

        private fun generateRandomMacAddress(): String {
            val macArr = mutableListOf<String>()

            for (i in 0..5) {
                macArr += Integer.parseInt((Math.random() * (0xff + 1)).toInt().toString())
                    .toString(16)
            }

            return macArr.joinToString(":")
        }
    }

    class BaseHttpConfig(
        range: String? = null,
        contentType: String? = null,
    ) : HttpConfig(
        range = range,
        contentType = contentType,
    )

    fun addHeader(block: (String, String) -> Unit) {
        block("User-Agent", userAgent)
        referer?.let { block("Referer", it) }
        origin?.let { block("Origin", it) }
        block("Accept-Language", acceptLanguage)
        block("Accept-Encoding", acceptEncoding)
        contentType?.let { block("Content-Type", it) }
        range?.let { block("Range", it) }
        cookies.forEach { block("Cookie", it) }
        headers.forEach { block(it.key, it.value) }
    }

}
