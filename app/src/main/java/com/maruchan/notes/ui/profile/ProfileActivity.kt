package com.maruchan.notes.ui.profile

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.clearNotification
import com.crocodic.core.extension.openActivity
import com.crocodic.core.helper.ImagePreviewHelper
import com.maruchan.notes.R
import com.maruchan.notes.base.BaseActivity
import com.maruchan.notes.const.Const
import com.maruchan.notes.databinding.ActivityProfileBinding
import com.maruchan.notes.ui.editprofile.EditProfileActivity
import com.maruchan.notes.ui.splash.SplashActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileActivity :
    BaseActivity<ActivityProfileBinding, ProfileViewModel>(R.layout.activity_profile) {
    private var isUser: Boolean = true
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
            logout()
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
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> {
                                loadingDialog.show()
                            }
                            ApiStatus.SUCCESS -> {
                                loadingDialog.dismiss()
                                clearNotification()
                                openActivity<SplashActivity> {
                                    finishAffinity()
                                }
                            }
                            ApiStatus.ERROR -> {
                                loadingDialog.setResponse(it.message ?: return@collect)
                            }
                            else -> loadingDialog.setResponse(it.message ?: return@collect)
                        }
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

    private fun logout() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        val customLayout: View = layoutInflater.inflate(R.layout.popup_logout, null)
        builder.setView(customLayout)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnLogout = customLayout.findViewById<TextView>(R.id.btn_yes)
        btnLogout.setOnClickListener {
            viewModel.logout()
        }

        val btnCancle = customLayout.findViewById<TextView>(R.id.btn_no)
        btnCancle.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}