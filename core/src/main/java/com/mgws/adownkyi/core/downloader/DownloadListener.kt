package com.mgws.adownkyi.core.downloader

import java.io.IOException
import java.util.UUID

interface DownloadListener {

    fun progress(id: UUID, current: Long, total: Long)

    fun success(id: UUID)

    fun error(id: UUID, exception: IOException)

    fun pause(id: UUID)

    fun cancel(id: UUID)
}