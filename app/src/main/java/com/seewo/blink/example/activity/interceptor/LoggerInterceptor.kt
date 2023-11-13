package com.seewo.blink.example.activity.interceptor

import android.content.Context
import android.content.Intent
import android.util.Log
import com.seewo.blink.interceptor.AsyncInterceptor

class LoggerInterceptor: AsyncInterceptor {
    override fun priority() = 1

    override suspend fun process(context: Context, intent: Intent) {
        Log.i("LoggerInterceptor", "from $context to $intent data: ${intent.dataString}")
    }
}