package com.seewo.blink.stub

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

class StubFragment : Fragment() {
    private val requestCode: Int by lazy { arguments?.getInt(ResultHolder.REQUEST_CODE) ?: 0 }
    private val stubData: StubData? by lazy { ResultHolder.resultMap.remove(requestCode) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val stubData = stubData
        if (stubData == null) {
            finish()
            return
        }
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) {
            stubData.callback.onActivityResult(it)
            finish()
        }.launch(stubData.intent, stubData.options)
    }

    private fun finish() {
        parentFragmentManager.run {
            beginTransaction().remove(this@StubFragment).commit()
        }
    }

    companion object {
        const val TAG = "Blink.StubFragment"
    }
}