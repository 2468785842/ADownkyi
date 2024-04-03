package com.mgws.adownkyi.ui.compose.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mgws.adownkyi.core.bilibili.videoStream.model.PlayUrl
import com.mgws.adownkyi.core.bilibili.videoStream.model.PlayUrlDash
import com.mgws.adownkyi.core.bilibili.videoStream.model.PlayUrlDashDolby
import com.mgws.adownkyi.core.bilibili.videoStream.model.PlayUrlDashVideo
import com.mgws.adownkyi.core.bilibili.videoStream.model.PlayUrlSupportFormat
import com.mgws.adownkyi.model.home.VideoItemUiState
import com.mgws.adownkyi.model.home.VideoQualityUiState
import com.mgws.adownkyi.ui.compose.TextSpinner

@Composable
fun VideoItem(
    videoPageUiState: VideoItemUiState,
    onCheckedChange: (VideoItemUiState) -> Unit,
) {
    var checked = videoPageUiState.isSelected

    Surface(color = MaterialTheme.colorScheme.surface) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
                .padding(vertical = 16.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .offset(y = 4.dp)
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    color = MaterialTheme.colorScheme.onSurface,
                    text = (videoPageUiState.order + 1).toString()
                )
                Checkbox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = 10.dp),
                    checked = checked,
                    onCheckedChange = {
                        checked = !checked
                        videoPageUiState.isSelected = checked
                        onCheckedChange(videoPageUiState)
                    })
            }

            Column {
                Text(
                    modifier = Modifier.height(24.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    text = videoPageUiState.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier = Modifier
                            .height(20.dp)
                            .wrapContentSize(Alignment.Center)
                            .padding(horizontal = 8.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        text = videoPageUiState.duration
                    )
                }
                Row(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    //音质
                    TextSpinner(
                        videoPageUiState.audioQualityFormatList,
                    ) {
                        videoPageUiState.audioQualityFormat = it
                    }
                    //画质
                    TextSpinner(
                        videoPageUiState.videoQualityList.map { it.qualityFormat },
                    ) {
                        videoPageUiState.videoQuality =
                            videoPageUiState.videoQualityList.first { videoQuality ->
                                videoQuality.qualityFormat == it
                            }
                    }
                    //视频编码
                    TextSpinner(
                        videoPageUiState.videoQuality!!.videoCodecList,
                    ) {
                        videoPageUiState.audioQualityFormat = it
                    }
                }

            }
        }
    }
}

@Composable
fun VideoList(
    videoPageUiStates: List<VideoItemUiState>,
    onCheckedChange: (VideoItemUiState) -> Unit,
) {
    LazyColumn {
        items(items = videoPageUiStates, key = { "${it.avid}-${it.bvid}-${it.cid}" }) {
            VideoItem(it, onCheckedChange)
        }
    }
}

@Preview(showBackground = true, locale = "zh-rCN")
@Composable
private fun PreviewVideoItem() {
    VideoItem(VIDEO_PAGE) {}
}

