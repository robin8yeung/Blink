package com.seewo.blink.example.interceptor

import android.content.Context
import android.content.Intent
import android.util.Log
import com.seewo.blink.example.Uris
import com.seewo.blink.interceptor.Interceptor
import com.seewo.blink.interrupt

class ExampleInterceptor: Interceptor {

    override fun process(context: Context, intent: Intent) {
        if (intent.data?.path == Uris.EXAMPLE_2.path) {
            interrupt("Interrupt Example 2")
        }
    }
}