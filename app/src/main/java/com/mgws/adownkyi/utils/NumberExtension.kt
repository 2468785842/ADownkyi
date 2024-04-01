package com.mgws.adownkyi.utils

import com.mgws.adownkyi.ADownKyiApplication

fun Number.byteAbbrev(): Pair<Float, String?> = this.toFloat().byteAbbrev()

/**
 * 为什么和Android文件管理器计算的文件大小不一样, 一个是/1000, 一个是/1024
 */
fun Float.byteAbbrev(): Pair<Float, String?> = when {
    // < M
    (this / (1024 * 1024)).toInt() != 0 -> this.byteToMByte() to "MB"
    // < K
    (this / 1024).toInt() != 0 -> this.byteToKByte() to "KB"
    else -> this to ""
}

fun Float.byteToKByte(): Float = (this / 1024).toFixed(1)
fun Float.byteToMByte(): Float = (this.byteToKByte() / 1024).toFixed(1)

/**
 * @param bit 保留小数点后bit位
 */
fun Float.toFixed(bit: Int): Float = (this * (10 * bit)).toInt() / (10F * bit)

fun Long.toWordNumber(): String =
    when (ADownKyiApplication.context.resources.configuration.locales[0].language) {
        "zh" -> when {
            this > 9999_9999L -> "${(this / 1_0000_0000F).toFixed(1)}亿"
            this > 9999L -> "${(this / 1_0000F).toFixed(1)}万"
            else -> this.toString()
        }

        else -> when {
            this > 999_999_999L -> "${(this / 1_000_000_000F).toFixed(1)}B"
            this > 999_999L -> "${(this / 1_000_000F).toFixed(1)}M"
            this > 999L -> "${(this / 1_000F).toFixed(1)}K"
            else -> this.toString()
        }
    }

fun Long.durationFormat() = when {
    this / 60 > 0 -> {
        if (this / 60 / 60 > 0)
            "${this / 60 / 60}h${this / 60 % 60}m${this % 60}s"
        "${this / 60}m${this % 60}s"
    }

    else -> "${this}s"
}