private val VIDEO_PAGE = VideoItemUiState(
    playUrl = PlayUrl(
        acceptDescription = listOf("高清 720P", "清晰 480P", "流畅 360P"),
        acceptQuality = listOf(64, 32, 16),
        durl = emptyList(),
        dash = PlayUrlDash(
            video = listOf(
                PlayUrlDashVideo(
                    id = 32,
                    baseUrl = "https://xy218x91x222x132xy.mcdn.bilivideo.cn:4483/upgcxcode/77/71/153247177/153247177_nb2-1-30032.m4s?e=ig8euxZM2rNcNbdlhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEV4NC8xNEV4N03eN0B5tZlqNxTEto8BTrNvNeZVuJ10Kj_g2UB02J0mN0B5tZlqNCNEto8BTrNvNC7MTX502C8f2jmMQJ6mqF2fka1mqx6gqj0eN0B599M=&uipk=5&nbs=1&deadline=1709215550&gen=playurlv2&os=mcdn&oi=1966959359&trid=00004bec95af15e143cca5b1a4aa11d2b7cau&mid=0&platform=pc&upsig=e6c434170497303db5b5c2780bebf5ac&uparams=e,uipk,nbs,deadline,gen,os,oi,trid,mid,platform&mcdnid=50000332&bvc=vod&nettype=0&orderid=0,3&buvid=&build=0&f=u_0_0&agrr=0&bw=19507&logo=A0020000",
                    backupUrl = listOf(
                        "https://upos-sz-estgoss.bilivideo.com/upgcxcode/77/71/153247177/153247177_nb2-1-30032.m4s?e=ig8euxZM2rNcNbdlhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEV4NC8xNEV4N03eN0B5tZlqNxTEto8BTrNvNeZVuJ10Kj_g2UB02J0mN0B5tZlqNCNEto8BTrNvNC7MTX502C8f2jmMQJ6mqF2fka1mqx6gqj0eN0B599M=&uipk=5&nbs=1&deadline=1709215550&gen=playurlv2&os=upos&oi=1966959359&trid=4bec95af15e143cca5b1a4aa11d2b7cau&mid=0&platform=pc&upsig=b562e905f12c2753b07ef559560c4188&uparams=e,uipk,nbs,deadline,gen,os,oi,trid,mid,platform&bvc=vod&nettype=0&orderid=1,3&buvid=&build=0&f=u_0_0&agrr=0&bw=19507&logo=40000000",
                        "https://upos-sz-mirrorali.bilivideo.com/upgcxcode/77/71/153247177/153247177_nb2-1-30032.m4s?e=ig8euxZM2rNcNbdlhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEV4NC8xNEV4N03eN0B5tZlqNxTEto8BTrNvNeZVuJ10Kj_g2UB02J0mN0B5tZlqNCNEto8BTrNvNC7MTX502C8f2jmMQJ6mqF2fka1mqx6gqj0eN0B599M=&uipk=5&nbs=1&deadline=1709215550&gen=playurlv2&os=alibv&oi=1966959359&trid=4bec95af15e143cca5b1a4aa11d2b7cau&mid=0&platform=pc&upsig=0730db15043b78ef40f1e5f2c5494e9b&uparams=e,uipk,nbs,deadline,gen,os,oi,trid,mid,platform&bvc=vod&nettype=0&orderid=2,3&buvid=&build=0&f=u_0_0&agrr=0&bw=19507&logo=40000000"
                    ), mimeType = "video/mp4", codecs = "avc1.64001F",
                    width = 852, height = 480, frameRate = "25", codecId = 7
                ),
                PlayUrlDashVideo(
                    id = 32,
                    baseUrl = "https://xy218x91x222x132xy.mcdn.bilivideo.cn:4483/upgcxcode/77/71/153247177/153247177_x3-1-30033.m4s?e=ig8euxZM2rNcNbdlhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEV4NC8xNEV4N03eN0B5tZlqNxTEto8BTrNvNeZVuJ10Kj_g2UB02J0mN0B5tZlqNCNEto8BTrNvNC7MTX502C8f2jmMQJ6mqF2fka1mqx6gqj0eN0B599M=&uipk=5&nbs=1&deadline=1709215550&gen=playurlv2&os=mcdn&oi=1966959359&trid=00004bec95af15e143cca5b1a4aa11d2b7cau&mid=0&platform=pc&upsig=2e179b2f3729d7b65bdfb226305f48c1&uparams=e,uipk,nbs,deadline,gen,os,oi,trid,mid,platform&mcdnid=50000332&bvc=vod&nettype=0&orderid=0,3&buvid=&build=0&f=u_0_0&agrr=0&bw=19405&logo=A0020000",
                    backupUrl = listOf(
                        "https://upos-sz-mirrorali.bilivideo.com/upgcxcode/77/71/153247177/153247177_x3-1-30033.m4s?e=ig8euxZM2rNcNbdlhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEV4NC8xNEV4N03eN0B5tZlqNxTEto8BTrNvNeZVuJ10Kj_g2UB02J0mN0B5tZlqNCNEto8BTrNvNC7MTX502C8f2jmMQJ6mqF2fka1mqx6gqj0eN0B599M=&uipk=5&nbs=1&deadline=1709215550&gen=playurlv2&os=alibv&oi=1966959359&trid=4bec95af15e143cca5b1a4aa11d2b7cau&mid=0&platform=pc&upsig=0faeb1523443da1147e94c4f8d8384a2&uparams=e,uipk,nbs,deadline,gen,os,oi,trid,mid,platform&bvc=vod&nettype=0&orderid=1,3&buvid=&build=0&f=u_0_0&agrr=0&bw=19405&logo=40000000",
                        "https://upos-sz-estgoss.bilivideo.com/upgcxcode/77/71/153247177/153247177_x3-1-30033.m4s?e=ig8euxZM2rNcNbdlhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEV4NC8xNEV4N03eN0B5tZlqNxTEto8BTrNvNeZVuJ10Kj_g2UB02J0mN0B5tZlqNCNEto8BTrNvNC7MTX502C8f2jmMQJ6mqF2fka1mqx6gqj0eN0B599M=&uipk=5&nbs=1&deadline=1709215550&gen=playurlv2&os=upos&oi=1966959359&trid=4bec95af15e143cca5b1a4aa11d2b7cau&mid=0&platform=pc&upsig=31474f471f7bd8cc938151135e77055d&uparams=e,uipk,nbs,deadline,gen,os,oi,trid,mid,platform&bvc=vod&nettype=0&orderid=2,3&buvid=&build=0&f=u_0_0&agrr=0&bw=19405&logo=40000000"
                    ),
                    mimeType = "video/mp4",
                    codecs = "hev1.1.6.L120.90",
                    width = 852,
                    height = 480,
                    frameRate = "25",
                    codecId = 12
                ),
                PlayUrlDashVideo(
                    id = 32,
                    baseUrl = "https://xy218x91x222x132xy.mcdn.bilivideo.cn:4483/upgcxcode/77/71/153247177/153247177-1-100023.m4s?e=ig8euxZM2rNcNbdlhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEV4NC8xNEV4N03eN0B5tZlqNxTEto8BTrNvNeZVuJ10Kj_g2UB02J0mN0B5tZlqNCNEto8BTrNvNC7MTX502C8f2jmMQJ6mqF2fka1mqx6gqj0eN0B599M=&uipk=5&nbs=1&deadline=1709215550&gen=playurlv2&os=mcdn&oi=1966959359&trid=00004bec95af15e143cca5b1a4aa11d2b7cau&mid=0&platform=pc&upsig=9eb6c572796ad0b7a3ced784565d6c42&uparams=e,uipk,nbs,deadline,gen,os,oi,trid,mid,platform&mcdnid=50000332&bvc=vod&nettype=0&orderid=0,3&buvid=&build=0&f=u_0_0&agrr=0&bw=11737&logo=A0020000",
                    backupUrl = listOf(
                        "https://upos-sz-mirrorali.bilivideo.com/upgcxcode/77/71/153247177/153247177-1-100023.m4s?e=ig8euxZM2rNcNbdlhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEV4NC8xNEV4N03eN0B5tZlqNxTEto8BTrNvNeZVuJ10Kj_g2UB02J0mN0B5tZlqNCNEto8BTrNvNC7MTX502C8f2jmMQJ6mqF2fka1mqx6gqj0eN0B599M=&uipk=5&nbs=1&deadline=1709215550&gen=playurlv2&os=alibv&oi=1966959359&trid=4bec95af15e143cca5b1a4aa11d2b7cau&mid=0&platform=pc&upsig=b90ba86cdac52c421962b22e6c241de7&uparams=e,uipk,nbs,deadline,gen,os,oi,trid,mid,platform&bvc=vod&nettype=0&orderid=1,3&buvid=&build=0&f=u_0_0&agrr=0&bw=11737&logo=40000000",
                        "https://upos-sz-estgoss.bilivideo.com/upgcxcode/77/71/153247177/153247177-1-100023.m4s?e=ig8euxZM2rNcNbdlhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEV4NC8xNEV4N03eN0B5tZlqNxTEto8BTrNvNeZVuJ10Kj_g2UB02J0mN0B5tZlqNCNEto8BTrNvNC7MTX502C8f2jmMQJ6mqF2fka1mqx6gqj0eN0B599M=&uipk=5&nbs=1&deadline=1709215550&gen=playurlv2&os=upos&oi=1966959359&trid=4bec95af15e143cca5b1a4aa11d2b7cau&mid=0&platform=pc&upsig=c49a4f4e08b346af1ae9d0aaad4dd793&uparams=e,uipk,nbs,deadline,gen,os,oi,trid,mid,platform&bvc=vod&nettype=0&orderid=2,3&buvid=&build=0&f=u_0_0&agrr=0&bw=11737&logo=40000000"
                    ),
                    mimeType = "video/mp4",
                    codecs = "av01.0.04M.08.0.110.01.01.01.0",
                    width = 852,
                    height = 480,
                    frameRate = "25",
                    codecId = 13
                ),
                PlayUrlDashVideo(
                    id = 16,
                    baseUrl = "https://xy218x91x222x132xy.mcdn.bilivideo.cn:4483/upgcxcode/77/71/153247177/153247177_x3-1-30011.m4s?e=ig8euxZM2rNcNbdlhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEV4NC8xNEV4N03eN0B5tZlqNxTEto8BTrNvNeZVuJ10Kj_g2UB02J0mN0B5tZlqNCNEto8BTrNvNC7MTX502C8f2jmMQJ6mqF2fka1mqx6gqj0eN0B599M=&uipk=5&nbs=1&deadline=1709215550&gen=playurlv2&os=mcdn&oi=1966959359&trid=00004bec95af15e143cca5b1a4aa11d2b7cau&mid=0&platform=pc&upsig=fd5d82938259e3223ac560f13d80feb9&uparams=e,uipk,nbs,deadline,gen,os,oi,trid,mid,platform&mcdnid=50000332&bvc=vod&nettype=0&orderid=0,3&buvid=&build=0&f=u_0_0&agrr=0&bw=13011&logo=A0020000",
                    backupUrl = listOf(
                        "https://upos-sz-mirror08c.bilivideo.com/upgcxcode/77/71/153247177/153247177_x3-1-30011.m4s?e=ig8euxZM2rNcNbdlhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEV4NC8xNEV4N03eN0B5tZlqNxTEto8BTrNvNeZVuJ10Kj_g2UB02J0mN0B5tZlqNCNEto8BTrNvNC7MTX502C8f2jmMQJ6mqF2fka1mqx6gqj0eN0B599M=&uipk=5&nbs=1&deadline=1709215550&gen=playurlv2&os=08cbv&oi=1966959359&trid=4bec95af15e143cca5b1a4aa11d2b7cau&mid=0&platform=pc&upsig=4dc69edfc02da402e98e107ba9850180&uparams=e,uipk,nbs,deadline,gen,os,oi,trid,mid,platform&bvc=vod&nettype=0&orderid=1,3&buvid=&build=0&f=u_0_0&agrr=0&bw=13011&logo=40000000",
                        "https://upos-sz-mirror08c.bilivideo.com/upgcxcode/77/71/153247177/153247177_x3-1-30011.m4s?e=ig8euxZM2rNcNbdlhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEV4NC8xNEV4N03eN0B5tZlqNxTEto8BTrNvNeZVuJ10Kj_g2UB02J0mN0B5tZlqNCNEto8BTrNvNC7MTX502C8f2jmMQJ6mqF2fka1mqx6gqj0eN0B599M=&uipk=5&nbs=1&deadline=1709215550&gen=playurlv2&os=08cbv&oi=1966959359&trid=4bec95af15e143cca5b1a4aa11d2b7cau&mid=0&platform=pc&upsig=4dc69edfc02da402e98e107ba9850180&uparams=e,uipk,nbs,deadline,gen,os,oi,trid,mid,platform&bvc=vod&nettype=0&orderid=2,3&buvid=&build=0&f=u_0_0&agrr=0&bw=13011&logo=40000000], mimeType=video/mp4, codecs=hev1.1.6.L120.90, width=640, height=360, frameRate=25, codecId=12), PlayUrlDashVideo(id=16, baseUrl=https://xy218x91x222x132xy.mcdn.bilivideo.cn:4483/upgcxcode/77/71/153247177/153247177-1-30016.m4s?e=ig8euxZM2rNcNbdlhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEV4NC8xNEV4N03eN0B5tZlqNxTEto8BTrNvNeZVuJ10Kj_g2UB02J0mN0B5tZlqNCNEto8BTrNvNC7MTX502C8f2jmMQJ6mqF2fka1mqx6gqj0eN0B599M=&uipk=5&nbs=1&deadline=1709215550&gen=playurlv2&os=mcdn&oi=1966959359&trid=00004bec95af15e143cca5b1a4aa11d2b7cau&mid=0&platform=pc&upsig=90b636d78fe7e80ae8f96ddb0273a73b&uparams=e,uipk,nbs,deadline,gen,os,oi,trid,mid,platform&mcdnid=50000332&bvc=vod&nettype=0&orderid=0,3&buvid=&build=0&f=u_0_0&agrr=0&bw=49873&logo=A0020000, backupUrl=[https://upos-sz-mirrorali.bilivideo.com/upgcxcode/77/71/153247177/153247177-1-30016.m4s?e=ig8euxZM2rNcNbdlhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEV4NC8xNEV4N03eN0B5tZlqNxTEto8BTrNvNeZVuJ10Kj_g2UB02J0mN0B5tZlqNCNEto8BTrNvNC7MTX502C8f2jmMQJ6mqF2fka1mqx6gqj0eN0B599M=&uipk=5&nbs=1&deadline=1709215550&gen=playurlv2&os=alibv&oi=1966959359&trid=4bec95af15e143cca5b1a4aa11d2b7cau&mid=0&platform=pc&upsig=087b906ddbbdcf2df4756f61ad8a164c&uparams=e,uipk,nbs,deadline,gen,os,oi,trid,mid,platform&bvc=vod&nettype=0&orderid=1,3&buvid=&build=0&f=u_0_0&agrr=0&bw=49873&logo=40000000, https://upos-sz-mirrorali.bilivideo.com/upgcxcode/77/71/153247177/153247177-1-30016.m4s?e=ig8euxZM2rNcNbdlhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEV4NC8xNEV4N03eN0B5tZlqNxTEto8BTrNvNeZVuJ10Kj_g2UB02J0mN0B5tZlqNCNEto8BTrNvNC7MTX502C8f2jmMQJ6mqF2fka1mqx6gqj0eN0B599M=&uipk=5&nbs=1&deadline=1709215550&gen=playurlv2&os=alibv&oi=1966959359&trid=4bec95af15e143cca5b1a4aa11d2b7cau&mid=0&platform=pc&upsig=087b906ddbbdcf2df4756f61ad8a164c&uparams=e,uipk,nbs,deadline,gen,os,oi,trid,mid,platform&bvc=vod&nettype=0&orderid=2,3&buvid=&build=0&f=u_0_0&agrr=0&bw=49873&logo=40000000"
                    ),
                    mimeType = "video/mp4",
                    codecs = "avc1.64001E",
                    width = 640,
                    height = 360,
                    frameRate = "25",
                    codecId = 7
                ),
                PlayUrlDashVideo(
                    id = 16,
                    baseUrl = "https://xy1x58x186x228xy.mcdn.bilivideo.cn:8082/v1/resource/153247177-1-100022.m4s?agrr=0&build=0&buvid=&bvc=vod&bw=8814&deadline=1709215550&e=ig8euxZM2rNcNbdlhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEV4NC8xNEV4N03eN0B5tZlqNxTEto8BTrNvNeZVuJ10Kj_g2UB02J0mN0B5tZlqNCNEto8BTrNvNC7MTX502C8f2jmMQJ6mqF2fka1mqx6gqj0eN0B599M%3D&f=u_0_0&gen=playurlv2&logo=A0020000&mcdnid=50000332&mid=0&nbs=1&nettype=0&oi=1966959359&orderid=0%2C3&os=mcdn&platform=pc&sign=f03a12&traceid=trUBZSogeVuJab_0_e_N&uipk=5&uparams=e%2Cuipk%2Cnbs%2Cdeadline%2Cgen%2Cos%2Coi%2Ctrid%2Cmid%2Cplatform&upsig=78389b66fc4e6f86c1ef2f51532bb4de",
                    backupUrl = listOf(
                        "https://xy218x91x222x132xy.mcdn.bilivideo.cn:4483/upgcxcode/77/71/153247177/153247177-1-100022.m4s?e=ig8euxZM2rNcNbdlhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEV4NC8xNEV4N03eN0B5tZlqNxTEto8BTrNvNeZVuJ10Kj_g2UB02J0mN0B5tZlqNCNEto8BTrNvNC7MTX502C8f2jmMQJ6mqF2fka1mqx6gqj0eN0B599M=&uipk=5&nbs=1&deadline=1709215550&gen=playurlv2&os=mcdn&oi=1966959359&trid=00004bec95af15e143cca5b1a4aa11d2b7cau&mid=0&platform=pc&upsig=78389b66fc4e6f86c1ef2f51532bb4de&uparams=e,uipk,nbs,deadline,gen,os,oi,trid,mid,platform&mcdnid=50000332&bvc=vod&nettype=0&orderid=0,3&buvid=&build=0&f=u_0_0&agrr=0&bw=8814&logo=A0020000",
                        "https://upos-sz-mirrorcos.bilivideo.com/upgcxcode/77/71/153247177/153247177-1-100022.m4s?e=ig8euxZM2rNcNbdlhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEV4NC8xNEV4N03eN0B5tZlqNxTEto8BTrNvNeZVuJ10Kj_g2UB02J0mN0B5tZlqNCNEto8BTrNvNC7MTX502C8f2jmMQJ6mqF2fka1mqx6gqj0eN0B599M=&uipk=5&nbs=1&deadline=1709215550&gen=playurlv2&os=cosbv&oi=1966959359&trid=4bec95af15e143cca5b1a4aa11d2b7cau&mid=0&platform=pc&upsig=3bf1dba87c5fc09e30ce83aad1caab8f&uparams=e,uipk,nbs,deadline,gen,os,oi,trid,mid,platform&bvc=vod&nettype=0&orderid=1,3&buvid=&build=0&f=u_0_0&agrr=0&bw=8814&logo=40000000"
                    ),
                    mimeType = "video/mp4",
                    codecs = "av01.0.01M.08.0.110.01.01.01.0",
                    width = 640,
                    height = 360,
                    frameRate = "25",
                    codecId = 13
                )
            ),
            audio = listOf(

                PlayUrlDashVideo(
                    id = 30280,
                    baseUrl = "https://xy218x91x222x132xy.mcdn.bilivideo.cn:4483/upgcxcode/77/71/153247177/153247177_nb2-1-30280.m4s?e=ig8euxZM2rNcNbdlhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEV4NC8xNEV4N03eN0B5tZlqNxTEto8BTrNvNeZVuJ10Kj_g2UB02J0mN0B5tZlqNCNEto8BTrNvNC7MTX502C8f2jmMQJ6mqF2fka1mqx6gqj0eN0B599M=&uipk=5&nbs=1&deadline=1709215550&gen=playurlv2&os=mcdn&oi=1966959359&trid=00004bec95af15e143cca5b1a4aa11d2b7cau&mid=0&platform=pc&upsig=c738d69a985abb83a5a5932d9536646f&uparams=e,uipk,nbs,deadline,gen,os,oi,trid,mid,platform&mcdnid=50000332&bvc=vod&nettype=0&orderid=0,3&buvid=&build=0&f=u_0_0&agrr=0&bw=15843&logo=A0020000",
                    backupUrl = listOf(
                        "https://upos-sz-estgoss.bilivideo.com/upgcxcode/77/71/153247177/153247177_nb2-1-30280.m4s?e=ig8euxZM2rNcNbdlhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEV4NC8xNEV4N03eN0B5tZlqNxTEto8BTrNvNeZVuJ10Kj_g2UB02J0mN0B5tZlqNCNEto8BTrNvNC7MTX502C8f2jmMQJ6mqF2fka1mqx6gqj0eN0B599M=&uipk=5&nbs=1&deadline=1709215550&gen=playurlv2&os=upos&oi=1966959359&trid=4bec95af15e143cca5b1a4aa11d2b7cau&mid=0&platform=pc&upsig=ded3776b38bff571a7d5d55a39769e17&uparams=e,uipk,nbs,deadline,gen,os,oi,trid,mid,platform&bvc=vod&nettype=0&orderid=1,3&buvid=&build=0&f=u_0_0&agrr=0&bw=15843&logo=40000000",
                        "https://upos-sz-mirrorali.bilivideo.com/upgcxcode/77/71/153247177/153247177_nb2-1-30280.m4s?e=ig8euxZM2rNcNbdlhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEV4NC8xNEV4N03eN0B5tZlqNxTEto8BTrNvNeZVuJ10Kj_g2UB02J0mN0B5tZlqNCNEto8BTrNvNC7MTX502C8f2jmMQJ6mqF2fka1mqx6gqj0eN0B599M=&uipk=5&nbs=1&deadline=1709215550&gen=playurlv2&os=alibv&oi=1966959359&trid=4bec95af15e143cca5b1a4aa11d2b7cau&mid=0&platform=pc&upsig=11c90b509589185723a360b31af4034b&uparams=e,uipk,nbs,deadline,gen,os,oi,trid,mid,platform&bvc=vod&nettype=0&orderid=2,3&buvid=&build=0&f=u_0_0&agrr=0&bw=15843&logo=40000000"
                    ),
                    mimeType = "audio/mp4",
                    codecs = "mp4a.40.2",
                    width = 0,
                    height = 0,
                    frameRate = "",
                    codecId = 0
                ),
                PlayUrlDashVideo(
                    id = 30216,
                    baseUrl = "https://xy218x91x222x132xy.mcdn.bilivideo.cn:4483/upgcxcode/77/71/153247177/153247177-1-30216.m4s?e=ig8euxZM2rNcNbdlhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEV4NC8xNEV4N03eN0B5tZlqNxTEto8BTrNvNeZVuJ10Kj_g2UB02J0mN0B5tZlqNCNEto8BTrNvNC7MTX502C8f2jmMQJ6mqF2fka1mqx6gqj0eN0B599M=&uipk=5&nbs=1&deadline=1709215550&gen=playurlv2&os=mcdn&oi=1966959359&trid=00004bec95af15e143cca5b1a4aa11d2b7cau&mid=0&platform=pc&upsig=db8c4690d994560d192e2dfa6d753fde&uparams=e,uipk,nbs,deadline,gen,os,oi,trid,mid,platform&mcdnid=50000332&bvc=vod&nettype=0&orderid=0,3&buvid=&build=0&f=u_0_0&agrr=0&bw=8388&logo=A0020000",
                    backupUrl = listOf(
                        "https://upos-sz-estgoss.bilivideo.com/upgcxcode/77/71/153247177/153247177-1-30216.m4s?e=ig8euxZM2rNcNbdlhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEV4NC8xNEV4N03eN0B5tZlqNxTEto8BTrNvNeZVuJ10Kj_g2UB02J0mN0B5tZlqNCNEto8BTrNvNC7MTX502C8f2jmMQJ6mqF2fka1mqx6gqj0eN0B599M=&uipk=5&nbs=1&deadline=1709215550&gen=playurlv2&os=upos&oi=1966959359&trid=4bec95af15e143cca5b1a4aa11d2b7cau&mid=0&platform=pc&upsig=6974388e31969c8ecf5678d6e51f58a0&uparams=e,uipk,nbs,deadline,gen,os,oi,trid,mid,platform&bvc=vod&nettype=0&orderid=1,3&buvid=&build=0&f=u_0_0&agrr=0&bw=8388&logo=40000000",
                        "https://upos-sz-mirrorali.bilivideo.com/upgcxcode/77/71/153247177/153247177-1-30216.m4s?e=ig8euxZM2rNcNbdlhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEV4NC8xNEV4N03eN0B5tZlqNxTEto8BTrNvNeZVuJ10Kj_g2UB02J0mN0B5tZlqNCNEto8BTrNvNC7MTX502C8f2jmMQJ6mqF2fka1mqx6gqj0eN0B599M=&uipk=5&nbs=1&deadline=1709215550&gen=playurlv2&os=alibv&oi=1966959359&trid=4bec95af15e143cca5b1a4aa11d2b7cau&mid=0&platform=pc&upsig=251f322aebf19762d5b0bcf89382df69&uparams=e,uipk,nbs,deadline,gen,os,oi,trid,mid,platform&bvc=vod&nettype=0&orderid=2,3&buvid=&build=0&f=u_0_0&agrr=0&bw=8388&logo=40000000"
                    ),
                    mimeType = "audio/mp4",
                    codecs = "mp4a.40.2",
                    width = 0,
                    height = 0,
                    frameRate = "",
                    codecId = 0
                ),
                PlayUrlDashVideo(
                    id = 30232,
                    baseUrl = "https://xy150x138x226x194xy.mcdn.bilivideo.cn:8082/v1/resource/153247177_nb2-1-30232.m4s?agrr=0&build=0&buvid=&bvc=vod&bw=15843&deadline=1709215550&e=ig8euxZM2rNcNbdlhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEV4NC8xNEV4N03eN0B5tZlqNxTEto8BTrNvNeZVuJ10Kj_g2UB02J0mN0B5tZlqNCNEto8BTrNvNC7MTX502C8f2jmMQJ6mqF2fka1mqx6gqj0eN0B599M%3D&f=u_0_0&gen=playurlv2&logo=A0020000&mcdnid=50000332&mid=0&nbs=1&nettype=0&oi=1966959359&orderid=0%2C3&os=mcdn&platform=pc&sign=15bef3&traceid=trHoKaPiZdzvjM_0_e_N&uipk=5&uparams=e%2Cuipk%2Cnbs%2Cdeadline%2Cgen%2Cos%2Coi%2Ctrid%2Cmid%2Cplatform&upsig=6c4032792ba45fad017a70affb1f5209",
                    backupUrl = listOf(
                        "https://xy218x91x222x132xy.mcdn.bilivideo.cn:4483/upgcxcode/77/71/153247177/153247177_nb2-1-30232.m4s?e=ig8euxZM2rNcNbdlhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEV4NC8xNEV4N03eN0B5tZlqNxTEto8BTrNvNeZVuJ10Kj_g2UB02J0mN0B5tZlqNCNEto8BTrNvNC7MTX502C8f2jmMQJ6mqF2fka1mqx6gqj0eN0B599M=&uipk=5&nbs=1&deadline=1709215550&gen=playurlv2&os=mcdn&oi=1966959359&trid=00004bec95af15e143cca5b1a4aa11d2b7cau&mid=0&platform=pc&upsig=6c4032792ba45fad017a70affb1f5209&uparams=e,uipk,nbs,deadline,gen,os,oi,trid,mid,platform&mcdnid=50000332&bvc=vod&nettype=0&orderid=0,3&buvid=&build=0&f=u_0_0&agrr=0&bw=15843&logo=A0020000",
                        "https://upos-sz-estgoss.bilivideo.com/upgcxcode/77/71/153247177/153247177_nb2-1-30232.m4s?e=ig8euxZM2rNcNbdlhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEV4NC8xNEV4N03eN0B5tZlqNxTEto8BTrNvNeZVuJ10Kj_g2UB02J0mN0B5tZlqNCNEto8BTrNvNC7MTX502C8f2jmMQJ6mqF2fka1mqx6gqj0eN0B599M=&uipk=5&nbs=1&deadline=1709215550&gen=playurlv2&os=upos&oi=1966959359&trid=4bec95af15e143cca5b1a4aa11d2b7cau&mid=0&platform=pc&upsig=9d9d9d594310c9fbbe323d6e93da6777&uparams=e,uipk,nbs,deadline,gen,os,oi,trid,mid,platform&bvc=vod&nettype=0&orderid=1,3&buvid=&build=0&f=u_0_0&agrr=0&bw=15843&logo=40000000"
                    ),
                    mimeType = "audio/mp4",
                    codecs = "mp4a.40.2",
                    width = 0,
                    height = 0,
                    frameRate = "",
                    codecId = 0
                )
            ),
            dolby = PlayUrlDashDolby(type = 0, audio = null), flac = null, duration = 1000
        ),
        supportFormats = listOf(
            PlayUrlSupportFormat(
                quality = 64,
                format = "flv720",
                newDescription = "720P 高清",
                displayDesc = "720P",
                superscript = ""
            ),
            PlayUrlSupportFormat(
                quality = 32,
                format = "flv480",
                newDescription = "480P 清晰",
                displayDesc = "480P",
                superscript = ""
            ),
            PlayUrlSupportFormat(
                quality = 16,
                format = "mp4",
                newDescription = "360P 流畅",
                displayDesc = "360P",
                superscript = ""
            )
        )
    ),
    avid = 89778007,
    bvid = "BV1u741177K9",
    cid = 153247177,
    episodeId = -1,
    publishTime = "2020-02-17",
    firstFrame = null,
    initialIsSelected = false,
    order = 0,
    name = "华为数通路由交换HCIP（HCNP）01-企业网络高级解决方案",
    duration = "N/A",
    audioQualityFormatList = listOf("Dolby Atmos", "中质量", "低质量", "高质量"),
    audioQualityFormat = "Dolby Atmos",
    videoQualityList = listOf(
        VideoQualityUiState(
            quality = 32,
            qualityFormat = "480P 清晰",
            videoCodecList = listOf("H.264/AVC", "H.265/HEVC", "AV1"),
            selectedVideoCodec = "H.264/AVC"
        ),
        VideoQualityUiState(
            quality = 16,
            qualityFormat = "360P 流畅",
            videoCodecList = listOf("H.264/AVC", "H.265/HEVC", "AV1"),
            selectedVideoCodec = "H.264/AVC"
        )
    ),
    videoQuality = VideoQualityUiState(
        quality = 32,
        qualityFormat = "480P 清晰",
        videoCodecList = listOf("H.264/AVC", "H.265/HEVC", "AV1"),
        selectedVideoCodec = "H.264/AVC"
    )
)