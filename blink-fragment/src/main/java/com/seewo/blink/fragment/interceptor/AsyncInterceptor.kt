package com.seewo.blink.fragment.interceptor

import android.os.Bundle
import androidx.fragment.app.Fragment

interface AsyncInterceptor: BaseInterceptor {

    /**
     * 拦截器处理函数，如果需要拦截路由，可以抛异常，异常可以自定义
     *
     * @param from 路由来源，如果不是从Fragment发起则为空
     * @param target 路由目标arguments，最终会从中取出uri，并索引到Fragment
     * @return 最终路由目标。如果希望通过uri来索引目标Fragment，可以通过RouteMetadata来索引
     * @throws InterruptedException 建议拦截路由抛这个异常
     */
    @kotlin.jvm.Throws(InterruptedException::class)
    suspend fun process(from: Fragment?, target: Bundle)
}