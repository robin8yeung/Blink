package com.seewo.blink.example.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.seewo.blink.annotation.BlinkUri
import com.seewo.blink.enumParams
import com.seewo.blink.example.Uris
import com.seewo.blink.example.bean.Navigator
import com.seewo.blink.example.databinding.ActivityReturnResultBinding

@BlinkUri(Uris.RETURN_RESULT)
class ReturnResultActivity : AppCompatActivity() {
    private val navigator by enumParams<Navigator>("navigator")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityReturnResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.params.text = "欢迎使用: $navigator"
        binding.result.setOnClickListener {
            setResult(Activity.RESULT_OK, Intent().putExtra("result", "再见"))
            finish()
        }
        binding.finish.setOnClickListener {
            finish()
        }
    }
}