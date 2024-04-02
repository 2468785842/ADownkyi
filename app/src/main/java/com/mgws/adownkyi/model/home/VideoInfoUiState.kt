package com.mgws.adownkyi.model.home

/**
 * 视频信息实体类
 *
 * @property cover 视频封面
 * @property title 视频标题
 * @property videoZone 视频分区
 * @property playNumber 播放量
 * @property danmakuNumber 弹幕
 * @property likeNumber 点赞
 * @property coinNumber 投币
 * @property favoriteNumber 收藏
 * @property shareNumber 分享
 * @property replyNumber 评论
 * @property upName up主名称
 * @property upHeader up主头像
 * @property description 视频简介
 */
data class VideoInfoUiState(
    var upperMid: Long,
    var typeId: Int,
    var cover: String,
    var title: String,
    var videoZone: String,
    var createTime: String,
    var playNumber: String,
    var danmakuNumber: String,
    var likeNumber: String,
    var coinNumber: String,
    var favoriteNumber: String? = null,
    var shareNumber: String,
    var replyNumber: String,
    var upName: String,
    var upHeader: String,
    var description: String,
)
