package com.seewo.blink.example.fragment

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.seewo.blink.annotation.BlinkUri
import com.seewo.blink.example.R
import com.seewo.blink.fragment.annotation.Orientation
import com.seewo.blink.fragment.annotation.SystemUI
import com.seewo.blink.fragment.blink
import com.seewo.blink.fragment.mode.SingleTaskFragment
import com.seewo.blink.fragment.pop

@BlinkUri("blink://route/f1")
@Orientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
@SystemUI(brightLight = false)
class Fragment1: SingleTaskFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_main, container, false).apply {
        findViewById<TextView>(R.id.name).text = arguments?.getString("name") ?: ""
        findViewById<View>(R.id.button).setOnClickListener {
            blink("blink://route/f2").exceptionOrNull()?.let { Log.e("robin", it.message, it) }
        }
        findViewById<View>(R.id.back).setOnClickListener {
            pop()
        }
        setBackgroundResource(R.color.purple_500)
    }

    override fun onNewArguments(arguments: Bundle?) {
        Log.i("tag", "onNewArguments $arguments")
    }
}