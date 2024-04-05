package com.mgws.adownkyi.ui.compose.home

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.mgws.adownkyi.R
import com.mgws.adownkyi.core.utils.logI
import com.mgws.adownkyi.model.download.DownloadUiState
import com.mgws.adownkyi.model.home.VideoInfoUiState
import com.mgws.adownkyi.repo.AppCacheRepository
import com.mgws.adownkyi.repo.DownloadRepository
import com.mgws.adownkyi.repo.HomeRepository
import com.mgws.adownkyi.utils.AppMessagePublisher
import com.mgws.adownkyi.utils.AppMessagePublisher.subscribe
import com.mgws.adownkyi.utils.toStateFlow
import com.mgws.adownkyi.work.VideoItemCacheWorker
import com.mgws.adownkyi.work.VideoItemCacheWorker.Companion.saveVideoItemCache
import com.mgws.adownkyi.work.VideoItemCacheWorker.Companion.updateVideoItemCache
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val context: Application,
    private val appCacheRepository: AppCacheRepository,
    private val homeRepository: HomeRepository,
    private val downloadRepository: DownloadRepository,
) : AndroidViewModel(context) {

    val historyList: StateFlow<List<String>> = appCacheRepository.searchHistoryFlow.toStateFlow(
        emptyList(), viewModelScope,
        { isNotEmpty() }
    )

    var query = mutableStateOf("")

    private var _videoInfoUiState = MutableStateFlow<VideoInfoUiState?>(null)
    val videoInfoView = _videoInfoUiState

    private var _loading = MutableStateFlow(false)
    val loading = _loading

    private var _tip = MutableStateFlow("")
    val tip = _tip

    //------------------------从缓存中更新视频列表--------------------------------//
    private val workManager: WorkManager = WorkManager.getInstance(context)
    private var uniqueWorkName: String = workManager.updateVideoItemCache()
    private val workInfo = workManager.getWorkInfosForUniqueWork(uniqueWorkName)
    private var _videoPagesModel = VideoItemCacheWorker.videoItemUiStateList
    val videoPages = _videoPagesModel
    //-----------------------------------------------------------------------//

    val cookies = appCacheRepository.loginCookiesCacheFlow.toStateFlow(
        emptyList<String>(),
        viewModelScope,
        { isNotEmpty() },
    )

    init {
        subscribe(VideoItemCacheWorker::class, AppMessagePublisher.ALL) {
            _tip.value = it
        }
    }

    fun delHistory(value: String) = viewModelScope.launch {
        appCacheRepository.removeSearchHistory(value)
    }

    fun searchVideo(url: String) = viewModelScope.launch {
        //如果缓存更新未完成,取消他
        if (!workInfo.isDone) {
            workManager.cancelUniqueWork(uniqueWorkName)
        }

        logI("search video: begin")

        //添加一条搜索记录
        appCacheRepository.addSearchHistory(url)

        //提交加载状态
        _loading.emit(true)

        val videoView = homeRepository.getVideoView(url)
        // videoView == null搜索失败,
        if (videoView != null) {
            //TODO: 视频集合,添加不同视频视频详细信息
            val videoInfoView = homeRepository.getVideoInfoView(videoView)
            _videoInfoUiState.emit(videoInfoView)

            var videoCount = 0
            // 获取视频集合
            val videoViewList = homeRepository.getViewSeasonList(videoView)
            if (videoViewList.isNotEmpty()) {

                _videoPagesModel.emit(videoViewList.map {
                    val videoPages = homeRepository.getVideoPages(it)
                    videoCount += videoPages.size
                    videoPages
                }.flatten())

            } else {
                val videoPages = homeRepository.getVideoPages(videoView)
                _videoPagesModel.emit(videoPages)
                videoCount = videoPages.size
            }

            _tip.emit(context.getString(R.string.get_search_data, videoCount))

        } else {
            _tip.emit(context.getString(R.string.search_failed_not_find))
        }

        _loading.emit(false)

        logI("search video: finish")
        WorkManager.getInstance(context).saveVideoItemCache(
            VideoItemCacheWorker.videoItemUiStateList.value
        )
    }

    fun downloadVideo(name: String, downloadUiState: DownloadUiState) {
        downloadRepository.addTask(name, downloadUiState)
    }

}
