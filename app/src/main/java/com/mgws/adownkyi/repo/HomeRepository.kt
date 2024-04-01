package com.mgws.adownkyi.repo

import com.mgws.adownkyi.core.bilibili.utils.ParseEntrance
import com.mgws.adownkyi.core.bilibili.video.VideoInfoService
import com.mgws.adownkyi.core.bilibili.video.model.VideoView
import com.mgws.adownkyi.core.bilibili.videoStream.VideoStreamService
import com.mgws.adownkyi.core.bilibili.zone.VideoZone
import com.mgws.adownkyi.model.home.VideoInfoUiState
import com.mgws.adownkyi.model.home.VideoItemUiState
import com.mgws.adownkyi.model.home.VideoQualityUiState
import com.mgws.adownkyi.model.home.VideoSectionUiState
import com.mgws.adownkyi.utils.ParseUtils
import com.mgws.adownkyi.utils.durationFormat
import com.mgws.adownkyi.utils.toWordNumber
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class HomeRepository @Inject constructor(
    private val videoInfoService: VideoInfoService,
    private val videoStreamService: VideoStreamService,
) {

    /* 获取视频详细信息 */
    suspend fun getVideoView(url: String): VideoView? = when {
        (ParseEntrance.isAvId(url) || ParseEntrance.isAvUrl(url)) -> {
            val aid: Long = ParseEntrance.getAvId(url)
            videoInfoService.videoViewInfo(aid = aid)
        }

        (ParseEntrance.isBvId(url) || ParseEntrance.isBvUrl(url)) -> {
            val bvid: String = ParseEntrance.getBvId(url)
            videoInfoService.videoViewInfo(bvid = bvid)
        }

        else -> null
    }

    /* 获取视频剧集 */
    @Throws(Exception::class)
    suspend fun getVideoPages(videoView: VideoView): List<VideoItemUiState> {
        if (videoView.pages.isEmpty()) return emptyList()

        val videoPageUiStates = mutableListOf<VideoItemUiState>()

        videoView.pages.forEachIndexed { index, page ->

            // 标题
            val title: String = if (videoView.pages.size == 1)
                videoView.title
            else
                page.part.ifBlank { "${videoView.title}-P$index" }

            // 视频发布时间
            val publishTime = LocalDateTime.ofEpochSecond(videoView.pubdate, 0, ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

            val playUrl = videoStreamService.getVideoPlayUrl(
                videoView.aid,
                videoView.bvid,
                page.cid
            )

            if (playUrl == null) {
                throw Exception("get playUrl error: ${videoView.aid} ${videoView.bvid} ${page.cid}")
            }

            val audioQualityFormatList = ParseUtils.getAudioQualityFormatList(playUrl)

            val videoQualityList = ParseUtils.getVideoQualityList(playUrl)
            val videoCodecList = ParseUtils.getVideoCodecList(playUrl)

            val videoQualityUiStateList = videoQualityList.map {
                VideoQualityUiState(
                    it.first,
                    it.second,
                    videoCodecList,
                    videoCodecList[0]
                )
            }

            val duration = playUrl.dash.duration.durationFormat()

            videoPageUiStates.add(
                VideoItemUiState(
                    avid = videoView.aid,
                    bvid = videoView.bvid,
                    cid = page.cid,
                    episodeId = -1,
                    firstFrame = videoView.pic,
                    order = index,
                    name = title,
                    duration = duration,
                    publishTime = publishTime,
                    playUrl = playUrl,
                    audioQualityFormat = audioQualityFormatList[0],
                    audioQualityFormatList = audioQualityFormatList,
                    videoQuality = videoQualityUiStateList[0],
                    videoQualityList = videoQualityUiStateList
                )
            )
        }
        return videoPageUiStates
    }


    /* 获取视频章节与剧集 */
    @Throws(Exception::class)
    suspend fun getVideoSections(
        videoView: VideoView,
        ugc: Boolean = false,
    ): List<VideoSectionUiState> {
        val videoSectionUiStates = mutableListOf<VideoSectionUiState>()

        // 不需要ugc内容
        if (!ugc) {
            videoSectionUiStates.add(
                VideoSectionUiState(
                    id = 0,
                    title = "default",
                    isSelected = true,
                    videoPages = getVideoPages(videoView)
                )
            )

            return videoSectionUiStates
        }

        videoView.ugcSeason?.sections?.forEach { section ->
            val pages = mutableListOf<VideoItemUiState>()

            section.episodes.forEachIndexed { index, episode ->

                // 这里的发布时间有问题，
                // 如果是合集，也会执行这里，
                // 但是发布时间是入口视频的，不是所有视频的
                // FIXME 修复
                // 视频发布时间
                val publishTime =
                    LocalDateTime.ofEpochSecond(videoView.pubdate, 0, ZoneOffset.UTC)
                        .format(DateTimeFormatter.BASIC_ISO_DATE)

                val playUrl = videoStreamService.getVideoPlayUrl(
                    videoView.aid,
                    videoView.bvid,
                    videoView.cid
                )

                if (playUrl == null) throw InternalError("playUrl is null")

                val audioQualityFormatList = ParseUtils.getAudioQualityFormatList(playUrl)

                val videoQualityList = ParseUtils.getVideoQualityList(playUrl)
                val videoCodecList = ParseUtils.getVideoCodecList(playUrl)

                val videoQualityUiStateList = videoQualityList.map {
                    VideoQualityUiState(
                        it.first,
                        it.second,
                        videoCodecList,
                        videoCodecList[0]
                    )
                }

                pages.add(
                    VideoItemUiState(
                        avid = episode.aid,
                        bvid = episode.bvid,
                        cid = episode.cid,
                        episodeId = -1,
                        firstFrame = episode.page.firstFrame,
                        order = index,
                        name = episode.title,
                        duration = "N/A",
                        publishTime = publishTime,
                        playUrl = playUrl,
                        audioQualityFormat = audioQualityFormatList[0],
                        audioQualityFormatList = audioQualityFormatList,
                        videoQuality = videoQualityUiStateList[0],
                        videoQualityList = videoQualityUiStateList
                    )
                )
            }


            videoSectionUiStates.add(
                VideoSectionUiState
                    (
                    id = section.id,
                    title = section.title,
                    videoPages = pages
                )
            )

        }
        return videoSectionUiStates
    }

    /* 获取视频信息 */
    fun getVideoInfoView(videoView: VideoView): VideoInfoUiState {

        // 查询、保存封面

        // 查询、保存封面
        //videoView.aid, videoView.bvid, videoView.cid, videoView.pic

        // 分区
        val zone = VideoZone.zones.find { it.id == videoView.tid }
        val videoZone = if (zone != null) {
            val zoneParent = VideoZone.zones.find { it.id == zone.parentId }
            if (zoneParent != null) {
                "${zoneParent.name}>${zone.name}"
            } else {
                zone.name
            }
        } else {
            videoView.tname
        }

        return VideoInfoUiState(
            cover = videoView.pic,
            title = videoView.title,
            typeId = videoView.tid,// 分区id
            videoZone = videoZone,
            createTime = LocalDateTime.ofEpochSecond(videoView.pubdate, 0, ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            playNumber = videoView.stat.view.toWordNumber(),
            danmakuNumber = videoView.stat.danmaku.toWordNumber(),
            likeNumber = videoView.stat.like.toWordNumber(),
            coinNumber = videoView.stat.coin.toWordNumber(),
            favoriteNumber = videoView.stat.favorite.toWordNumber(),
            shareNumber = videoView.stat.share.toWordNumber(),
            replyNumber = videoView.stat.reply.toWordNumber(),
            description = videoView.desc,
            upName = videoView.owner.name,
            upperMid = videoView.owner.mid,
            upHeader = videoView.owner.face,
        )
    }

}
