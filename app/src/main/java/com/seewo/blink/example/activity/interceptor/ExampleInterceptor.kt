package com.seewo.blink.example.activity.interceptor

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.seewo.blink.example.Uris
import com.seewo.blink.interceptor.AsyncInterceptor
import com.seewo.blink.interrupt

class ExampleInterceptor: AsyncInterceptor {
    override fun filter(intent: Intent) = intent.data?.path == Uris.NEXT.toUri().path

    override suspend fun process(context: Context, intent: Intent) {
        interrupt("拦截器: 禁止跳转")
    }
}