package com.mgws.adownkyi.ui.compose.download

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.CombinedModifier
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.mgws.adownkyi.model.download.DownloadItemUiState
import com.mgws.adownkyi.model.download.DownloadItemUiState.Companion.DOWNLOADING
import com.mgws.adownkyi.model.download.DownloadItemUiState.Companion.FAILED
import com.mgws.adownkyi.model.download.DownloadItemUiState.Companion.MEDIA_MERGE
import com.mgws.adownkyi.model.download.DownloadItemUiState.Companion.PAUSED
import com.mgws.adownkyi.model.download.DownloadItemUiState.Companion.PREPARE
import com.mgws.adownkyi.model.download.DownloadItemUiState.Companion.SUCCESS
import com.mgws.adownkyi.ui.compose.SwipeMenuBox
import com.mgws.adownkyi.ui.theme.LocalColorScheme
import com.mgws.adownkyi.utils.byteAbbrev

/**
 * TODO: 添加视频封面图片缓存
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DownloadTaskItem(
    downloadViewModel: DownloadViewModel = hiltViewModel(),
    downloadTask: DownloadItemUiState,
    isFocused: Boolean,
    onClick: () -> Unit,
) {

    SwipeMenuBox(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .padding(vertical = 5.dp)
            .clickable(onClick = onClick),
        disable = false
    ) {

        SwipeMenu {

            if (downloadTask.status == FAILED) {
                // 重试按钮
                // 不是完全重新下载, 如果可以会从失败的地方下载, 不然就重新下载??(maybe)
                IconButton(modifier = Modifier
                    .fillMaxHeight()
                    .width(75.dp)
                    .background(LocalColorScheme.current.passContainer),
                    onClick = {
                        downloadViewModel.resumeTask(downloadTask.id)
                    }) {
                    Icon(
                        Icons.Filled.Refresh,
                        contentDescription = null,
                        tint = LocalColorScheme.current.pass
                    )
                }
            }

            when (downloadTask.status) {
                PAUSED, DOWNLOADING -> {
                    val isPaused = downloadTask.status == PAUSED

                    // 暂停按钮
                    IconButton(modifier = Modifier
                        .fillMaxHeight()
                        .width(75.dp)
                        .background(LocalColorScheme.current.warnContainer),
                        onClick = {
                            if (isPaused)
                                downloadViewModel.resumeTask(downloadTask.id)
                            else
                                downloadViewModel.pauseTask(downloadTask.id)
                        }) {
                        Icon(
                            if (isPaused) Icons.Filled.PlayArrow
                            else Icons.Filled.Pause,
                            contentDescription = null,
                            tint = LocalColorScheme.current.warn
                        )
                    }
                }
            }

            // 删除
            IconButton(modifier = Modifier
                .fillMaxHeight()
                .width(75.dp)
                .background(MaterialTheme.colorScheme.errorContainer),
                onClick = { downloadViewModel.cancelTask(downloadTask.id) }) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            Row(modifier = Modifier.weight(.95f)) {
                AsyncImage(
                    modifier = Modifier
                        .height(70.dp)
                        .width(110.dp),
                    model = downloadTask.coverUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .fillMaxSize()
                        .padding(horizontal = 5.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.Start
                ) {
                    val textModifier by remember {
                        mutableStateOf(
                            Modifier
                                .height(24.dp)
                                .wrapContentSize(Alignment.Center)
                        )
                    }
                    Text(
                        modifier =
                        if (isFocused) CombinedModifier(textModifier, Modifier.basicMarquee())
                        else textModifier,
                        color = MaterialTheme.colorScheme.onSurface,
                        text = downloadTask.name,
                        overflow = if (isFocused) TextOverflow.Clip else TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val downloadStatus: DownloadItemUiState.() -> String = {
                            when (status) {
                                PREPARE -> "准备"
                                DOWNLOADING -> "下载中"
                                PAUSED -> "暂停"
                                MEDIA_MERGE -> "合并视频"
                                SUCCESS -> "完成"
                                FAILED -> "失败"
                                else -> ""
                            }
                        }
                        Text(
                            modifier = Modifier.height(20.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            text = downloadStatus(downloadTask),
                        )

                        val current = downloadTask.current.byteAbbrev()
                        val total = downloadTask.total.byteAbbrev()
                        Text(
                            modifier = Modifier.height(20.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            text = "${current.first}${current.second}/${total.first}${total.second}"
                        )
                    }
                }
            }

            when (downloadTask.status) {
                DOWNLOADING ->
                    LinearProgressIndicator(
                        modifier = Modifier
                            .weight(.05f)
                            .fillMaxWidth(),
                        progress = {
                            if (downloadTask.total == 0) 0f
                            else downloadTask.current.toFloat() / downloadTask.total
                        },
                    )

                MEDIA_MERGE ->
                    LinearProgressIndicator(
                        modifier = Modifier
                            .weight(.05f)
                            .fillMaxWidth(),
                        color = LocalColorScheme.current.warn,
                    )
            }

        }

    }
}