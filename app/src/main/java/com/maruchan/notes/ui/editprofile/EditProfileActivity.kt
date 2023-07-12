package com.maruchan.notes.ui.editprofile

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.createImageFile
import com.crocodic.core.extension.openGallery
import com.crocodic.core.extension.snacked
import com.crocodic.core.extension.textOf
import com.crocodic.core.helper.BitmapHelper
import com.maruchan.notes.R
import com.maruchan.notes.base.BaseActivity
import com.maruchan.notes.data.helper.ViewBindingHelper.Companion.writeBitmap
import com.maruchan.notes.databinding.ActivityEditProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class EditProfileActivity :
    BaseActivity<ActivityEditProfileBinding, EditProfileViewModel>(R.layout.activity_edit_profile) {

    private var photo: String? = null
    private var photoFile: File? = null
    private var username: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initClick()
        observe()

        username = intent.getStringExtra("username")
        photo = intent.getStringExtra("photo")
        binding.edit = this
        binding.photo = photo
        binding.etNameEditProfile.setText(username)

    }

    private fun initClick() {
        binding.imgBackEditProfile.setOnClickListener {
            finish()
        }
        binding.btnEditProfile.setOnClickListener {
            validateForm()
        }
        binding.imgEditProfile.setOnClickListener {
            gallery()
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
                            ApiStatus.LOADING -> loadingDialog.show(" Please Wait Save Profil")
                            ApiStatus.SUCCESS -> {
                                loadingDialog.dismiss()
                                finish()
                            }
                            else -> loadingDialog.setResponse(it.message ?: return@collect)
                        }
                    }


                }
            }
        }
    }

    private fun validateForm() {
        val name = binding.etNameEditProfile.textOf()

        if (name.isEmpty()) {
            binding.root.snacked(R.string.empty_username)
            return
        }

        if (photoFile == null) {
            if (name == username) {
                binding.root.snacked(R.string.nothing)
                return
            }
            viewModel.updateProfile(name)
        } else {
            lifecycleScope.launch {
                binding.root.snacked(R.string.compressing)
                if (photoFile != null) {
                    viewModel.updateProfileWithPhoto(name, photoFile ?: return@launch)
                }
            }
        }

    }

    private fun gallery() {
        activityLauncher.openGallery(this) { file, exception ->

            val bitmap = BitmapFactory.decodeFile(file?.absolutePath)
            val resizeBitmap = BitmapHelper.resizeBitmap(bitmap, 512f)

            photoFile = createImageFile().also { it.writeBitmap(resizeBitmap) }
            binding.imageFileEdit = photoFile
            file?.delete()
        }
    }


}
