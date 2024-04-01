package com.mgws.adownkyi.ui.compose.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mgws.adownkyi.ui.compose.LoadingProgress
import com.mgws.adownkyi.utils.toDownloadUiState

enum class TabType(
    val compose: @Composable (HomeViewModel) -> Unit,
) {
    VIDEO_LIST({ homeViewModel ->
        val videoPages by homeViewModel.videoPages.collectAsState()

        VideoListScreen(
            videoItemUiStates = videoPages,
        ) { videoPageModels ->
            // 下载选中的视频
            for (videoPageModel in videoPageModels) {
                val downloadModel =
                    videoPageModel.toDownloadUiState()
                homeViewModel.downloadVideo(videoPageModel.name, downloadModel)
            }
        }
    }),
    VIDEO_DETAIL({ homeViewModel ->
        val videoInfoView by homeViewModel.videoInfoView.collectAsState()
        if (videoInfoView != null) {
            VideoDetailScreen(videoInfoView!!)
        }
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    padding: PaddingValues,
    homeViewModel: HomeViewModel = viewModel(),
) {

    val loading by homeViewModel.loading.collectAsState()
    val searchHistory by homeViewModel.historyList.collectAsState()
    val tip by homeViewModel.tip.collectAsState()

    var query by homeViewModel.query

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        var state by remember { mutableIntStateOf(0) }

        val tabs = listOf("视频列表", "视频详情")

        //添加搜索文本框
        SearchUserInput(
            query = query,
            history = searchHistory,
            onSearch = { query = it; homeViewModel.searchVideo(it) },
            onQueryChange = { query = it.trim() },
            onClear = { query = "" },
            onDelHistory = homeViewModel::delHistory
        )

        if (tip.isNotBlank()) {
            Text(
                text = tip,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(15.dp),
                overflow = TextOverflow.Ellipsis
            )
        }

        if (loading)
            LoadingProgress()
        else

            Column {
                SecondaryTabRow(selectedTabIndex = state) {
                    tabs.forEachIndexed { index, tab ->
                        Tab(
                            text = { Text(text = tab, maxLines = 1) },
                            selected = (state == index),
                            onClick = { state = index }
                        )
                    }
                }
                TabType.entries[state].compose(homeViewModel)
            }
    }
}
