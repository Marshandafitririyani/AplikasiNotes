package com.maruchan.notes.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.extension.openActivity
import com.crocodic.core.helper.ImagePreviewHelper
import com.maruchan.notes.R
import com.maruchan.notes.base.BaseActivity
import com.maruchan.notes.databinding.ActivityProfileBinding
import com.maruchan.notes.ui.editprofile.EditProfileActivity
import com.maruchan.notes.ui.setting.SettingActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileActivity :
    BaseActivity<ActivityProfileBinding, ProfileViewModel>(R.layout.activity_profile) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observe()
        initClick()

    }

    private fun initClick() {
        binding.ibBackRedProfile.setOnClickListener {
            finish()
        }
        binding.btnEditProfile.setOnClickListener {
            profile()
        }
        binding.ibLogoutProfile.setOnClickListener {
            openActivity<SettingActivity> ()
        }
        binding.imgProfile.setOnClickListener {
            ImagePreviewHelper(this).show(binding.imgProfile, binding.user?.photo)
        }
    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.user.collect {
                        binding.user = it
                    }
                }
            }

        }
    }

    private fun profile() {
        val editProfile = Intent(this, EditProfileActivity::class.java).apply {
            putExtra("username", binding.user?.name)
            putExtra("photo", binding.user?.photo)

        }
        startActivity(editProfile)
    }

}