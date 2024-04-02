package com.mgws.adownkyi.core.bilibili.video.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @property aids 稿件avid	对应下方数组中内容 aid
 * @property archives 合集中的视频
 * @property meta 合集元数据
 * @property page 分页信息
 */
@Serializable
data class SeasonsArchivesList(
    val aids: List<Long>,
    val archives: List<Archive>,
    val meta: Meta,
    val page: Page,
) {

    /**
     * @property covr 合集封面 URL
     * @property description 合集描述
     * @property mid UP 主 ID
     * @property name 合集标题
     * @property ptime 发布时间 Unix 时间戳
     * @property seasonId 合集 ID
     * @property total 合集内视频数量
     */
    @Serializable
    data class Meta(
        val category: Long = 0,
        val covr: String? = null,
        val description: String,
        val mid: Long,
        val name: String,
        val ptime: Long,
        @SerialName("season_id") val seasonId: Long,
        val total: Long,
    )

    /**
     * @property pageNum 分页页码
     * @property pageSize 单页个数
     * @property total 合集内视频数量
     */
    @Serializable
    data class Page(
        @SerialName("page_num") val pageNum: Int,
        @SerialName("page_size") val pageSize: Int,
        val total: Int,
    )
}

/**
 * @property aid 稿件avid
 * @property bvid 稿件bvid
 * @property ctime 创建时间 Unix 时间戳
 * @property duration 视频时长 单位为秒
 * @property interactiveVideo 是否允许互动视频
 * @property pic 封面 URL
 * @property playbackPosition 会随着播放时间增长，播放完成后为 -1 。单位未知
 * @property pubdate 发布日期 Unix 时间戳
 * @property stat 稿件信息
 * @property state 稿件状态
 * @property title 稿件标题
 */
@Serializable
data class Archive(
    val aid: Long,
    val bvid: String,
    val ctime: Long,
    val duration: Long,
    @SerialName("enable_vt") val enableVt: Boolean? = false,
    @SerialName("interactive_video") val interactiveVideo: Boolean? = false,
    val pic: String,
    @SerialName("playback_position") val playbackPosition: Long,
    val pubdate: Long,
    val stat: ArchiveStat,
    val state: Long = 0,
    val title: String,
    @SerialName("ugc_pay") val ugcPay: Long = 0,
    @SerialName("vt_display") val vtDisplay: String,
)

/**
 * @property view 稿件播放量
 */
@Serializable
data class ArchiveStat(
    val view: Long,
    val vt: Long = 0,
)
