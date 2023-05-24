package com.seewo.blink.fragment

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.seewo.blink.fragment.container.BlinkContainerFragment
import com.seewo.blink.fragment.interceptor.Interceptor
import com.seewo.blink.fragment.interceptor.Interceptors
import java.util.UUID

object Blink {
    private const val REQUEST_TAG = "BLINK#REQUEST#TAG"
    internal var onNavigation: ((fragment: Fragment) -> Unit)? = null
    private val interceptors = Interceptors()

    /**
     * 添加拦截器
     */
    @JvmStatic
    fun add(interceptor: Interceptor) {
        interceptors.add(interceptor)
    }

    /**
     * 移除拦截器
     */
    @JvmStatic
    fun remove(interceptor: Interceptor) {
        interceptors.remove(interceptor)
    }

    fun navigation(
        uri: String,
        from: Fragment? = null,
        onResult: ((Bundle?) -> Unit)? = null
    ) {
        Uri.parse(uri)
        RouteMap.get(uri)?.let {
            from.doNavigation(it, from, onResult)
        }
    }

    private val onResults = mutableMapOf<String, (Bundle?) -> Unit>()

    fun navigation(
        fragment: Fragment,
        from: Fragment? = null,
        onResult: ((Bundle?) -> Unit)? = null
    ) {
        from.doNavigation(fragment, from, onResult)
    }

    private fun Fragment?.doNavigation(
        fragment: Fragment,
        from: Fragment? = null,
        onResult: ((Bundle?) -> Unit)? = null
    ) {
        onNavigation?.invoke(BlinkContainerFragment().apply {
            attach(interceptors.process(this@doNavigation, fragment)?.apply {
                val requestTag = from?.generateFragmentTag ?: UUID.randomUUID().toString()
                onResult?.let {
                    onResults[requestTag] = onResult
                }
                arguments = (arguments ?: Bundle()).apply {
                    putString(REQUEST_TAG, requestTag)
                }
            }!!)
        })
    }

    fun returnResult(fragment: Fragment, result: Bundle?) {
        fragment.arguments?.getString(REQUEST_TAG)?.let {
            onResults.remove(it)?.invoke(result)
        }

    }
}
