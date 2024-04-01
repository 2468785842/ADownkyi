package com.mgws.adownkyi.ui.compose.download

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mgws.adownkyi.repo.DownloadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DownloadViewModel @Inject constructor(
    private val downloadRepository: DownloadRepository,
) : ViewModel() {
    val downloadTasks = MutableStateFlow(downloadRepository.downloadTaskMap.values)

    val loading = downloadRepository.loading

    fun resumeTask(id: UUID) = viewModelScope.launch {
        downloadRepository.resumeTask(id)
    }

    fun pauseTask(id: UUID) {
        downloadRepository.pauseTask(id)
    }

    fun cancelTask(id: UUID) = viewModelScope.launch {
        downloadRepository.cancelTask(id)
        // map删除元素,因为用的可变数组compose不能感知,使用emit提示应该刷新ui
        downloadTasks.emit(downloadRepository.downloadTaskMap.values.toMutableList())
        delay(1)
        downloadTasks.emit(downloadRepository.downloadTaskMap.values)
    }

}