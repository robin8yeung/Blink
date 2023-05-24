package com.seewo.blink.stub

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.FragmentActivity
import kotlin.random.Random

internal object ResultHolder {
    const val REQUEST_CODE = "Blink.RequestCode"

    val resultMap = mutableMapOf<Int, StubData>()
    private val random = Random.Default

    fun launch(
        context: Context,
        intent: Intent,
        options: ActivityOptionsCompat?,
        onResult: ActivityResultCallback<ActivityResult>
    ) {
        val requestCode = random.nextInt(1, Int.MAX_VALUE)
        kotlin.runCatching {
            if (context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) == null) {
                throw ActivityNotFoundException("No Activity found to handle $intent")
            }

            context.startActivity(Intent(context, StubActivity::class.java).apply {
                if (context !is Activity) addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                resultMap[requestCode] = StubData(intent, options, onResult)
                putExtra(REQUEST_CODE, requestCode)
            })
        }.apply {
            exceptionOrNull()?.let {
                resultMap.remove(requestCode)
                throw it
            }
        }
    }

    fun launchWithFragment(
        context: FragmentActivity,
        intent: Intent,
        options: ActivityOptionsCompat?,
        onResult: ActivityResultCallback<ActivityResult>
    ) {
        val requestCode = random.nextInt(1, Int.MAX_VALUE)
        kotlin.runCatching {
            if (context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) == null) {
                throw ActivityNotFoundException("No Activity found to handle $intent")
            }
            context.supportFragmentManager.apply {
                beginTransaction().add(StubFragment().apply {
                    resultMap[requestCode] = StubData(intent, options, onResult)
                    arguments = Bundle().apply {
                        putInt(REQUEST_CODE, requestCode)
                    }
                }, StubFragment.TAG).commit()
            }

        }.apply {
            exceptionOrNull()?.let {
                resultMap.remove(requestCode)
                context.supportFragmentManager.findFragmentByTag(StubFragment.TAG)?.runCatching {
                    context.supportFragmentManager.beginTransaction().remove(this).commit()
                }
                throw it
            }
        }
    }
}