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
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mgws.adownkyi.ui.compose.download.DownloadScreen
import com.mgws.adownkyi.ui.compose.home.HomeScreen
import com.mgws.adownkyi.ui.compose.login.Login

// 定义一个枚举类，表示不同的页面
enum class Page(
    val icon: ImageVector,
    val label: Int,
    val compose: @Composable (PaddingValues, NavController) -> Unit,
) {
    Home(
        Icons.Filled.Home,
        R.string.home,
        { padding, navController -> HomeScreen(padding, navController) }),
    Download(Icons.Filled.SaveAlt, R.string.download, { padding, _ -> DownloadScreen(padding) }),
// TODO 设置界面
//    Setting(Icons.Filled.Settings, R.string.settings, { padding -> SettingsScreen(padding) })
}

@Composable
fun MainPage(navController: NavController) {
    // 定义一个状态变量，表示当前选中的页面
    var selectedPage by remember { mutableStateOf(Page.Home) }

    // 使用 Scaffold 组件来创建一个基本的页面结构
    Scaffold(
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
        selectedPage.compose(padding, navController)
    }
}

sealed class Router(
    val route: String,
    val screen: @Composable (NavController) -> Unit,
) {
    data object MainRouter : Router("main", { MainPage(it) })
    data object LoginRouter : Router("login", { Login(it) })
}

@Composable
fun ADownKyiApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Router.MainRouter.route,
    ) {
        composable(route = Router.MainRouter.route) {
            Router.MainRouter.screen(navController)
        }
        composable(route = Router.LoginRouter.route) {
            Router.LoginRouter.screen(navController)
        }
    }
}