package com.mgws.adownkyi.ui.compose.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mgws.adownkyi.repo.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    val maxHistory = settingsRepository.maxHistory
    val userAgent = settingsRepository.userAgent

    fun updateMaxHistory(maxHistory: String) = viewModelScope.launch {
        //判断字符串是否是数字
        // TODO 输入不是数字时提醒
        if (maxHistory.toIntOrNull() == null) return@launch
        settingsRepository.updateMaxHistory(maxHistory.toInt())
    }

    fun updateUserAgentString(userAgentString: String) = viewModelScope.launch {
        settingsRepository.updateUserAgent(userAgentString)
    }

}