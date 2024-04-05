package com.mgws.adownkyi.repo

import androidx.datastore.core.DataStore
import com.mgws.adownkyi.core.bilibili.HttpConfig
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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppCacheRepository @Inject constructor(
    private val appCache: DataStore<AppCache>,
    settingsRepository: SettingsRepository,
) : CoroutineScope by CoroutineScope(dispatcher) {

    private val mutex = Mutex()

    companion object {
        private val dispatcher =
            Dispatchers.IO + SupervisorJob() + CoroutineExceptionHandler { context, e ->
                context.logE("AppCacheRepository error", e)
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

    val loginCookiesCacheFlow = appCache.data.map { it.loginCookies }

    private val maxHistoryFlow = settingsRepository.maxHistory

    init {
        launch {
            loginCookiesCacheFlow.collect { cookies ->
                if (cookies.isEmpty()) return@collect
                mutex.withLock {
                    HttpConfig.BiliBiliHttpConfig.cookies = cookies
                }
            }
        }
    }

    fun updateCookies(cookies: List<String>) = launch {
        mutex.withLock {
            appCache.updateData { preferences ->
                preferences.copy(loginCookies = cookies)
            }
        }
    }

    /**
     * 添加搜索记录,如果没有, 添加, 历史记录maxHistory,超过删除最旧的
     */
    fun addSearchHistory(value: String) = launch {
        if (value.isBlank()) return@launch
        val history = searchHistoryFlow.first().toMutableList()

        //没有就添加
        if (history.firstOrNull { it == value } == null) {
            if (history.size == maxHistoryFlow.value) {
                history.removeFirst()
                history.add(value)
                mutex.withLock {
                    appCache.updateData { preferences ->
                        preferences.copy(searchHistory = history)
                    }
                }
            } else {
                mutex.withLock {
                    appCache.updateData { preferences ->
                        preferences.copy(searchHistory = searchHistoryFlow.first() + value)
                    }
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

            mutex.withLock {
                appCache.updateData { preferences ->
                    preferences.copy(searchHistory = history)
                }
            }
        }
    }

    fun clearVideoItemCacheList() = launch {
        mutex.withLock {
            appCache.updateData { preferences ->
                preferences.copy(videoItemCache = emptyList())
            }
        }
    }

    fun addAllVideoItemCacheList(videoItemCacheList: List<VideoItemUiState>) = launch {
        mutex.withLock {
            appCache.updateData { preferences ->
                preferences.copy(
                    videoItemCache = videoListCacheFlow.first() + videoItemCacheList
                )
            }
        }
    }

    fun addDownloadTaskCacheList(downloadTaskCache: DownloadItemUiState) = launch {
        mutex.withLock {
            appCache.updateData { preferences ->
                preferences.copy(
                    downloadTaskCache = downloadTaskCacheFlow.first() + downloadTaskCache
                )
            }
        }
    }

    fun removeDownloadTaskCacheList(id: UUID) = launch {
        val tasks = downloadTaskCacheFlow.first().toMutableList()
        tasks.removeIf { it.id == id }
        mutex.withLock {
            appCache.updateData { preferences ->
                preferences.copy(downloadTaskCache = tasks)
            }
        }
    }

    fun updateDownloadTaskCache(downloadTaskCache: DownloadItemUiState) = launch {
        val downloadTaskCacheList = downloadTaskCacheFlow.first()
        val newCache = downloadTaskCacheList.map {
            if (it.id == downloadTaskCache.id) {
                return@map downloadTaskCache.copy(
                    _id = downloadTaskCache.id.toString(),
                    _current = downloadTaskCache.current,
                    _total = downloadTaskCache.total,
                    _status = downloadTaskCache.status,
                )
            } else it
        }
        mutex.withLock {
            appCache.updateData { preferences ->
                preferences.copy(downloadTaskCache = newCache)
            }
        }
    }

    fun updateDownloaderInfoCache(downloaderInfoCache: String) = launch {
        mutex.withLock {
            appCache.updateData { preferences ->
                preferences.copy(downloaderInfoCache = downloaderInfoCache)
            }
        }
    }
}
