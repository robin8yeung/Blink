package com.seewo.blink

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.FragmentActivity
import com.seewo.blink.interceptor.Interceptor
import com.seewo.blink.interceptor.Interceptors
import com.seewo.blink.stub.ResultHolder

/**
 * 基于uri来实现Activity路由
 *
 * - 路由功能
 * - Intent修改
 * - 路由结果回调 通过Result
 * - 页面结果回调
 * - 拦截器
 */
object Blink {
    private val interceptors = Interceptors()

    /**
     * Intent的action，默认如下，业务可自定义
     */
    var action = "blink.action.VIEW"

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

    /**
     * 通过uri创建UriNavigator使用的Intent
     */
    @JvmStatic
    fun createIntent(uri: Uri) = Intent(action).apply {
        data = uri
    }

    /**
     * 主要为java提供
     * @return 执行结果，可能存在以下两种异常
     * - ActivityNotFoundException 无法找到uri对应的Activity
     * - 自定义异常 路由被拦截
     */
    @JvmStatic
    fun navigation(
        context: Context,
        uri: String,
    ): Result<Unit> = navigationForResult(context, uri)

    /**
     * @return 执行结果，可能存在以下两种异常
     * - ActivityNotFoundException 无法找到uri对应的Activity
     * - 自定义异常 路由被拦截
     */
    @JvmStatic
    fun navigationForResult(
        context: Context,
        uri: String,
        options: ActivityOptionsCompat? = null,
        onResult: ActivityResultCallback<ActivityResult>? = null
    ): Result<Unit> = navigationForResult(context, Uri.parse(uri), options, onResult)

    /**
     * 主要为java提供
     * @return 执行结果，可能存在以下两种异常
     * - ActivityNotFoundException 无法找到uri对应的Activity
     * - 自定义异常 路由被拦截
     */
    @JvmStatic
    fun navigation(
        context: Context,
        uri: Uri,
    ): Result<Unit> = navigationForResult(context, uri)

    /**
     * @return 执行结果，可能存在以下两种异常
     * - ActivityNotFoundException 无法找到uri对应的Activity
     * - 自定义异常 路由被拦截
     */
    @JvmStatic
    fun navigationForResult(
        context: Context,
        uri: Uri,
        options: ActivityOptionsCompat? = null,
        onResult: ActivityResultCallback<ActivityResult>? = null
    ): Result<Unit> = navigationForResult(context, createIntent(uri), options, onResult)

    /**
     * 主要为java提供
     * @return 执行结果，可能存在以下两种异常
     * - ActivityNotFoundException 无法找到uri对应的Activity
     * - NavigationInterruptedException 路由被拦截
     */
    @JvmStatic
    fun navigation(
        context: Context,
        intent: Intent,
    ): Result<Unit> = navigationForResult(context, intent)

    /**
     * @return 执行结果，可能存在以下两种异常
     * - ActivityNotFoundException 无法找到uri对应的Activity
     * - NavigationInterruptedException 路由被拦截
     */
    @JvmStatic
    fun navigationForResult(
        context: Context,
        intent: Intent,
        options: ActivityOptionsCompat? = null,
        onResult: ActivityResultCallback<ActivityResult>? = null
    ): Result<Unit> = kotlin.runCatching {
        doNavigation(context, intent, options, onResult)
    }

    private fun doNavigation(
        context: Context,
        intent: Intent,
        options: ActivityOptionsCompat?,
        onResult: ActivityResultCallback<ActivityResult>?
    ) {
        interceptors.process(context, intent)
        if (onResult == null) {
            context.startActivity(intent.apply {
                if (context !is Activity) {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }, options?.toBundle())
        } else {
            when (context) {
                is FragmentActivity -> {
                    ResultHolder.launchWithFragment(context, intent, options, onResult)
                }
                else -> {
                    ResultHolder.launch(context, intent, options, onResult)
                }
            }
        }
    }
}