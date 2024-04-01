package com.mgws.adownkyi

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.mgws.adownkyi.ui.compose.download.DownloadScreen
import com.mgws.adownkyi.ui.compose.home.HomeScreen

// 定义一个枚举类，表示不同的页面
enum class Page(
    val icon: ImageVector,
    val label: Int,
    val compose: @Composable (PaddingValues) -> Unit,
) {
    Home(Icons.Filled.Home, R.string.home, { padding -> HomeScreen(padding) }),
    Download(Icons.Filled.SaveAlt, R.string.download, { padding -> DownloadScreen(padding) }),
// TODO 设置界面
//    Setting(Icons.Filled.Settings, R.string.settings, { padding -> SettingsScreen(padding) })
}

@Composable
fun ADownKyiApp(modifier: Modifier = Modifier) {
    // 定义一个状态变量，表示当前选中的页面
    var selectedPage by remember { mutableStateOf(Page.Home) }

    // 使用 Scaffold 组件来创建一个基本的页面结构
    Scaffold(
        modifier = modifier,
        bottomBar = { // 设置底部导航栏
            NavigationBar {
                Page.entries.forEach { page ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = page.icon,
                                contentDescription = null
                            )
                        }, // 设置图标
                        label = { Text(stringResource(page.label)) }, // 设置标签
                        selected = selectedPage == page, // 设置是否选中
                        onClick = { selectedPage = page }, // 设置点击事件
                    )
                }
            }
        }
    ) { padding ->
        selectedPage.compose(padding)
    }
}