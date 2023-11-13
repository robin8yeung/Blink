package com.seewo.blink.example.fragment.interceptor

import android.os.Bundle
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.seewo.blink.example.Uris
import com.seewo.blink.fragment.interrupt
import com.seewo.blink.fragment.uriNonNull

class ExampleInterceptor : com.seewo.blink.fragment.interceptor.AsyncInterceptor {

    override fun filter(target: Bundle) =
        target.uriNonNull.path == Uris.NEXT_FRAGMENT.toUri().path!!

    override suspend fun process(from: Fragment?, target: Bundle) {
        interrupt("拦截器: 禁止跳转")
    }
}