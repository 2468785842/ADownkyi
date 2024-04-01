package com.mgws.adownkyi.core.bilibili

sealed class Result<out T> {

    data class Success<out T>(val data: T) : Result<T>()

    data class Failure(val throwable: Throwable?) : Result<Nothing>()

}

