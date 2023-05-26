package com.seewo.blink.example

import android.app.Application
import com.seewo.blink.attach
import com.seewo.blink.example.activity.interceptor.LoggerInterceptor

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // 注册路由表
        RouteMetadata().inject()
        LoggerInterceptor().attach()
    }
}