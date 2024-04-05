package com.mgws.adownkyi.core.bilibili.utils

object Constant {
    data class Info(val id: Int, val name: String)

    //获取支持的视频画质
    val RESOLUTIONS = listOf(
        Info(16, "360P 流畅"),
        Info(32, "480P 清晰"),
        Info(64, "720P 高清"),
        Info(74, "720P 60帧"),
        Info(80, "1080P 高清"),
        Info(112, "1080P 高码率"),
        Info(116, "1080P 60帧"),
        Info(120, "4K 超清"),
        Info(125, "HDR 真彩"),
        Info(126, "杜比视界"),
        Info(127, "超高清 8K"),
    )

    // 获取视频编码代码
    val CODEC_IDS = listOf(
        Info(7, "H.264/AVC"),
        Info(12, "H.265/HEVC"),
        Info(13, "AV1"),
    )

    // 获取支持的视频音质
    val QUALITIES = listOf(
        //Quality(30216, "64K"),
        //Quality(30232, "132K"),
        //Quality(30280, "192K"),
        Info(30216, "低质量"),
        Info(30232, "中质量"),
        Info(30280, "高质量"),
        Info(30250, "Dolby Atmos"),
        Info(30251, "Hi-Res无损"),
    )

    val AUTH_ERROR = listOf(
        Info(-1, "应用程序不存在或已被封禁"),
        Info(-2, "Access Key 错误"),
        Info(-3, "API 校验密匙错误"),
        Info(-4, "调用方对该 Method 没有权限"),
        Info(-101, "账号未登录"),
        Info(-102, "账号被封停"),
        Info(-103, "积分不足"),
        Info(-104, "硬币不足"),
        Info(-105, "验证码错误"),
        Info(-106, "账号非正式会员或在适应期"),
        Info(-107, "应用不存在或者被封禁"),
        Info(-108, "未绑定手机"),
        Info(-110, "未绑定手机"),
        Info(-111, "csrf 校验失败"),
        Info(-112, "系统升级中"),
        Info(-113, "账号尚未实名认证"),
        Info(-114, "请先绑定手机"),
        Info(-115, "请先完成实名认证"),
    )

    val ERRORS = listOf(
        Info(-304, "木有改动"),
        Info(-307, "撞车跳转"),
        Info(-400, "请求错误"),
        Info(-401, "未认证 (或非法请求)"),
        Info(-403, "访问权限不足"),
        Info(-404, "啥都木有"),
        Info(-405, "不支持该方法"),
        Info(-409, "冲突"),
        Info(-412, "请求被拦截 (客户端 ip 被服务端风控)"),
        Info(-500, "服务器错误"),
        Info(-503, "过载保护,服务暂不可用"),
        Info(-504, "服务调用超时"),
        Info(-509, "超出限制"),
        Info(-616, "上传文件不存在"),
        Info(-617, "上传文件太大"),
        Info(-625, "登录失败次数太多"),
        Info(-626, "用户不存在"),
        Info(-628, "密码太弱"),
        Info(-629, "用户名或密码错误"),
        Info(-632, "操作对象数量限制"),
        Info(-643, "被锁定"),
        Info(-650, "用户等级太低"),
        Info(-652, "重复的用户"),
        Info(-658, "Token 过期"),
        Info(-662, "密码时间戳过期"),
        Info(-688, "地理区域限制"),
        Info(-689, "版权限制"),
        Info(-701, "扣节操失败"),
        Info(-799, "请求过于频繁，请稍后再试"),
        Info(-8888, "对不起，服务器开小差了~ (ಥ﹏ಥ)"),
    )
}