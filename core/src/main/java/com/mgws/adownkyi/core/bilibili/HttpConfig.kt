package com.mgws.adownkyi.core.bilibili

val BiliBiliHttpConfig = HttpConfig("https://www.bilibili.com", contentType = "application/json")


data class HttpConfig(
    private val referer: String? = null,
    private val origin: String? = referer,
    private val range: String? = null,
    private val userAgent: String =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36 Edg/121.0.0.0",
    private val acceptLanguage: String = "en-US,en;q=0.9",
    private val acceptEncoding: String = "gzip,deflate,br",
    private val contentType: String? = null,
) {
    fun addHeader(block: (String, String) -> Unit) {
        block("User-Agent", userAgent)
        referer?.let { block("Referer", it) }
        origin?.let { block("Origin", it) }
        block("Accept-Language", acceptLanguage)
        block("Accept-Encoding", acceptEncoding)
        contentType?.let { block("Content-Type", it) }
        range?.let { block("Range", it) }
    }
}
