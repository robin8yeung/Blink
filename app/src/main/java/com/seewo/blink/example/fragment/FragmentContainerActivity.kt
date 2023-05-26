package com.seewo.blink.example.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.seewo.blink.example.fragment.interceptor.LoggerInterceptor
import com.seewo.blink.fragment.attach
import com.seewo.blink.fragment.container.BlinkContainerActivity
import com.seewo.blink.fragment.detach

class FragmentContainerActivity: BlinkContainerActivity() {
    companion object {
        @JvmStatic
        fun start(context: Context) {
            context.startActivity(Intent(context, FragmentContainerActivity::class.java))
        }
    }

    override fun startFragment() = HomeFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LoggerInterceptor().attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        LoggerInterceptor().detach()
    }
}