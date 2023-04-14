package com.seewo.blink.interceptor

import android.content.Context
import android.content.Intent

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
        interceptors.forEach { it.process(context, intent) }
    }
}