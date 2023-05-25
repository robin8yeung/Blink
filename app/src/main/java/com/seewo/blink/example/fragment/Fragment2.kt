package com.seewo.blink.example.fragment

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.seewo.blink.annotation.BlinkUri
import com.seewo.blink.example.R
import com.seewo.blink.fragment.annotation.CustomAnimations
import com.seewo.blink.fragment.annotation.Orientation
import com.seewo.blink.fragment.annotation.SystemUI
import com.seewo.blink.fragment.blink
import com.seewo.blink.fragment.pop

@BlinkUri(value = ["blink://route/f2", "blink://route/f3"])
@Orientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
@CustomAnimations
@SystemUI(hideNavigationBar = true, )
class Fragment2: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_main, container, false).apply {
        findViewById<TextView>(R.id.name).text = "2"
        findViewById<View>(R.id.button2).setOnClickListener {
            blink("blink://route/f2?a=1") {
                Log.e("robin", "return >> $it")
            }
        }
        findViewById<View>(R.id.button).setOnClickListener {
            blink("blink://route/f1")
        }
        findViewById<View>(R.id.button3).apply {
            visibility = View.VISIBLE
            setOnClickListener {
                pop(Bundle().apply {
                    putString("result", "result")
                })
            }
        }
        findViewById<View>(R.id.back).setOnClickListener {
            pop()
        }
        setBackgroundResource(R.color.teal_200)
    }
}