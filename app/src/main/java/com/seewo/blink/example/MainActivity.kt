package com.seewo.blink.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.seewo.blink.blinking
import com.seewo.blink.example.databinding.ActivityMainBinding
import com.seewo.blink.example.fragment.FragmentContainerActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.button.setOnClickListener {
            blinking("blink://example/activity")
        }
        binding.buttonFragment.setOnClickListener {
            FragmentContainerActivity.start(it.context)
        }
    }
}