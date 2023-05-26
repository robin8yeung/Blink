package com.seewo.blink.example.fragment

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.seewo.blink.annotation.BlinkUri
import com.seewo.blink.example.Uris
import com.seewo.blink.example.bean.Navigator
import com.seewo.blink.example.databinding.FragmentReturnResultBinding
import com.seewo.blink.fragment.annotation.CustomAnimations
import com.seewo.blink.fragment.annotation.Orientation
import com.seewo.blink.fragment.enumParams
import com.seewo.blink.fragment.pop

@BlinkUri(Uris.RETURN_RESULT_FRAGMENT)
@Orientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
@CustomAnimations
class ReturnResultFragment: Fragment() {
    private val navigator by enumParams<Navigator>("navigator")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentReturnResultBinding.inflate(inflater, container, false).apply {
        params.text = "本页面为横屏\n欢迎使用: $navigator"
        result.setOnClickListener {
            pop(Bundle().apply {
                putString("result", "再见")
            })
        }
        finish.setOnClickListener {
            pop()
        }
    }.root
}