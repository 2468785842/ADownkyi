package com.mgws.adownkyi.core.utils

import android.util.Log

fun Any.getClassName() = this::class.simpleName

fun Any.logE(message: String, exception: Throwable? = null) =
    if (exception == null)
        Log.e(getClassName(), message)
    else
        Log.e(getClassName(), message, exception)

fun Any.logD(message: String) = Log.d(getClassName(), message)
fun Any.logI(message: String) = Log.i(getClassName(), message)

fun Any.logW(message: String) = Log.w(getClassName(), message)
