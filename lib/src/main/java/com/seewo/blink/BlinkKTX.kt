package com.seewo.blink

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.core.app.ActivityOptionsCompat
import com.seewo.blink.interceptor.Interceptor
import com.seewo.blink.interceptor.InterruptedException


/**
 * 添加拦截器
 */
fun Interceptor.attach() {
    Blink.add(this)
}

/**
 * 移除拦截器
 */
fun Interceptor.detach() {
    Blink.remove(this)
}

/**
 * 通过uri创建UriNavigator使用的Intent
 */
fun Uri.createBlinkIntent() = Blink.createIntent(this)

/**
 * @param uri 字符串类型的Uri
 * @param options 控制跳转动画
 * @param onResult ActivityResult回调。
 * 注意：对于Context不是FragmentActivity的情况设置回调，可能会导致共享元素动画异常
 * @return 执行结果，可能存在以下两种异常
 * - ActivityNotFoundException 无法找到uri对应的Activity
 * - 自定义异常 路由被拦截
 */
fun Context.blink(
    uri: String,
    options: ActivityOptionsCompat? = null,
    onResult: ActivityResultCallback<ActivityResult>? = null
): Result<Unit> = Blink.navigationForResult(this, uri, options, onResult)

/**
 * @param uri Uri类型的Uri
 * @param options 控制跳转动画
 * @param onResult ActivityResult回调。
 * 注意：对于Context不是FragmentActivity的情况设置回调，可能会导致共享元素动画异常
 * @return 执行结果，可能存在以下两种异常
 * - ActivityNotFoundException 无法找到uri对应的Activity
 * - 自定义异常 路由被拦截
 */
fun Context.blink(
    uri: Uri,
    options: ActivityOptionsCompat? = null,
    onResult: ActivityResultCallback<ActivityResult>? = null
): Result<Unit> = Blink.navigationForResult(this, uri, options, onResult)

/**
 * @param intent 建议使用 Uri.createIntent()方法来创建Intent
 * @param options 控制跳转动画
 * @param onResult ActivityResult回调。
 * 注意：对于Context不是FragmentActivity的情况设置回调，可能会导致共享元素动画异常
 * @return 执行结果，可能存在以下两种异常
 * - ActivityNotFoundException 无法找到uri对应的Activity
 * - NavigationInterruptedException 路由被拦截
 * @see createBlinkIntent
 */
fun Context.blink(
    intent: Intent,
    options: ActivityOptionsCompat? = null,
    onResult: ActivityResultCallback<ActivityResult>? = null
): Result<Unit> = Blink.navigationForResult(this, intent, options, onResult)

/**
 * 拦截器拦截路由建议抛出以下异常
 */
fun Interceptor.interrupt(msg: String? = null) {
    throw InterruptedException(this, msg)
}