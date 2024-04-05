package com.mgws.adownkyi.ui.compose.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mgws.adownkyi.core.utils.logD
import com.mgws.adownkyi.core.utils.logW
import com.mgws.adownkyi.repo.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
) : ViewModel() {

    private var captchaKey: String? = null

    val verification = loginRepository.verification
    fun sendSms(phoneNumber: String) = viewModelScope.launch {
        if (verification.value == null) return@launch

        if (verification.value!!) {
            captchaKey = loginRepository.sendSms(phoneNumber)
            logD("人机验证成功: $captchaKey")
        } else {
            logW("人机验证失败")
        }
    }

    fun login(phoneNumber: String, smsCode: String): Deferred<Boolean> = viewModelScope.async {
        if (captchaKey == null) return@async false
        val success = loginRepository.login(phoneNumber, smsCode, captchaKey!!)

        if (!success) {
            logD("login failed")
        }

        return@async success
    }

    fun updateVerification(verification: Boolean?) {
        loginRepository.verification.value = verification
    }

}