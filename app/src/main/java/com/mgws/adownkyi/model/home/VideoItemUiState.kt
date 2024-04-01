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
    var avid: Long,
    @ProtoNumber(2)
    var bvid: String,
    @ProtoNumber(3)
    var cid: Long,
    @ProtoNumber(4)
    var episodeId: Long,
    @ProtoNumber(5)
    var publishTime: String,
    @Transient
    var initialIsSelected: Boolean = false,
    @ProtoNumber(6)
    var order: Int,
    @ProtoNumber(7)
    var name: String,
    @ProtoNumber(8)
    var duration: String,
    @ProtoNumber(9)
    var firstFrame: String? = null,
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