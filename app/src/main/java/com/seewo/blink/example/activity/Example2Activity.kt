package com.seewo.blink.example.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.seewo.blink.annotation.BlinkUri
import com.seewo.blink.blink
import com.seewo.blink.createBlinkIntent
import com.seewo.blink.example.Uris
import com.seewo.blink.example.databinding.ActivityExample2Binding

@BlinkUri("blink://navigator/example2")
class Example2Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityExample2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.next.setOnClickListener {
            blink(Uris.NOT_EXIST).exceptionOrNull()?.let {
                Log.e("BLINK", it.message, it)
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
            Log.e("robin", "resolve ${packageManager.resolveActivity(Uris.NOT_EXIST.createBlinkIntent()!!, PackageManager.MATCH_DEFAULT_ONLY)}")
        }
        binding.result.setOnClickListener {
            setResult(Activity.RESULT_OK, Intent().putExtra("result", "See you"))
            finish()
        }
    }
}