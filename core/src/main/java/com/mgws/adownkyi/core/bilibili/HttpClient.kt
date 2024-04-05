package com.mgws.adownkyi.core.bilibili

import com.mgws.adownkyi.core.utils.logD
import com.mgws.adownkyi.core.utils.logE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.brotli.dec.BrotliInputStream
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.DeflaterInputStream
import java.util.zip.GZIPInputStream

object HttpClient {

    val json = Json {
        ignoreUnknownKeys = true
    }

    @Serializable
    data class HttpResult<T>(
        val code: Int,
        val message: String,
        val ttl: Int,
        val data: T? = null,
    )

    suspend inline fun <reified T> get(url: String, params: Map<String, String> = emptyMap()) =
        doResponse<T>("GET", url, params)

    suspend inline fun <reified T> post(url: String, params: Map<String, String> = emptyMap()) =
        doResponse<T>("POST", url, params)

    fun getHttpConnection(
        method: String,
        url: String,
        httpConfig: HttpConfig,
        timeout: Int = 3000,
    ): HttpURLConnection {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.connectTimeout = timeout
        connection.requestMethod = method
        httpConfig.addHeader(connection::addRequestProperty)
        return connection
    }

    fun getInputStreamAsEncoding(httpConnect: HttpURLConnection): InputStream =
        when (httpConnect.contentEncoding) {
            "gzip" -> GZIPInputStream(httpConnect.inputStream)
            "deflate" -> DeflaterInputStream(httpConnect.inputStream)
            "br" -> BrotliInputStream(httpConnect.inputStream)
            else -> httpConnect.inputStream
        }

    /* 处理响应数据 */
    @OptIn(ExperimentalSerializationApi::class)
    suspend inline fun <reified T> doResponse(
        method: String, url: String,
        params: Map<String, String>,
    ): Result<T> {

        val httpRequest = try {
            withContext(Dispatchers.Default) {
                json.decodeFromString<HttpResult<T>>(
                    request(method, HttpConfig.BiliBiliHttpConfig, url, params)
                )
            }
        } catch (e: IOException) {
            logE("连接网络失败", e)
            return Result.Failure(e)
        } catch (e: MissingFieldException) {
            logE("与期望的数据不一致!", e)
            return Result.Failure(e)
        }

        logD("httpRequest: $httpRequest")

        return when (httpRequest.code) {
            0 -> Result.Success<T>(httpRequest.data!!)
            else -> Result.Failure(Throwable(httpRequest.message))
        }
    }

    /**
     * 发送请求
     *
     * @param method 请求方法
     * @param url url连接
     * @param params 参数
     *
     * @throws IOException 读写错误
     */
    @Throws(IOException::class)
    suspend fun request(
        method: String,
        httpConfig: HttpConfig,
        url: String, params: Map<String, String> = emptyMap(),
    ): String = withContext(Dispatchers.IO) {

        val tUrl = if (params.isEmpty() || method == "POST") url
        else "$url?${WbiSign.getUrlParam(params)}"

        logD("-".repeat(20))
        logD("request url: $tUrl")
        logD("request method: $method")

        val httpConnect = getHttpConnection(
            method, tUrl, httpConfig
        )

        if (method == "POST") {
            httpConnect.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            httpConnect.doOutput = true
            httpConnect.outputStream.use {
                it.write(WbiSign.getUrlParam(params).toByteArray())
                it.flush()
            }
        }

        logD("code: ${httpConnect.responseCode}")
        logD("encode: ${httpConnect.contentEncoding}")

        //创建对应的压缩算法io流
        val inputStream = getInputStreamAsEncoding(httpConnect)

        try {
            return@withContext String(BufferedInputStream(inputStream).readBytes(), Charsets.UTF_8)
        } finally {
            inputStream.close()
            httpConnect.disconnect()
        }
    }

}