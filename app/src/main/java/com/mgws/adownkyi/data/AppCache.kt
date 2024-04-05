package com.mgws.adownkyi.data

import com.mgws.adownkyi.model.download.DownloadItemUiState
import com.mgws.adownkyi.model.home.VideoItemUiState
import com.mgws.datastore.annotations.HiltDataStore
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
@HiltDataStore("app_cache.pb")
@OptIn(ExperimentalSerializationApi::class)
data class AppCache(
    @ProtoNumber(1)
    val videoItemCache: List<VideoItemUiState> = emptyList(),

    @ProtoNumber(2)
    val searchHistory: List<String> = emptyList(),

    @ProtoNumber(3)
    val downloadTaskCache: List<DownloadItemUiState> = emptyList(),

    @ProtoNumber(4)
    val downloaderInfoCache: String? = null,

    @ProtoNumber(5)
    val loginCookies: List<String> = emptyList(),
)