package com.maruchan.notes.ui.editprofile

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.*
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
//        binding.user
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
        binding.imgSettingEditProfile.setOnClickListener {
            editPassword()
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
                launch {
                    viewModel.editPassword.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> loadingDialog.show("Save Password")
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
            binding.root.snacked("Username tidak boleh kosong")
            return
        }

        if (photoFile == null) {
            if (name == username) {
                binding.root.snacked("Tidak ada yang berubah")
                return
            }
            viewModel.updateProfile(name)
        } else {
            lifecycleScope.launch {
                tos("Tunggu")
                if (photoFile != null) {
                    viewModel.updateProfileWithPhoto(name, photoFile ?: return@launch)
                    tos("photo")
                }
            }
        }

    }

    private fun gallery() {
        activityLauncher.openGallery(this) { file, exception ->

            //Todo: Mengambil filenya
            val bitmap = BitmapFactory.decodeFile(file?.absolutePath)
            //Todo: File di compress
            val resizeBitmap = BitmapHelper.resizeBitmap(bitmap, 512f)

            //Todo: PhotoFile mendapat file yang telah dicompress
            photoFile = createImageFile().also { it.writeBitmap(resizeBitmap) }
            binding.imageFileEdit = photoFile
            //Todo: Kemudian file yang asli atau yang belum tercompress di hapus
            file?.delete()
        }
    }

    private fun editPassword() {
        val builder = AlertDialog.Builder(this)
        val customLayout: View = layoutInflater.inflate(R.layout.edit_password_popup, null)
        builder.setView(customLayout)
        builder.setView(customLayout)

        builder.setCancelable(true)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(R.color.transparent)

        val editTextPasswordNw = customLayout.findViewById<EditText>(R.id.et_password_new)
        val editConfirmPassword = customLayout.findViewById<EditText>(R.id.et_confirmasi_password)
        val textConfirmPassword = customLayout.findViewById<TextView>(R.id.tv_password_not_match)

        val btnSave = customLayout.findViewById<TextView>(R.id.btn_save_password)
        btnSave.setOnClickListener {

            if (editTextPasswordNw.isEmptyRequired(R.string.label_must_fill) ||
                editConfirmPassword.isEmptyRequired(R.string.label_must_fill)
            ) {
                return@setOnClickListener
            }

            val newPassword = editTextPasswordNw.textOf()
            val passwordConfirmation = editConfirmPassword.textOf()

            if (newPassword != passwordConfirmation) {
                textConfirmPassword.visibility = View.VISIBLE
            } else {
                textConfirmPassword.visibility = View.GONE
                viewModel.editPassword(newPassword, passwordConfirmation)
            }
        }
        dialog.show()
    }

}
