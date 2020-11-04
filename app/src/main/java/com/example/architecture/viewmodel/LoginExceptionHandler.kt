package com.example.architecture.viewmodel

import android.util.Log

class LoginExceptionHandler: Thread.UncaughtExceptionHandler {
    private val TAG = "EHandler"

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        Log.d(TAG, "Exception thread: ${thread.name}")
        throwable.printStackTrace()
    }
}