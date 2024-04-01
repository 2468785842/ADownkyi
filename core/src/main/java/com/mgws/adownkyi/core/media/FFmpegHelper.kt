package com.mgws.adownkyi.core.media

//object FFmpegHelper : IMediaHelper {
//
//    /* 合并音视频 */
//    override fun merge(audio: String, video: String, destVideo: String): Boolean {
//
//        val param =
//            "-y -i \"$audio\" -i \"$video\" -strict -2 -acodec copy -vcodec copy -f mp4 \"$destVideo\""
//
//        File(destVideo).deleteOnExit()
//
//        FFmpegKit.execute(param).let {
//            if (it.returnCode.isValueError) {
//                logI(
//                    """合并视频失败, 状态:${it.state},
//                       返回值:${it.returnCode},
//                       堆栈信息:${it.failStackTrace}""".trimMargin()
//                )
//                return false
//            }
//        }
//
//        File(audio).delete()
//        File(video).delete()
//
//        return true
//    }
//
//
//}