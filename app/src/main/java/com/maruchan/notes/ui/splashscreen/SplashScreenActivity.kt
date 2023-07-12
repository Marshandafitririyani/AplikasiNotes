package com.maruchan.notes.ui.splashscreen

import android.os.Bundle
import androidx.core.content.ContextCompat
import com.crocodic.core.extension.openActivity
import com.maruchan.notes.R
import com.maruchan.notes.base.BaseActivity
import com.maruchan.notes.databinding.ActivitySplashScreenBinding
import com.maruchan.notes.ui.home.activity.HomeActivity
import com.maruchan.notes.ui.splash.SplashActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashScreenActivity :  BaseActivity<ActivitySplashScreenBinding, SplashScreenViewModel>(R.layout.activity_splash_screen) {


    override fun onCreate(savedInstanceState: Bundle?) {
        window.navigationBarColor = ContextCompat.getColor(this,R.color.yellow)
        super.onCreate(savedInstanceState)
        viewModel.splash {
            if (it) {
                openActivity<HomeActivity>()
            } else {
                openActivity<SplashActivity>()
            }
            finish()
        }

    }
}
