package com.seewo.blink.fragment.interceptor

import android.os.Bundle
import androidx.fragment.app.Fragment

private const val GREEN_CHANNEL = "BLINK#GREEN#CHANNEL"

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

    fun process(from: Fragment?, target: Bundle) {
        interceptors.filter {
            it.filter(target) && !target.isInGreenChannel(it)
        }.forEach {
            it.process(from, target)
        }
    }

    private fun Bundle.isInGreenChannel(interceptor: Interceptor): Boolean =
        getSerializable(GREEN_CHANNEL) == interceptor::class.java
}

fun Interceptor.putInGreenChannel(target: Bundle) {
    target.putSerializable(GREEN_CHANNEL, this::class.java)
}