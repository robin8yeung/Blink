package com.seewo.blink.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.seewo.blink.blink
import com.seewo.blink.example.activity.interceptor.LoggerInterceptor
import com.seewo.blink.example.databinding.ActivityMainBinding
import com.seewo.blink.example.fragment.FragmentContainerActivity

class MainActivity : AppCompatActivity() {
    private val loggerInterceptor = LoggerInterceptor()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.button.setOnClickListener {
            blink("blink://example/activity")
        }
        binding.buttonFragment.setOnClickListener {
            FragmentContainerActivity.start(it.context)
        }
    }
}