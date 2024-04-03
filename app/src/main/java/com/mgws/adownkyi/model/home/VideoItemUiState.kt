package com.mgws.adownkyi.model.home

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.mgws.adownkyi.core.bilibili.videoStream.model.PlayUrl
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.protobuf.ProtoNumber

@OptIn(ExperimentalSerializationApi::class)
@Stable
@Serializable
data class VideoItemUiState(
    @Transient
    var playUrl: PlayUrl? = null,
    @ProtoNumber(1)
    val avid: Long,
    @ProtoNumber(2)
    val bvid: String,
    @ProtoNumber(3)
    val cid: Long,
    @ProtoNumber(4)
    val episodeId: Long,
    @ProtoNumber(5)
    val publishTime: String,
    @Transient
    var initialIsSelected: Boolean = false,
    @ProtoNumber(6)
    val order: Int,
    @ProtoNumber(7)
    val name: String,
    @ProtoNumber(8)
    val duration: String,
    @ProtoNumber(9)
    val firstFrame: String? = null,
    @Transient
    var audioQualityFormatList: List<String> = emptyList(),
    @Transient
    var audioQualityFormat: String = "",
    @Transient
    var videoQualityList: List<VideoQualityUiState> = emptyList(),
    @Transient
    var videoQuality: VideoQualityUiState? = null,
) {
    var isSelected by mutableStateOf(initialIsSelected)
}