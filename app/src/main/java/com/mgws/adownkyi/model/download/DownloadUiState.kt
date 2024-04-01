package com.mgws.adownkyi.model.download

import com.mgws.adownkyi.core.bilibili.utils.Constant
import com.mgws.adownkyi.core.bilibili.videoStream.model.PlayUrlDashVideo

/**
 * @property avid 视频id
 * @property bvid 视频bvid
 * @property cid 视频cid
 * @property episodeId 视频episodeId
 * @property pageCoverUrl 视频页面封面url
 * @property order 视频序号
 * @property name 视频标题
 * @property duration 视频时长
 * @property videoCodecName 视频编码名称
 * @property resolution 视频画质
 * @property audioCodec 视频音质
 * @property fileSize 文件大小
 */
data class DownloadUiState(
    val dashVideo: PlayUrlDashVideo,
    val dashAudio: PlayUrlDashVideo,
    val avid: Long,
    val bvid: String,
    val cid: Long,
    val episodeId: Long,
    val pageCoverUrl: String?,
    val order: Int,
    val name: String,
    val duration: String,
    val videoCodecName: String,
    val resolution: Constant.Quality,
    val audioCodec: Constant.Quality,
    val fileSize: Int? = null,
)