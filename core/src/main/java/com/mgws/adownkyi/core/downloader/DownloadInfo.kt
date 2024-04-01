@file:UseSerializers(UUIDSerializer::class, ExceptionSerializer::class)

package com.mgws.adownkyi.core.downloader

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.io.IOException
import java.util.UUID

object UUIDSerializer : KSerializer<UUID> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "UUID",
        PrimitiveKind.STRING
    )

    override fun deserialize(decoder: Decoder): UUID {
        return UUID.fromString(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: UUID) {
        encoder.encodeString(value.toString())
    }
}

object ExceptionSerializer : KSerializer<IOException> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "Exception",
        PrimitiveKind.STRING
    )

    override fun deserialize(decoder: Decoder): IOException {
        return IOException(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: IOException) {
        encoder.encodeString(value.toString())
    }
}

@Serializable
data class DownloadInfo(
    val id: UUID = UUID.randomUUID(),
    var url: String,
    val fileName: String,
    val group: List<UUID> = listOf(id),
    var current: Long = 0,
    var total: Long = 0,
    var status: Int = PREPARE,
    var exception: IOException? = null,
) {
    companion object {
        const val PREPARE = 0
        const val RUNNING = 1
        const val PAUSE = 2
        const val CANCEL = 3
        const val SUCCESS = 4
        const val ERROR = 5
    }

}