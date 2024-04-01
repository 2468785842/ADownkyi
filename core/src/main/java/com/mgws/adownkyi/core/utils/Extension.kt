package com.mgws.adownkyi.core.utils


fun ByteArray.byteArrayToHexString(): String {
    val hexChars = CharArray(this.size * 2)
    for (i in this.indices) {
        val v = this[i].toInt() and 0xFF
        hexChars[i * 2] = "0123456789ABCDEF"[v ushr 4]
        hexChars[i * 2 + 1] = "0123456789ABCDEF"[v and 0x0F]
    }
    return String(hexChars)
}
