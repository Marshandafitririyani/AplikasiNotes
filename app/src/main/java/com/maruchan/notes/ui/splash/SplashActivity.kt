package com.maruchan.notes.ui.splash

import android.os.Bundle
import com.crocodic.core.base.viewmodel.CoreViewModel
import com.crocodic.core.extension.openActivity
import com.maruchan.notes.R
import com.maruchan.notes.base.BaseActivity
import com.maruchan.notes.data.room.user.UserDao
import com.maruchan.notes.databinding.ActivitySplashBinding
import com.maruchan.notes.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity :
    BaseActivity<ActivitySplashBinding, CoreViewModel>(R.layout.activity_splash) {

    @Inject
    lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnEnter.setOnClickListener {
            openActivity<LoginActivity> ()
        }

    }
}
