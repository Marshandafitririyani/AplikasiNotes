package com.maruchan.notes.ui.login

import androidx.biometric.BiometricPrompt
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.isEmptyRequired
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.textOf
import com.maruchan.notes.R
import com.maruchan.notes.base.BaseActivity
import com.maruchan.notes.const.Const
import com.maruchan.notes.databinding.ActivityLoginBinding
import com.maruchan.notes.ui.home.activity.HomeActivity
import com.maruchan.notes.ui.register.RegisterActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>(R.layout.activity_login) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.tvSignUp.setOnClickListener {
            openActivity<RegisterActivity> ()
        }

        binding.btnBiometrik.setOnClickListener {
         showBiometricPrompt()
        }


        binding.btnLogin.setOnClickListener {
            if (binding.etEmailOrPhoneLogin.isEmptyRequired(R.string.label_must_fill) ||
                binding.etLoginPassword.isEmptyRequired(R.string.label_must_fill)
            ) {
                return@setOnClickListener
            }
            val emailOrPhone = binding.etEmailOrPhoneLogin.textOf()
            val password = binding.etLoginPassword.textOf()


            viewModel.login(emailOrPhone, password)
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> {
                                binding.progressBar.visibility = View.VISIBLE
                                binding.loginForm.visibility = View.GONE
                            }
                            ApiStatus.SUCCESS -> {
                                binding.progressBar.visibility = View.GONE
                                binding.loginForm.visibility = View.VISIBLE
                                openActivity<HomeActivity>()
                                finish()
                            }
                            else -> loadingDialog.setResponse(it.message ?: return@collect)
                        }
                    }
                }
            }
        }
    }
    private fun showBiometricPrompt() {
        val builder = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Authentication")
            .setSubtitle("Enter biometric credentials to proceed.")
            .setDescription("Input your Fingerprint or FaceID to ensure it's you!")
            .setNegativeButtonText("Cancel")
        val promptInfo = builder.build()
        val biometricPrompt= initBiometricPrompt{
            viewModel.login(session.getString(Const.BIOMETRIC.EMAILORPHONE), session.getString(Const.BIOMETRIC.PASSWORD))
        }
        biometricPrompt.authenticate(promptInfo)
    }

    private fun initBiometricPrompt(listener: (Boolean)-> Unit): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(this)

        val callback = object : BiometricPrompt.AuthenticationCallback(){
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                listener(true)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                listener(false)
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                listener(false)
            }
        }

        return BiometricPrompt(this,executor, callback)

    }
}