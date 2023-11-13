package com.seewo.blink.fragment.interceptor

import android.os.Bundle
import androidx.fragment.app.Fragment

private const val GREEN_CHANNEL = "BLINK#GREEN#CHANNEL"

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

    suspend fun process(from: Fragment?, target: Bundle) {
        interceptors.filter {
            it.filter(target) && !target.isInGreenChannel(it)
        }.forEach {
            if (it is Interceptor) {
                it.process(from, target)
            } else if (it is AsyncInterceptor) {
                it.process(from, target)
            }
        }
    }

    private fun Bundle.isInGreenChannel(interceptor: BaseInterceptor): Boolean =
        getSerializable(GREEN_CHANNEL) == interceptor::class.java
}

fun BaseInterceptor.putInGreenChannel(target: Bundle) {
    target.putSerializable(GREEN_CHANNEL, this::class.java)
}