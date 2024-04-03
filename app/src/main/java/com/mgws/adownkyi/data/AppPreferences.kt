package com.mgws.adownkyi.data

import com.mgws.datastore.annotations.HiltDataStore
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
@HiltDataStore("app_prefs.pb")
@OptIn(ExperimentalSerializationApi::class)
data class AppPreferences(
    @ProtoNumber(1)
    private val _maxHistory: Int = 0,
    @ProtoNumber(2)
    private val _savePath: String = "",
) {
    val maxHistory = _maxHistory
    fun hasMaxHistory(): Boolean = maxHistory > 0

    val savePath = _savePath
    fun hasSavePath(): Boolean = savePath.isNotEmpty()

}