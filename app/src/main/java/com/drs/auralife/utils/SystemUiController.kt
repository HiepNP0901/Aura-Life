package com.drs.auralife.utils

import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController

@Suppress("DEPRECATION")
class SystemUiController {
    companion object {
        fun showSystemUI(window: Window) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val windowInsetsController = window.insetsController
                windowInsetsController?.show(WindowInsets.Type.systemBars())
            }
            else {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            }
        }

        fun autoHideSystemUI(window: Window) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val windowInsetsController = window.insetsController
                windowInsetsController?.let {
                    it.hide(WindowInsets.Type.systemBars())
                    it.systemBarsBehavior =
                        WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            }
            else {
                window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
            }
        }
    }
}