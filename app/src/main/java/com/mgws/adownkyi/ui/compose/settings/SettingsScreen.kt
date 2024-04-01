package com.mgws.adownkyi.ui.compose.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SettingsScreen(
    padding: PaddingValues,
    settingsViewModel: SettingsViewModel = viewModel(),
) {

    val maxHistory by settingsViewModel.maxHistory.collectAsState()

    val userAgentString by settingsViewModel.userAgent.collectAsState()

    Surface(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {

        Column(modifier = Modifier.padding(padding)) {

            //TODO
            Text(
                "用户设置",
                style = MaterialTheme.typography.titleMedium
            )

            TextField(
                maxHistory.toString(),
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(
                        "保存历史记录条数",
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                onValueChange = settingsViewModel::updateMaxHistory,
                singleLine = true
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 15.dp))

            Text(
                "网络设置",
                style = MaterialTheme.typography.titleMedium
            )


            TextField(
                userAgentString,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(
                        "请求Bilibili服务器时客户端Agent",
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                onValueChange = settingsViewModel::updateUserAgentString,
                singleLine = true
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 15.dp))

        }
    }

}