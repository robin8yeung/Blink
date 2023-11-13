package com.seewo.blink.interceptor

import android.content.Intent

interface BaseInterceptor {
    /**
     * 过滤器
     * @return 仅在函数返回true时，过滤器生效。默认不过滤。
     */
    fun filter(intent: Intent): Boolean = true
    /**
     * 优先级：数字大的优先级高，允许负数
     */
    fun priority(): Int = 0
}