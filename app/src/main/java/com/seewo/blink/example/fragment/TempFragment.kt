package com.seewo.blink.example.fragment

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.seewo.blink.annotation.BlinkUri
import com.seewo.blink.example.Uris
import com.seewo.blink.example.databinding.FragmentTempBinding
import com.seewo.blink.fragment.R
import com.seewo.blink.fragment.annotation.CustomAnimations
import com.seewo.blink.fragment.annotation.Orientation
import com.seewo.blink.fragment.blinking

@BlinkUri(Uris.TEMP_FRAGMENT)
@Orientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
@CustomAnimations(
    enter = R.anim.enter_from_bottom, exit = R.anim.fade_out,
    popEnter = R.anim.fade_in, popExit = R.anim.exit_to_bottom)
class TempFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentTempBinding.inflate(inflater, container, false).apply {
        next.setOnClickListener {
            blinking(Uris.FINAL_FRAGMENT)
        }
    }.root
}