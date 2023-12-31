package com.seewo.blink.fragment

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.seewo.blink.fragment.container.BlinkContainerFragment
import com.seewo.blink.fragment.interceptor.BaseInterceptor
import com.seewo.blink.fragment.interceptor.Interceptors
import java.util.UUID

object Blink {
    private const val REQUEST_TAG = "BLINK#REQUEST#TAG"
    internal var onNavigation: ((fragment: Fragment) -> Unit)? = null
    private val interceptors = Interceptors()

    private val onResults = mutableMapOf<String, (Bundle?) -> Unit>()

    /**
     * 添加拦截器
     */
    @JvmStatic
    fun add(interceptor: BaseInterceptor) {
        interceptors.add(interceptor)
    }

    /**
     * 移除拦截器
     */
    @JvmStatic
    fun remove(interceptor: BaseInterceptor) {
        interceptors.remove(interceptor)
    }

    suspend fun navigation(
        uri: Uri,
        from: Fragment? = null,
        onResult: ((Bundle?) -> Unit)? = null
    ) {
        from.doNavigation(uri, onResult)
    }

    private suspend fun Fragment?.doNavigation(
        uri: Uri,
        onResult: ((Bundle?) -> Unit)? = null
    ) {
        val to = Bundle().apply {
            setUri(uri)
        }
        interceptors.process(this@doNavigation, to)
        onNavigation?.invoke(BlinkContainerFragment().apply {
            attach(RouteMap.get(to.uriNonNull).apply {
                val requestTag =
                    this@doNavigation?.generateFragmentTag ?: UUID.randomUUID().toString()
                onResult?.let {
                    onResults[requestTag] = onResult
                }
                arguments = to.apply {
                    putString(REQUEST_TAG, requestTag)
                }
            })
        })
    }

    fun returnResult(fragment: Fragment, result: Bundle?) {
        fragment.arguments?.getString(REQUEST_TAG)?.let {
            onResults.remove(it)?.invoke(result)
        }

    }
}
