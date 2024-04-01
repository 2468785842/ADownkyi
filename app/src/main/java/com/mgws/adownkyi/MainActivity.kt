package com.mgws.adownkyi

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.Modifier
import com.mgws.adownkyi.core.utils.logI
import com.mgws.adownkyi.core.utils.logW
import com.mgws.adownkyi.repo.DownloadRepository
import com.mgws.adownkyi.repo.SettingsRepository
import com.mgws.adownkyi.service.DownloadService
import com.mgws.adownkyi.service.DownloadServiceBinder
import com.mgws.adownkyi.ui.theme.ADownKyiTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var downloadRepository: DownloadRepository

    @Inject
    lateinit var settingsRepository: SettingsRepository

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            downloadRepository.downloadServiceBinder = service as DownloadServiceBinder
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            downloadRepository.downloadServiceBinder = null
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            ADownKyiTheme {
                ADownKyiApp(Modifier.safeDrawingPadding())
            }
        }

        val intent = Intent(this, DownloadService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

        registerResult = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) {
            uri = it
            if (uri == null) throw Exception("get directory error uri is null")
            val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            contentResolver.takePersistableUriPermission(uri!!, flag)
            logI("get directory: $uri")
        }

        updateDirectoryPermission(settingsRepository)
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
    }

    companion object {

        private var uri: Uri? = null

        private lateinit var registerResult: ActivityResultLauncher<Uri?>

        /**
         * 通过SAF框架获取文件夹读写权限 [SettingsRepository.savePath]为空则更新
         */
        fun updateDirectoryPermission(settingsRepository: SettingsRepository) {
            uri = null
            CoroutineScope(Dispatchers.Default).launch {
                delay(1000)
                if (settingsRepository.savePath.first().isEmpty()) {
                    registerResult.launch(null)
                    while (uri == null) {
                        logW("waiting for get directory permission")
                        delay(1000)
                    }
                    settingsRepository.updateSavePath(uri.toString())
                }
            }
        }
    }

}