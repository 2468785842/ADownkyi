package com.mgws.adownkyi.utils

import com.mgws.adownkyi.core.bilibili.utils.Constant
import com.mgws.adownkyi.core.bilibili.videoStream.model.PlayUrl

object ParseUtils {

    fun getAudioQualityFormatList(playUrl: PlayUrl): List<String> {
        //音质
        val audioQualityFormatList = ArrayList(playUrl.dash.audio.map { audio ->
            Constant.QUALITIES.first { it.id == audio.id }.name
        })

        //杜比
        if (playUrl.dash.dolby.audio != null) {
            audioQualityFormatList.add(Constant.QUALITIES[3].name)
        }

        //Hi-Res
        if (playUrl.dash.flac?.audio != null) {
            audioQualityFormatList.add(Constant.QUALITIES[4].name)
        }

        audioQualityFormatList.reverse()
        return audioQualityFormatList
    }

    fun getVideoCodecList(playUrl: PlayUrl): List<String> {
        val videoCodecList = ArrayList<String>()

        //编码
        for (video in playUrl.dash.video) {
            val name = Constant.CODEC_IDS.first { it.id == video.codecId }.name

            //过滤重复的
            if (videoCodecList.firstOrNull { it == name } == null) {
                videoCodecList.add(name)
            }
        }

        return videoCodecList
    }

    fun getVideoQualityList(playUrl: PlayUrl): List<Pair<Int, String>> {
        val videoQualityList = ArrayList<Pair<Int, String>>()
        //画质
        for (video in playUrl.dash.video) {
            val qualityFormat =
                playUrl.supportFormats.first { it.quality == video.id }.newDescription

            //过滤重复的
            if (videoQualityList.firstOrNull { it.second == qualityFormat } == null) {
                videoQualityList.add(video.id to qualityFormat)
            }
        }
        return videoQualityList
    }

}