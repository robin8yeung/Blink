package com.seewo.blink.example.fragment

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seewo.blink.annotation.BlinkUri
import com.seewo.blink.example.Uris
import com.seewo.blink.example.databinding.FragmentTempBinding
import com.seewo.blink.example.ktx.toast
import com.seewo.blink.fragment.annotation.Orientation
import com.seewo.blink.fragment.blink

@BlinkUri(Uris.SINGLE_TOP_FRAGMENT)
@Orientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
class SingleTopFragment: com.seewo.blink.fragment.mode.SingleTopFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentTempBinding.inflate(inflater, container, false).apply {
        text.text = "当前页面为SingleTop页面"
        next.text = "跳转到本页面"
        next.setOnClickListener {
            blink(Uris.SINGLE_TOP_FRAGMENT)
        }
    }.root

    override fun onNewArguments(arguments: Bundle?) {
        super.onNewArguments(arguments)
        Log.e("BLINK", "onNewArguments >> arguments: $arguments")
        toast("onNewArguments >> arguments: $arguments")
    }
}