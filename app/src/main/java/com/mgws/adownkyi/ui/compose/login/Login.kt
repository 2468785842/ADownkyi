package com.mgws.adownkyi.ui.compose.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.geetest.sdk.GT3GeetestUtils
import com.mgws.adownkyi.MainActivity
import com.mgws.adownkyi.ui.theme.LocalColorScheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Login(
    navController: NavController,
    loginViewModel: LoginViewModel = hiltViewModel(),
) {
    val verification by loginViewModel.verification.collectAsState()
    val context = LocalContext.current
    val gt3GeetestUtils = (context as MainActivity).gt3GeetestUtils

    Login(
        navController = navController,
        gt3GeetestUtils = gt3GeetestUtils,
        verification = verification,
        changeVerification = { loginViewModel.updateVerification(it) },
        sendSms = loginViewModel::sendSms,
        login = loginViewModel::login
    )
}

@Composable
fun Login(
    navController: NavController,
    gt3GeetestUtils: GT3GeetestUtils?, //方便预览UI, 正常是必须有的
    verification: Boolean?,
    changeVerification: (Boolean?) -> Unit,
    sendSms: (String) -> Unit,
    login: (String, String) -> Deferred<Boolean>,
) {
    val coroutine = rememberCoroutineScope()

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        //        var username by remember { mutableStateOf("") }
        //        var password by remember { mutableStateOf("") }
        var phoneNumber by remember { mutableStateOf("") }
        var smsCode by remember { mutableStateOf("") }
        var sendSmsEnable by remember { mutableStateOf(true) }

        var sendSmsWaitTime by remember { mutableIntStateOf(0) }

        LaunchedEffect(sendSmsEnable) {
            if (!sendSmsEnable) {
                sendSmsWaitTime = 60
                while (sendSmsWaitTime > 0) {
                    sendSmsWaitTime--
                    delay(1000)
                }
                changeVerification(null)
            }
        }
        Box(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            Column(
                modifier = Modifier
                    .height(270.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = "验证码登录",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )

//        TextField(
//            label = {
//                Text(
//                    "用户名",
//                    style = MaterialTheme.typography.labelMedium
//                )
//            },
//            value = username,
//            onValueChange = { username = it })
//
//        TextField(
//            label = {
//                Text(
//                    "密码",
//                    style = MaterialTheme.typography.labelMedium
//                )
//            }, value = password,
//            onValueChange = { password = it })

                TextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    label = {
                        Text(
                            "手机号",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }, value = phoneNumber, onValueChange = {
                        phoneNumber = it
                    })

                // 验证码
//            AndroidView(
//                factory = { ctx ->
//                    GT3GeetestButton(ctx).apply {
//                        layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
//                        gravity = Gravity.CENTER
//                        orientation = LinearLayout.HORIZONTAL
//                        setGeetestUtils(gt3GeetestUtils)
//                    }
//                },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(44.dp)
//            )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        modifier = Modifier
                            .weight(.7f),
                        label = { Text("验证码") },
                        value = smsCode,
                        onValueChange = { smsCode = it }
                    )
                    Button(
                        modifier = Modifier
                            .weight(.3f)
                            .padding(0.dp)
                            .height(IntrinsicSize.Min),
                        enabled = sendSmsEnable,
                        shape = RectangleShape,
                        onClick = {

                            if (phoneNumber.isEmpty()) return@Button

                            if (sendSmsEnable) {
                                // 开始人机验证, 发送验证码
                                gt3GeetestUtils!!.startCustomFlow()
                            }

                        }
                    ) {
                        if (sendSmsEnable)
                            Text(
                                modifier = Modifier.padding(5.dp),
                                text = "发送"
                            )
                        else
                            Text(
                                modifier = Modifier.padding(5.dp),
                                text = "${sendSmsWaitTime}s"
                            )
                    }
                }


                if (verification == null) {
                    sendSmsEnable = true
                } else if (verification) {
                    // 人机验证成功,自动发送验证码请求
                    LaunchedEffect(Unit) { sendSms(phoneNumber) }
                    sendSmsEnable = false
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(LocalColorScheme.current.passContainer),
                        text = "发送成功",
                        color = LocalColorScheme.current.pass,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                } else {
                    sendSmsEnable = true
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.errorContainer),
                        text = "发送失败",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }

                Button(
                    modifier = Modifier
                        .fillMaxWidth(),
                    enabled = phoneNumber.isNotEmpty() && smsCode.isNotEmpty(),
                    onClick = {
                        coroutine.launch {
                            if (login(phoneNumber, smsCode).await()) {
//                                navController.navigate(Router.MainRouter.route)
                                navController.popBackStack()
                            }
                        }
                    }) {
                    Text(text = "登录")
                }

            }

        }
    }
}

@Preview(showBackground = true, locale = "zh-rCN")
@Composable
private fun PreviewLogin() {
    val login: (String, String) -> Deferred<Boolean> = { _, _ ->
        CoroutineScope(Dispatchers.IO).async { true }
    }
    Login(
        navController = NavController(LocalContext.current),
        gt3GeetestUtils = null,
        verification = false,
        changeVerification = {},
        sendSms = {},
        login = login
    )
}