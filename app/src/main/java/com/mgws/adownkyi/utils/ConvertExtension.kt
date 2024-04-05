package com.mgws.adownkyi.utils

import com.mgws.adownkyi.core.bilibili.utils.Constant
import com.mgws.adownkyi.core.bilibili.videoStream.model.PlayUrlDashVideo
import com.mgws.adownkyi.model.download.DownloadUiState
import com.mgws.adownkyi.model.home.VideoItemUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

fun VideoItemUiState.toDownloadUiState(): DownloadUiState {

    if (this.videoQuality == null)
        throw NullPointerException("videoQuality is null")

    //视频编码
    val videoCodecName = this.videoQuality!!.selectedVideoCodec
    //画质
    val resolution =
        Constant.Info(this.videoQuality!!.quality, this.videoQuality!!.qualityFormat)
    var dashVideo: PlayUrlDashVideo? = null

    for (video in this.playUrl!!.dash.video) {
        val codec = Constant.CODEC_IDS.firstOrNull { it.id == video.id }
            ?: Constant.CODEC_IDS[0]
        if (video.id == resolution.id && codec.name == videoCodecName) {
            dashVideo = video
            break
        }
    }

    //音质
    val audioCodec = Constant.QUALITIES.firstOrNull { it.name == this.audioQualityFormat }
        ?: Constant.QUALITIES[0]
    var dashAudio: PlayUrlDashVideo? = null

    for (audio in this.playUrl!!.dash.audio) {
        if (audio.id == audioCodec.id) {
            dashAudio = audio
            break
        }
    }

    return DownloadUiState(
        dashVideo = dashVideo!!,
        dashAudio = dashAudio!!,
        avid = this.avid,
        bvid = this.bvid,
        cid = this.cid,
        episodeId = this.episodeId,
        pageCoverUrl = this.firstFrame,
        order = this.order,
        name = this.name,
        duration = this.duration,
        videoCodecName = videoCodecName,
        audioCodec = audioCodec,
        resolution = resolution,
    )
}

/**
 * 将Flow转为StateFlow, 如果没有就返回初始值
 *
 * @param initial 初始值
 * @param scope 协程作用域
 * @param hasMethod 判断是否拥有[ifHasBlock]
 * @param ifHasBlock 有就执行,并返回结果
 */
fun <T, R> Flow<T>.toStateFlow(
    initial: R,
    scope: CoroutineScope,
    hasMethod: T.() -> Boolean,
    ifHasBlock: (T.() -> R)? = null,
): StateFlow<R> = this@toStateFlow.map {
    if (it.hasMethod()) {
        @Suppress("UNCHECKED_CAST")
        if (ifHasBlock == null) this@toStateFlow.first() as R
        else it.ifHasBlock()
    } else {
        initial
    }
}.stateIn(scope, SharingStarted.WhileSubscribed(), initial)