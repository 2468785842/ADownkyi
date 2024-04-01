package com.mgws.adownkyi.repo

import androidx.datastore.core.DataStore
import com.mgws.adownkyi.core.utils.logE
import com.mgws.adownkyi.data.AppCache
import com.mgws.adownkyi.model.download.DownloadItemUiState
import com.mgws.adownkyi.model.home.VideoItemUiState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppCacheRepository @Inject constructor(
    private val appCache: DataStore<AppCache>,
    settingsRepository: SettingsRepository,
) : CoroutineScope by CoroutineScope(dispatcher) {

    companion object {
        private val dispatcher =
            Dispatchers.IO + SupervisorJob() + CoroutineExceptionHandler { context, e ->
                context.logE("save cache error", e)
            }
    }

    // 搜索历史缓存
    val searchHistoryFlow = appCache.data.map { it.searchHistory }

    // 视频列表缓存
    val videoListCacheFlow = appCache.data.map { it.videoItemCache }

    // 下载任务页面信息缓存
    val downloadTaskCacheFlow = appCache.data.map { it.downloadTaskCache }

    // 下载任务
    val downloaderInfoCacheFlow = appCache.data.map { it.downloaderInfoCache }

    private val maxHistoryFlow = settingsRepository.maxHistory

    /**
     * 添加搜索记录,如果没有, 添加, 历史记录maxHistory,超过删除最旧的
     */
    fun addSearchHistory(value: String) = launch {
        if (value.isBlank()) return@launch
        val history = searchHistoryFlow.first().toMutableList()
        val maxHistory = maxHistoryFlow.value

        //没有就添加
        if (history.firstOrNull { it == value } == null) {
            if (history.size == maxHistory) {
                history.removeFirst()
                history.add(value)
                appCache.updateData { preferences ->
                    preferences.copy(searchHistory = history)
                }
            } else {
                appCache.updateData { preferences ->
                    preferences.copy(searchHistory = preferences.searchHistory + value)
                }
            }
        }
    }

    /**
     * 删除搜索记录,如果有
     */
    fun removeSearchHistory(value: String) = launch {
        val history = searchHistoryFlow.first().toMutableList()

        if (history.isNotEmpty()) {
            val index = history.indexOf(value)
            if (index == -1) return@launch
            history.removeAt(index)

            appCache.updateData { preferences ->
                preferences.copy(
                    searchHistory = history
                )
            }
        }
    }

    fun clearVideoItemCacheList() = launch {
        appCache.updateData { preferences ->
            preferences.copy(videoItemCache = emptyList())
        }
    }

    fun addAllVideoItemCacheList(videoItemCacheList: List<VideoItemUiState>) = launch {
        appCache.updateData { preferences ->
            preferences.copy(
                videoItemCache = preferences.videoItemCache + videoItemCacheList
            )
        }
    }

    fun addDownloadTaskCacheList(downloadTaskCache: DownloadItemUiState) = launch {
        appCache.updateData { preferences ->
            preferences.copy(
                downloadTaskCache = preferences.downloadTaskCache + downloadTaskCache
            )
        }
    }

    fun removeDownloadTaskCacheList(id: UUID) = launch {
        appCache.updateData { preferences ->
            preferences.copy(
                downloadTaskCache = preferences.downloadTaskCache.filter { it.id != id }
            )
        }
    }

    fun updateDownloadTaskCache(downloadTaskCache: DownloadItemUiState) = launch {
        appCache.updateData { preferences ->
            val newCache = preferences.downloadTaskCache.map {
                if (it.id == downloadTaskCache.id) {
                    return@map downloadTaskCache.copy(
                        _id = downloadTaskCache.id.toString(),
                        _current = downloadTaskCache.current,
                        _total = downloadTaskCache.total,
                        _status = downloadTaskCache.status,
                    )
                } else it
            }
            preferences.copy(
                downloadTaskCache = newCache
            )
        }
    }

    fun updateDownloaderInfoCache(downloaderInfoCache: String) = launch {
        appCache.updateData { preferences ->
            preferences.copy(
                downloaderInfoCache = downloaderInfoCache
            )
        }
    }
}
