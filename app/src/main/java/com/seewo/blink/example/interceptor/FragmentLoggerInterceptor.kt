package com.seewo.blink.example.interceptor

import android.util.Log
import androidx.fragment.app.Fragment
import com.seewo.blink.fragment.generateFragmentTag
import com.seewo.blink.fragment.interceptor.Interceptor

class FragmentLoggerInterceptor: Interceptor {
    override fun process(from: Fragment?, to: Fragment) = to.apply {
        Log.i("fragment", "[from] ${from?.generateFragmentTag} [to] ${to.generateFragmentTag} [args] ${to.arguments}")
    }
}