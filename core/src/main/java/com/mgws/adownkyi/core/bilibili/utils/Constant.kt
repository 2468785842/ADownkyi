package com.mgws.adownkyi.core.bilibili.utils

object Constant {
    data class Quality(val id: Int, val name: String)

    //获取支持的视频画质
    val RESOLUTIONS = listOf(
        Quality(16, "360P 流畅"),
        Quality(32, "480P 清晰"),
        Quality(64, "720P 高清"),
        Quality(74, "720P 60帧"),
        Quality(80, "1080P 高清"),
        Quality(112, "1080P 高码率"),
        Quality(116, "1080P 60帧"),
        Quality(120, "4K 超清"),
        Quality(125, "HDR 真彩"),
        Quality(126, "杜比视界"),
        Quality(127, "超高清 8K"),
    )

    // 获取视频编码代码
    val CODEC_IDS = listOf(
        Quality(7, "H.264/AVC"),
        Quality(12, "H.265/HEVC"),
        Quality(13, "AV1"),
    )

    // 获取支持的视频音质
    val QUALITIES = listOf(
        //Quality(30216, "64K"),
        //Quality(30232, "132K"),
        //Quality(30280, "192K"),
        Quality(30216, "低质量"),
        Quality(30232, "中质量"),
        Quality(30280, "高质量"),
        Quality(30250, "Dolby Atmos"),
        Quality(30251, "Hi-Res无损"),
    )

}