package com.seewo.blink.interceptor

import android.content.Context
import android.content.Intent
import com.seewo.blink.Blink

internal class Interceptors {
    private val interceptors = mutableListOf<Interceptor>()

    fun add(interceptor: Interceptor) {
        if (interceptors.contains(interceptor)) return
        interceptors += interceptor
        interceptors.sortByDescending { it.priority() }
    }

    fun remove(interceptor: Interceptor) {
        interceptors.remove(interceptor)
    }

    fun process(context: Context, intent: Intent) {
        interceptors.filter {
            !intent.isInGreenChannel(it) && it.filter(intent)
        }.forEach { it.process(context, intent) }
    }

    private fun Intent.isInGreenChannel(interceptor: Interceptor): Boolean =
        getSerializableExtra(Blink.GREEN_CHANNEL) == interceptor::class.java
}