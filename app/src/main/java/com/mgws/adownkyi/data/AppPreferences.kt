package com.mgws.adownkyi.data

import com.mgws.datastore.annotations.HiltDataStore
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
@HiltDataStore("app_prefs.pb")
@OptIn(ExperimentalSerializationApi::class)
data class AppPreferences(
    private val _maxHistory: Int = 0,
    private val _savePath: String = "",
) {
    @ProtoNumber(1)
    val maxHistory = _maxHistory
    fun hasMaxHistory(): Boolean = maxHistory > 0

    @ProtoNumber(2)
    val savePath = _savePath
    fun hasSavePath(): Boolean = savePath.isNotEmpty()

}