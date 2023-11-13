package com.seewo.blink.example.fragment.interceptor

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.seewo.blink.fragment.generateFragmentTag
import com.seewo.blink.fragment.interceptor.AsyncInterceptor
import com.seewo.blink.fragment.uriNonNull

class LoggerInterceptor: AsyncInterceptor {

    override suspend fun process(from: Fragment?, target: Bundle) {
        Log.i("fragment", "[from] ${from?.generateFragmentTag} [to] ${target.uriNonNull}")
    }
}