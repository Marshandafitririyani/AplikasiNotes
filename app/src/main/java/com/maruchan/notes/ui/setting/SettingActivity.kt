package com.maruchan.notes.ui.setting

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.biometric.BiometricManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.*
import com.maruchan.notes.R
import com.maruchan.notes.base.BaseActivity
import com.maruchan.notes.const.Const
import com.maruchan.notes.databinding.ActivityLoginBinding
import com.maruchan.notes.databinding.ActivitySettingBinding
import com.maruchan.notes.ui.login.LoginActivity
import com.maruchan.notes.ui.login.LoginViewModel
import com.maruchan.notes.ui.splash.SplashActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingActivity :
    BaseActivity<ActivitySettingBinding, SettingViewModel>(R.layout.activity_setting) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observe()
        initClick()


    }

    private fun initClick() {
        binding.hasBiometric = hasBiometricCapability()
        binding.enableBiometric = session.getBoolean(Const.BIOMETRIC.BIOMETRIC)

        binding.btnBiometric.setOnCheckedChangeListener { buttonView, isChecked ->
            session.setValue(Const.BIOMETRIC.BIOMETRIC, isChecked)

        }
        binding.btnLogout.setOnClickListener {
            logout()
        }
        binding.btnPassword.setOnClickListener {
            editPassword()
        }
        binding.btnBackSetting.setOnClickListener{
            finish()
        }
        binding.switchDarkMode.setOnClickListener {
            binding.root.snacked(R.string.available)
        }

    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
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
                launch {
                    viewModel.editPassword.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> loadingDialog.show("Save Password")
                            ApiStatus.SUCCESS -> {
                                loadingDialog.dismiss()
//                                finish()
                            }
                            else -> loadingDialog.setResponse(it.message ?: return@collect)
                        }
                    }
                }
            }

        }
    }

    private fun hasBiometricCapability(): Boolean {
        val biometricManager = BiometricManager.from(this)
        return biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS
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