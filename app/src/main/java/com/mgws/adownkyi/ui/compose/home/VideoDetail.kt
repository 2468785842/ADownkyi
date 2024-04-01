package com.mgws.adownkyi.ui.compose.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mgws.adownkyi.R
import com.mgws.adownkyi.model.home.VideoInfoUiState

/**
 * 显示视频信息
 * TODO: 添加视频封面图片缓存
 *
 */
@Composable
fun VideoDetail(
    videoInfoUiState: VideoInfoUiState,
) {

    SelectionContainer {
        Card(
            modifier = Modifier
                .padding(5.dp)
                .wrapContentSize(align = Alignment.TopCenter)
        ) {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(start = 10.dp, end = 10.dp)
            ) {
                AsyncImage(
                    model = videoInfoUiState.cover,
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
                Text(
                    style = MaterialTheme.typography.titleMedium,
                    text = videoInfoUiState.title
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceAround
                    ) {
                        Row {
                            Text(
                                stringResource(
                                    R.string.video_play_number,
                                    videoInfoUiState.playNumber
                                )
                            )
                            Text(
                                modifier = Modifier.padding(start = 10.dp), text =
                                stringResource(
                                    R.string.video_danmaku_number,
                                    videoInfoUiState.danmakuNumber
                                )
                            )
                        }
                        Row {
                            Text(
                                stringResource(
                                    R.string.video_like_number,
                                    videoInfoUiState.likeNumber
                                )
                            )
                            Text(
                                modifier = Modifier.padding(start = 10.dp),
                                text = stringResource(
                                    R.string.video_coin_number,
                                    videoInfoUiState.coinNumber
                                )
                            )
                        }
                        Row {
                            Text(
                                stringResource(
                                    R.string.video_favorite_number,
                                    videoInfoUiState.favoriteNumber
                                )
                            )
                            Text(
                                modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                                text = stringResource(
                                    R.string.video_share_number,
                                    videoInfoUiState.shareNumber
                                )
                            )
                            Text(
                                stringResource(
                                    R.string.video_reply_number,
                                    videoInfoUiState.replyNumber
                                )
                            )
                        }
                    }
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceAround,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape),
                            model = videoInfoUiState.upHeader,
                            contentDescription = null,
                            contentScale = ContentScale.Fit
                        )
                        Text(videoInfoUiState.upName)
                    }

                }
                Text(
                    modifier = Modifier.padding(top = 10.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    text = videoInfoUiState.description
                )

            }
        }
    }
}


@Preview(showBackground = true, locale = "zh-rCN")
@Composable
private fun PreviewVideoDetail() {
//    VideoDetail(VideoInfoUiState, "", mutableListOf(), {}) {}
}


// https://www.bilibili.com/video/BV1u741177K9/?p=4&vd_source=18d2497a065f7d879d1602ff0caf8cf5
private val VideoInfoUiState = VideoInfoUiState(
    upperMid = 5314521,
    typeId = 122,
    cover = "http://i0.hdslb.com/bfs/archive/70ab659beeb99024ec478b2c56e89bac2128baf0.jpg",
    title = "华为数通路由交换HCIP/HCNP(完)",
    videoZone = "知识>野生技能协会",
    createTime = "2020-02-17 17:50:28",
    playNumber = "87.7万",
    danmakuNumber = "2.8万",
    likeNumber = "6376",
    coinNumber = "4464",
    favoriteNumber = "2.7万",
    shareNumber = "3840",
    replyNumber = "1749",
    upName = "黑桃小超人呀",
    upHeader = "http://i2.hdslb.com/bfs/face/1b1f10d72183d1375a3cf5580c465a97d9c1c35f.jpg",
    description = "自己在论坛找的视频，声音比较清晰，而且老师讲的很全面也很细致\n" +
            "--------" +
            "转载自：https://bbs.hh010.com/\n" +
            "讲师：Wakin谢斌\n" +
            "培训机构：肯耐博IT认证中心\n"
)