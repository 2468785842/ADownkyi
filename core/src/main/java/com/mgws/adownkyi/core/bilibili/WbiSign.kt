package com.mgws.adownkyi.core.bilibili

import com.mgws.adownkyi.core.utils.byteArrayToHexString
import java.net.URLEncoder
import java.security.MessageDigest
import java.time.Clock
import java.time.LocalDateTime


object WbiSign {

    /* 打乱重排实时口令 */
    private fun getMixinKey(origin: String): String {
        if (origin.isEmpty()) return ""

        val mixinKeyEncTab = listOf(
            46, 47, 18, 2, 53, 8, 23, 32, 15, 50, 10, 31, 58, 3, 45, 35, 27, 43, 5, 49,
            33, 9, 42, 19, 29, 28, 14, 39, 12, 38, 41, 13, 37, 48, 7, 16, 24, 55, 40,
            61, 26, 17, 0, 1, 60, 51, 30, 4, 22, 25, 54, 21, 56, 59, 6, 63, 57, 62, 11,
            36, 20, 34, 44, 52
        )

        val temp = StringBuilder()
        for (i in mixinKeyEncTab.indices) {
            temp.append(origin[i])
        }
        return temp.substring(0, 32)
    }

    fun encodeWbi(
        vararg params: Pair<String, Any>,
        imgKey: String = "",
        subKey: String = "",
    ): Map<String, String> {
        val paraStr =
            HashMap(mapOf(*params.map { it.first to it.second.toString() }.toTypedArray()))

        val mixinKey: String = getMixinKey(imgKey + subKey)
        val currTime: String = LocalDateTime.now(Clock.systemUTC()).second.toString()

        //添加 wts 字段
        paraStr["wts"] = currTime
        //过滤 value 中的 "!'()*" 字符
        paraStr.forEach {
            it.value.filter { c ->
                "!'()*".contains(c)
            }
        }

        // 序列化参数
        val query: String = URLEncoder.encode(getUrlParam(paraStr), "UTF-8")
        val md5 = MessageDigest.getInstance("MD5")
        val digest = md5.digest((query + mixinKey).toByteArray(Charsets.UTF_8))
        val wbiSign = digest.byteArrayToHexString().lowercase()
        paraStr["w_rid"] = wbiSign

        return paraStr
    }

    fun getUrlParam(params: Map<String, String>): String {
        val paramStr = StringBuilder()
        for ((k, v) in params) {
            paramStr.append("$k=$v&")
        }
        return paramStr.substring(0, paramStr.length - 1)
    }
}