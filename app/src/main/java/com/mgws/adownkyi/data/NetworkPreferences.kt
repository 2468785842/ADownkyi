package com.mgws.adownkyi.data

import com.mgws.datastore.annotations.HiltDataStore
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
@HiltDataStore("network_prefs.pb")
@OptIn(ExperimentalSerializationApi::class)
data class NetworkPreferences(private val _userAgent: String = "") {

    @ProtoNumber(1)
    val userAgent: String = _userAgent
    fun hasUserAgent(): Boolean = _userAgent.isNotEmpty()
}