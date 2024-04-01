package com.mgws.adownkyi.repo

import androidx.datastore.core.DataStore
import com.mgws.adownkyi.MainActivity.Companion.updateDirectoryPermission
import com.mgws.adownkyi.core.utils.logE
import com.mgws.adownkyi.data.AppPreferences
import com.mgws.adownkyi.data.NetworkPreferences
import com.mgws.adownkyi.utils.toStateFlow
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * TODO: 目前并没有卵用, 只有显示, 没有应用到代码, 没有实际功能
 */
@Singleton
class SettingsRepository @Inject constructor(
    private val appPrefs: DataStore<AppPreferences>,
    private val networkPrefs: DataStore<NetworkPreferences>,
) : CoroutineScope by CoroutineScope(dispatcher) {

    companion object {
        private val dispatcher = Dispatchers.IO + SupervisorJob() +
                CoroutineExceptionHandler { context, e ->
                    context.logE("save config error", e)
                }
    }

    val maxHistory = appPrefs.data.toStateFlow(
        10,
        this,
        AppPreferences::hasMaxHistory
    ) { maxHistory }

    val userAgent = networkPrefs.data.toStateFlow(
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36",
        this,
        NetworkPreferences::hasUserAgent
    ) { userAgent }

    val savePath = appPrefs.data.map {
        it.savePath
    }

    fun updateMaxHistory(value: Int) = launch {
        appPrefs.updateData {
            it.copy(_maxHistory = value)
        }
    }

    fun updateUserAgent(value: String) = launch {
        networkPrefs.updateData {
            it.copy(_userAgent = value)
        }
    }

    fun updateSavePath(value: String) = launch {
        appPrefs.updateData {
            it.copy(_savePath = value)
        }
    }

    fun getDirectory() = updateDirectoryPermission(this)

}