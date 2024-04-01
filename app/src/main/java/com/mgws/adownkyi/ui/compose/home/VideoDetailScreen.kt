package com.mgws.adownkyi.ui.compose.home

import androidx.compose.runtime.Composable
import com.mgws.adownkyi.model.home.VideoInfoUiState

@Composable
fun VideoDetailScreen(
    videoInfoUiState: VideoInfoUiState,
) {
    VideoDetail(videoInfoUiState)
}