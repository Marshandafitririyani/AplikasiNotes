package com.maruchan.notes.ui.register

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.isEmptyRequired
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.snacked
import com.crocodic.core.extension.textOf
import com.maruchan.notes.R
import com.maruchan.notes.base.BaseActivity
import com.maruchan.notes.databinding.ActivityRegisterBinding
import com.maruchan.notes.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterActivity :
    BaseActivity<ActivityRegisterBinding, RegisterViewModel>(R.layout.activity_register) {
    override fun onCreate(savedInstanceState: Bundle?) {
        window.navigationBarColor = ContextCompat.getColor(this,R.color.yellow)
        super.onCreate(savedInstanceState)
        observe()
        initClick()
    }

    private fun initClick() {

        binding.tvLogin.setOnClickListener {
            finish()
        }
        binding.imgBack.setOnClickListener {
            finish()
        }
        binding.btnRegister.setOnClickListener {
            register()
        }
    }

    private fun register() {

        val name = binding.etNameRegister.textOf()
        val phone = binding.etPhoneRegister.textOf()
        val email = binding.etEmailRegister.textOf()
        val password = binding.etPasswordRegister.textOf()
        val passwordConfirmation = binding.etConfirmPasswordRegister.textOf()

        if (binding.etNameRegister.isEmptyRequired(R.string.label_must_fill) ||
            binding.etEmailRegister.isEmptyRequired(R.string.label_must_fill) ||
            binding.etPhoneRegister.isEmptyRequired(R.string.label_must_fill) ||
            binding.etPasswordRegister.isEmptyRequired(R.string.label_must_fill) ||
            binding.etConfirmPasswordRegister.isEmptyRequired(R.string.label_must_fill)
        ) {
            return
        }
        if (password.length < 7) {
            binding.root.snacked("Password of at least 8 characters")
        } else {
            if (password != passwordConfirmation) {
                binding.tvPasswordNotMatch.visibility = View.VISIBLE
            } else {
                binding.tvPasswordNotMatch.visibility = View.GONE
                viewModel.register(name, phone, email, password, passwordConfirmation)
            }
        }

    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> loadingDialog.show(R.string.please_wait_register)
                            ApiStatus.SUCCESS -> {
                                loadingDialog.dismiss()
                                openActivity<LoginActivity>()
                                finish()
                            }
                            ApiStatus.ERROR -> {
                                disconnect(it)
                                binding.root.snacked(R.string.register_failed)
                                loadingDialog.setResponse(it.message ?: return@collect)

                            }
                            else -> loadingDialog.setResponse(it.message ?: return@collect)
                        }
                    }
                }
            }
        }
    }
}