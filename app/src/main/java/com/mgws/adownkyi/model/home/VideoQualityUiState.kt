package com.mgws.adownkyi.model.home

data class VideoQualityUiState(
    val quality: Int,
    val qualityFormat: String,
    val videoCodecList: List<String>,
    val selectedVideoCodec: String,
)
