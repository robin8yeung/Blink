package com.seewo.blink.example.fragment.interceptor

import android.os.Bundle
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.seewo.blink.example.Uris
import com.seewo.blink.fragment.interceptor.Interceptor
import com.seewo.blink.fragment.setUri
import com.seewo.blink.fragment.uriNonNull

class RedirectInterceptor: Interceptor {
    override fun filter(target: Bundle) =target.uriNonNull.path == Uris.NOT_EXISTS.toUri().path!!

    override fun priority() = 2
    override fun process(from: Fragment?, target: Bundle) {
        target.setUri(Uris.FRAGMENT)
    }
}