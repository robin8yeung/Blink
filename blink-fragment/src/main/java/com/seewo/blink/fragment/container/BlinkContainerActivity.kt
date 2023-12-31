package com.seewo.blink.fragment.container

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentManager.BackStackEntry
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import com.seewo.blink.fragment.Blink
import com.seewo.blink.fragment.R
import com.seewo.blink.fragment.generateFragmentTag
import com.seewo.blink.fragment.mode.LaunchMode.NORMAL
import com.seewo.blink.fragment.mode.LaunchMode.SINGLE_TASK
import com.seewo.blink.fragment.mode.LaunchMode.SINGLE_TOP
import com.seewo.blink.fragment.mode.ReEnterFragment

abstract class BlinkContainerActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.blink_fragment_activity)
        Blink.onNavigation = this::load
        if (savedInstanceState != null) return
        startFragment()?.let {
            supportFragmentManager.commit {
                add(R.id.blink_container_activity_root, BlinkContainerFragment().apply {
                    attach(it)
                })
            }
        }
    }

    /**
     * 设置入口Fragment，若为null则不设置入口Fragment，后续Fragment通过路由跳转
     */
    protected abstract fun startFragment(): Fragment?

    override fun onDestroy() {
        super.onDestroy()
        Blink.onNavigation = null
    }

    private fun FragmentManager.findBackStackEntry(block: (BackStackEntry) -> Boolean): BackStackEntry? {
        for (i in 0 until backStackEntryCount) {
            val entry = getBackStackEntryAt(i)
            if (block(entry)) {
                return entry
            }
        }
        return null
    }

    private fun load(fragment: Fragment) {
        supportFragmentManager.commit {
            val customAnimation = (fragment as? BlinkContainerFragment)?.customAnimation
            if (customAnimation == null) {
                setCustomAnimations(
                    R.anim.enter_from_right, R.anim.fade_out,
                    R.anim.fade_in, R.anim.exit_to_right
                )
            } else {
                setCustomAnimations(
                    customAnimation.enter, customAnimation.exit,
                    customAnimation.popEnter, customAnimation.popExit)
            }
            val fragmentTag = FragmentTag((fragment as? BlinkContainerFragment)?.fragmentTag
                ?: fragment.generateFragmentTag)
            when (fragmentTag.launchMode) {
                NORMAL -> launchFragment(fragment)
                SINGLE_TOP -> handleSingleTop(fragment)
                SINGLE_TASK -> handleSingleTask(fragment)
            }
        }
    }

    private fun FragmentTransaction.handleSingleTask(fragment: Fragment) {
        val oldFragmentEntry =
            supportFragmentManager.findBackStackEntry {
                val realFragment = (fragment as? BlinkContainerFragment)?.fragment
                    ?: fragment
                FragmentTag(it.name).className == realFragment::class.java.canonicalName
            }
        if (oldFragmentEntry != null) {
            supportFragmentManager.popBackStackImmediate(oldFragmentEntry.name, 1)
            val reEnteringFragment =
                supportFragmentManager.findFragmentById(R.id.blink_container_activity_root)
            (reEnteringFragment as? BlinkContainerFragment)?.onNewArguments(
                fragment.arguments
            ) ?: (reEnteringFragment as? ReEnterFragment)?.onNewArguments(fragment.arguments)
        } else {
            launchFragment(fragment)
        }
    }

    private fun FragmentTransaction.handleSingleTop(fragment: Fragment) {
        val currentFragment =
            (currentFragment as? BlinkContainerFragment)?.fragment ?: currentFragment
        if (currentFragment != null) {
            val lastBackStackEntry =
                FragmentTag((currentFragment as? BlinkContainerFragment)?.fragmentTag ?: currentFragment.generateFragmentTag)
            val currentBackStackEntry = FragmentTag((fragment as? BlinkContainerFragment)?.fragmentTag ?: fragment.generateFragmentTag)
            if (lastBackStackEntry.className == currentBackStackEntry.className) {
                (this@BlinkContainerActivity.currentFragment as? BlinkContainerFragment)?.onNewArguments(fragment.arguments)
            } else {
                launchFragment(fragment)
            }
        } else {
            launchFragment(fragment)
        }
    }

    private fun FragmentTransaction.launchFragment(fragment: Fragment) {
        currentFragment?.let {
            val keepAlive = (fragment as? BlinkContainerFragment)?.keepAlive
            if (keepAlive?.value != true) {
                remove(it)
            } else {
                hide(it)
            }
            if (it is BlinkContainerFragment) {
                addToBackStack(it.fragmentTag)
            } else {
                addToBackStack(it.generateFragmentTag)
            }
        }
        add(R.id.blink_container_activity_root, fragment, fragment::class.java.canonicalName)
    }

    private val currentFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.blink_container_activity_root)

}