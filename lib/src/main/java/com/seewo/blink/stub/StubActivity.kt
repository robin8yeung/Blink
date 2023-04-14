package com.seewo.blink.stub

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult

class StubActivity : Activity() {
    private val requestCode: Int by lazy { intent.getIntExtra(ResultHolder.REQUEST_CODE, 0) }
    private val stubData: StubData? by lazy { ResultHolder.resultMap.remove(requestCode) }

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(0, 0)
        super.onCreate(savedInstanceState)
        val data = this.stubData
        if (data == null) {
            finish()
        } else {
            Log.e("robin", "start ${this.requestCode}")
            startActivityForResult(data.intent, requestCode, data.options?.toBundle())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.e("robin", "requestCode: $requestCode ${this.requestCode}")
        if (requestCode == this.requestCode) {
            stubData?.callback?.onActivityResult(ActivityResult(resultCode, data))
            finish()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }
}