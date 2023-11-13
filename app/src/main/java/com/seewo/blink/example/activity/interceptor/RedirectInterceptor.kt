package com.seewo.blink.example.activity.interceptor

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.seewo.blink.example.Uris
import com.seewo.blink.interceptor.AsyncInterceptor

class RedirectInterceptor: AsyncInterceptor {
    override fun filter(intent: Intent): Boolean = intent.data?.path == Uris.NOT_EXISTS.toUri().path!!

    override fun priority() = 2

    override suspend fun process(context: Context, intent: Intent) {
        intent.apply {
            data = Uris.HOME_ACTIVITY.toUri()
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }
}