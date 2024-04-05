package com.mgws.adownkyi.ui.compose.download

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.mgws.adownkyi.model.download.DownloadItemUiState
import com.mgws.adownkyi.ui.compose.LoadingProgress

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadScreen(
    padding: PaddingValues,
    downloadViewModel: DownloadViewModel = hiltViewModel(),
) {

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        color = MaterialTheme.colorScheme.background
    ) {

        val downloadTasks by downloadViewModel.downloadTasks.collectAsState()

        var state by remember { mutableIntStateOf(0) }

        val tabs = listOf("队列", "完成")

        val loading by downloadViewModel.loading.collectAsState()
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
                var indexIsFocused by remember { mutableIntStateOf(0) }
                /*---------------------- 下载列表 --------------------------*/
                LazyColumn {
                    // 显示: state == 0 -> 正在下载, state == 1 -> 已完成
                    val tempDownloadTasks = downloadTasks.run {
                        filter {
                            if (state == 1) it.status == DownloadItemUiState.SUCCESS
                            else it.status != DownloadItemUiState.SUCCESS
                        }
                    }

                    itemsIndexed(tempDownloadTasks.toList(),
                        { _, item -> item.id }) { index, item ->

                        DownloadTaskItem(downloadViewModel, item, indexIsFocused == index) {
                            indexIsFocused = index
                        }
                    }

                }
            }
    }
}