package com.mgws.adownkyi.core.media

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import com.mgws.adownkyi.core.utils.logI
import java.io.File
import java.nio.ByteBuffer

object AndroidMediaHelper : MediaHelper {
    override fun merge(audio: String, video: String, destVideo: String): Boolean {
        val muxer = MediaMuxer(destVideo, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

        val audioExtractor = MediaExtractor()
        audioExtractor.setDataSource(audio)

        val videoExtractor = MediaExtractor()
        videoExtractor.setDataSource(video)

        // 获取音频轨道和视频轨道
        val audioTrackIndex = getTrackIndex(audioExtractor, "audio/")
        val videoTrackIndex = getTrackIndex(videoExtractor, "video/")

        if (audioTrackIndex == -1 || videoTrackIndex == -1) {
            return false
        }

        // 添加音频轨道和视频轨道到Muxer
        val audioTrack = muxer.addTrack(audioExtractor.getTrackFormat(audioTrackIndex))
        val videoTrack = muxer.addTrack(videoExtractor.getTrackFormat(videoTrackIndex))

        muxer.start()

        // 将音频数据写入Muxer
        extractAndWriteTrack(audioExtractor, audioTrackIndex, muxer, audioTrack)

        // 将视频数据写入Muxer
        extractAndWriteTrack(videoExtractor, videoTrackIndex, muxer, videoTrack)

        muxer.release()

        val deleteA = File(audio).delete()
        val deleteV = File(video).delete()

        logI("delete audio: $deleteA")
        logI("delete video: $deleteV")

        return true
    }

    private fun getTrackIndex(extractor: MediaExtractor, mimeTypePrefix: String): Int {
        val trackCount = extractor.trackCount
        for (i in 0 until trackCount) {
            val format = extractor.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME)
            if (mime != null) {
                if (mime.startsWith(mimeTypePrefix)) {
                    return i
                }
            }
        }
        return -1
    }

    private fun extractAndWriteTrack(
        extractor: MediaExtractor,
        trackIndex: Int,
        muxer: MediaMuxer,
        muxerTrackIndex: Int,
    ) {
        val buffer = ByteBuffer.allocate(1024 * 1024)
        val bufferInfo = MediaCodec.BufferInfo()
        extractor.selectTrack(trackIndex)
        while (true) {
            val sampleSize = extractor.readSampleData(buffer, 0)
            if (sampleSize < 0) {
                break
            }
            bufferInfo.offset = 0
            bufferInfo.size = sampleSize
            bufferInfo.presentationTimeUs = extractor.sampleTime
            bufferInfo.flags = when (extractor.sampleFlags) {
                MediaExtractor.SAMPLE_FLAG_SYNC -> MediaCodec.BUFFER_FLAG_KEY_FRAME
                MediaExtractor.SAMPLE_FLAG_PARTIAL_FRAME -> MediaCodec.BUFFER_FLAG_PARTIAL_FRAME
                else -> 0
            }
            muxer.writeSampleData(muxerTrackIndex, buffer, bufferInfo)
            extractor.advance()
        }

    }
}