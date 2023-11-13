package com.seewo.blink.interceptor

import android.content.Context
import android.content.Intent
import com.seewo.blink.Blink

internal class Interceptors {
    private val interceptors = mutableListOf<BaseInterceptor>()

    @Synchronized
    fun add(interceptor: BaseInterceptor) {
        if (interceptors.contains(interceptor)) return
        interceptors += interceptor
        interceptors.sortByDescending { it.priority() }
    }

    @Synchronized
    fun remove(interceptor: BaseInterceptor) {
        interceptors.remove(interceptor)
    }

    suspend fun process(context: Context, intent: Intent) {
        interceptors.filter {
            !intent.isInGreenChannel(it) && it.filter(intent)
        }.forEach {
            if (it is Interceptor) {
                it.process(context, intent)
            } else if (it is AsyncInterceptor) {
                it.process(context, intent)
            }
        }
    }

    private fun Intent.isInGreenChannel(interceptor: BaseInterceptor): Boolean =
        getSerializableExtra(Blink.GREEN_CHANNEL) == interceptor::class.java
}