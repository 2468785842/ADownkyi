package com.mgws.adownkyi.model.download

import androidx.annotation.IntDef
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.protobuf.ProtoNumber
import java.util.UUID

@Stable
@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class DownloadItemUiState(
    @ProtoNumber(1)
    private val _id: String,
    @ProtoNumber(2)
    val name: String,
    @ProtoNumber(3)
    val coverUrl: String,
    @ProtoNumber(4)
    private val _current: Int = 0,
    @ProtoNumber(5)
    private val _total: Int = 0,
    @Status
    @ProtoNumber(6)
    private val _status: Int = PREPARE,
    @ProtoNumber(7)
    val avid: Long,
    @ProtoNumber(8)
    val bvid: String,
    @ProtoNumber(9)
    val cid: Long,
    @Transient
    var isLoadingForSerialize: Boolean = false,
) {
    @Transient
    val id: UUID = UUID.fromString(_id)

    var current by mutableIntStateOf(_current)

    var total by mutableIntStateOf(_total)

    var status by mutableIntStateOf(_status)

    companion object {
        const val PREPARE = -1
        const val DOWNLOADING = 0
        const val MEDIA_MERGE = 1
        const val SUCCESS = 2
        const val FAILED = 3
        const val PAUSED = 4

        @IntDef(
            PREPARE,
            DOWNLOADING,
            MEDIA_MERGE,
            SUCCESS,
            FAILED,
            PAUSED
        )
        @Retention(AnnotationRetention.SOURCE)
        annotation class Status
    }

}
