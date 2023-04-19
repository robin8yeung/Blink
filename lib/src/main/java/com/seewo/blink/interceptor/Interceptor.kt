package com.seewo.blink.interceptor

import android.content.Context
import android.content.Intent

interface Interceptor {
    /**
     * 过滤器
     * @return 仅在函数返回true时，过滤器生效。默认不过滤。
     */
    fun filter(intent: Intent): Boolean = true
    /**
     * 优先级：数字大的优先级高，允许负数
     */
    fun priority(): Int = 0

    /**
     * 拦截器处理函数，如果需要拦截路由，可以抛异常，异常可以自定义
     *
     * @throws InterruptedException 建议拦截路由抛这个异常
     */
    @kotlin.jvm.Throws(InterruptedException::class)
    fun process(context: Context, intent: Intent)
}