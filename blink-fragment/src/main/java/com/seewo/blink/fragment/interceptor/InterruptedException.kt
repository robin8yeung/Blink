package com.seewo.blink.fragment.interceptor

open class InterruptedException(
    val interceptor: BaseInterceptor, msg: String? = null
): RuntimeException("Interrupted By ${interceptor::class.java.simpleName} $msg")