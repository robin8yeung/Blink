package com.seewo.blink.example.fragment

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.seewo.blink.annotation.BlinkUri
import com.seewo.blink.example.Uris
import com.seewo.blink.example.databinding.FragmentFinalBinding
import com.seewo.blink.fragment.annotation.Orientation
import com.seewo.blink.fragment.popTo

@BlinkUri(Uris.FINAL_FRAGMENT)
@Orientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
class FinalFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentFinalBinding.inflate(inflater, container, false).apply {
        next.setOnClickListener {
            // 直接回退到HomeFragment
            popTo("")
        }
    }.root

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // 拦截返回键，直接回退到HomeFragment
        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 直接回退到HomeFragment
                popTo("")
            }
        })
    }
}