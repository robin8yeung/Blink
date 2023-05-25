package com.seewo.blink.example.interceptor

import android.content.Context
import android.content.Intent
import android.util.Log
import com.seewo.blink.interceptor.Interceptor

class LoggerInterceptor: Interceptor {
    override fun priority() = 1

    override fun process(context: Context, intent: Intent) {
        Log.i("LoggerInterceptor", "from $context to $intent data: ${intent.dataString}")
    }
}