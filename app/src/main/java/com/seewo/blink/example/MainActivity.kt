package com.seewo.blink.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.seewo.blink.attach
import com.seewo.blink.detach
import com.seewo.blink.example.databinding.ActivityMainBinding
import com.seewo.blink.example.fragment.FragmentContainerActivity
import com.seewo.blink.example.interceptor.LoggerInterceptor

class MainActivity : AppCompatActivity() {
    private val loggerInterceptor = LoggerInterceptor()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loggerInterceptor.attach()
        binding.button.setOnClickListener {
            FragmentContainerActivity.start(it.context)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        loggerInterceptor.detach()
    }
}