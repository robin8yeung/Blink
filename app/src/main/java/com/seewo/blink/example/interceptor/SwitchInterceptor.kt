package com.seewo.blink.example.interceptor

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.net.toUri
import com.seewo.blink.interceptor.Interceptor

class SwitchInterceptor: Interceptor {
    override fun filter(intent: Intent): Boolean = intent.data?.path == "/example"

    override fun priority() = 2

    override fun process(context: Context, intent: Intent) {
        val uri = intent.data ?: return
        Log.e("robin", "interceptor")
        intent.data = "blink://navigator/example2".toUri()
    }
}