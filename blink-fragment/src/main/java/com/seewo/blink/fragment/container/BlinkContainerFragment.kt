package com.seewo.blink.fragment.container

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import androidx.fragment.app.commit
import com.seewo.blink.fragment.Blink
import com.seewo.blink.fragment.R
import com.seewo.blink.fragment.annotation.Background
import com.seewo.blink.fragment.annotation.CustomAnimations
import com.seewo.blink.fragment.annotation.KeepAlive
import com.seewo.blink.fragment.annotation.Orientation
import com.seewo.blink.fragment.annotation.SystemUI
import com.seewo.blink.fragment.exception.FragmentNotFoundException
import com.seewo.blink.fragment.generateFragmentTag
import com.seewo.blink.fragment.mode.ReEnterFragment
import com.seewo.blink.fragment.utils.hideNavigationBar
import com.seewo.blink.fragment.utils.hideStatusBar
import com.seewo.blink.fragment.utils.setStatusBarTransparent
import com.seewo.blink.fragment.utils.showNavigationBar
import com.seewo.blink.fragment.utils.showStatusBar


@SuppressLint("SourceLockedOrientationActivity")
internal class BlinkContainerFragment : Fragment() {
    lateinit var fragmentTag: String
    private var orientation: Int? = null
    private var systemUISettings: SystemUI = SystemUI()
    private var backgroundColor: Int = Color.WHITE
    private var root: View? = null
    var customAnimation: CustomAnimations? = null
        private set

    var keepAlive: KeepAlive? = null
        private set

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.blink_container_fragment, container, false).apply {
        backgroundColor = kotlin.runCatching { windowBackground }.getOrNull() ?: Color.WHITE
        root = this
        root?.setBackgroundColor(backgroundColor)
    }!!

    private var pending: Runnable? = null
    private var result: Bundle? = null

    var fragment: Fragment? = null

    fun popResult(result: Bundle? = null) {
        this.result = result
        parentFragmentManager.popBackStack()
    }

    fun popTo(uri: String): Boolean {
        for (i in parentFragmentManager.backStackEntryCount - 1 downTo 0) {
            val name = parentFragmentManager.getBackStackEntryAt(i).name
            val tag = FragmentTag(name)
            if (tag.uri == uri) {
                parentFragmentManager.popBackStack(name, POP_BACK_STACK_INCLUSIVE)
                return true
            }
        }
        return false
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        pending?.run()
        pending = null
        mayUpdateFragmentSettings()
    }

    private fun mayUpdateFragmentSettings() {
        if (isAdded && !isHidden && userVisibleHint && isResumed) {
            orientation?.let { requireActivity().requestedOrientation = it }
            systemUISettings.run {
                requireActivity().setStatusBarTransparent(brightnessLight)
                if (hideStatusBar) {
                    requireActivity().hideStatusBar()
                } else {
                    requireActivity().showStatusBar()
                }
                if (hideNavigationBar) {
                    requireActivity().hideNavigationBar()
                } else {
                    requireActivity().showNavigationBar()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fragment?.let {
            Blink.returnResult(it, result)
        }
    }

    internal fun attach(fragment: Fragment) {
        fragment.checkException()
        this.fragment = fragment
        arguments = fragment.arguments
        fragmentTag = fragment.generateFragmentTag
        fragment::class.java.apply {
            getAnnotation(Orientation::class.java)?.let {
                orientation = it.value
            }
            getAnnotation(SystemUI::class.java)?.let { systemUISettings = it }
            getAnnotation(Background::class.java)?.let { backgroundColor = it.value }
            customAnimation = getAnnotation(CustomAnimations::class.java)
            keepAlive = getAnnotation(KeepAlive::class.java)
        }
        root?.setBackgroundColor(backgroundColor)
        if (isAdded) {
            doAttach(fragment)
        } else {
            pending = Runnable {
                doAttach(fragment)
            }
        }
    }

    private fun doAttach(fragment: Fragment) {
        childFragmentManager.commit {
            add(R.id.blink_container_fragment_root, fragment)
        }
    }

    private fun Fragment.checkException() {
        if (this is NullFragment) throw FragmentNotFoundException(this)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        mayUpdateFragmentSettings()
    }

    override fun onResume() {
        super.onResume()
        mayUpdateFragmentSettings()
    }

    fun onNewArguments(newArguments: Bundle?) {
        when (val currentFragment =
            childFragmentManager.findFragmentById(R.id.blink_container_fragment_root)) {
            is ReEnterFragment -> currentFragment.onNewArguments(newArguments)
        }
    }

    private val windowBackground: Int
        get() {
            val typedValue = TypedValue()
            requireContext().theme.resolveAttribute(
                android.R.attr.windowBackground,
                typedValue,
                true
            )
            val colorRes = typedValue.resourceId
            return requireContext().resources.getColor(colorRes)
        }
}