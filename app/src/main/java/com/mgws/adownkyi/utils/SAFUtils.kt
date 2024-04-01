package com.mgws.adownkyi.utils

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.mgws.adownkyi.core.utils.logE
import com.mgws.adownkyi.core.utils.logI
import com.mgws.adownkyi.core.utils.logW
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * 通过 SAF获取Uri,然后用Uri获取[DocumentFile]
 * 参考: [com.mgws.adownkyi.MainActivity.updateDirectoryPermission]
 *
 * @param uri SAF OpenDocumentTree Uri
 */
@Throws(Exception::class)
fun Context.getDocumentFileFromUri(uri: Uri): DocumentFile {
    val documentFile = DocumentFile.fromTreeUri(this, uri)
        ?: throw Exception("(documentFile == null) maybe no permission")
    return documentFile
}

fun Context.createFile(directory: Uri, fileName: String, mimeType: String): DocumentFile? {
    val documentFile = try {
        getDocumentFileFromUri(directory)
    } catch (e: Exception) {
        logW(e.message.toString())
        return null
    }

    val createFile = documentFile.createFile(mimeType, fileName)
    if (createFile == null) {
        logW("create file error: $directory, $fileName, $mimeType")
        return null
    }

    logI("create file: ${createFile.name}")
    return createFile
}

/**
 * 文件是否存在
 */
fun Context.fileExists(uri: Uri, fileName: String): Boolean {
    val documentFile = try {
        getDocumentFileFromUri(uri)
    } catch (e: Exception) {
        logE("uri is not exist: $uri", e)
        return false
    }

    return documentFile.findFile(fileName) != null
}

fun Context.deleteFile(uri: Uri, fileName: String): Boolean {
    try {
        if (fileExists(uri, fileName)) {
            getDocumentFileFromUri(uri).findFile(fileName)?.delete()
            return true
        }
    } catch (e: Exception) {
        logE("delete file error: $uri, $fileName", e)
    }

    return false
}

/**
 * 通过SAF, 复制App内部文件,到外部
 * @param directory SAF OpenDocumentTree Uri
 * @param fileName 文件名, 复制到外部文件的名称
 * @param internalFilePath 内部文件路径
 * @param mimeType 文件类型
 *
 * @return 是否复制成功
 */
fun Context.copyFile(
    directory: Uri,
    fileName: String,
    internalFilePath: String,
    mimeType: String,
): Boolean {

    logI("copyFile $directory, $fileName, $internalFilePath, $mimeType")
    val fileExists = fileExists(directory, fileName)

    val file: DocumentFile?
    if (!fileExists) {
        file = createFile(directory, fileName, mimeType)
        if (file == null) {
            logW("copy file failed")
            return false
        }
    } else {
        logW("$fileName this file already exists")
        return false
    }

    val outputStream: OutputStream = contentResolver.openOutputStream(file.uri)!!
    val inputStream: InputStream = File(internalFilePath).inputStream()
    try {
        inputStream.copyTo(outputStream)
    } catch (e: IOException) {
        logE("copyFile error", e)
        return false
    } finally {
        outputStream.close()
        inputStream.close()
    }

    logI("copyFile $fileName success")
    return true
}