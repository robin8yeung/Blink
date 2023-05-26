package com.seewo.blink.example.fragment.interceptor

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.seewo.blink.fragment.generateFragmentTag
import com.seewo.blink.fragment.interceptor.Interceptor
import com.seewo.blink.fragment.uriNonNull

class LoggerInterceptor: Interceptor {

    override fun process(from: Fragment?, target: Bundle) {
        Log.i("fragment", "[from] ${from?.generateFragmentTag} [to] ${target.uriNonNull}")
    }
}