package com.seewo.blink.example.fragment

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.seewo.blink.example.interceptor.FragmentLoggerInterceptor
import com.seewo.blink.fragment.attach
import com.seewo.blink.fragment.container.BlinkContainerActivity

class FragmentContainerActivity: BlinkContainerActivity() {
    companion object {
        @JvmStatic
        fun start(context: Context) {
            RouteMetadata().inject()
            FragmentLoggerInterceptor().attach()
            context.startActivity(Intent(context, FragmentContainerActivity::class.java))
        }
    }

    override fun startFragment(): Fragment = Fragment1()
}