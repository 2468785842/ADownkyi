package com.mgws.adownkyi.model.home

data class VideoSectionUiState(
    val id: Long,
    val title: String,
    val isSelected: Boolean = false,
    val videoPages: List<VideoItemUiState>,
)
