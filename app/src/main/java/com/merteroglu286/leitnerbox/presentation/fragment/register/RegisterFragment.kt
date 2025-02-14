package com.merteroglu286.leitnerbox.presentation.fragment.register

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputLayout
import com.merteroglu286.leitnerbox.R
import com.merteroglu286.leitnerbox.databinding.FragmentRegisterBinding
import com.merteroglu286.leitnerbox.domain.model.User
import com.merteroglu286.leitnerbox.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class RegisterFragment : BaseFragment<FragmentRegisterBinding, RegisterVM>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentRegisterBinding {
        return FragmentRegisterBinding.inflate(layoutInflater, container, false)
    }

    override fun initUI() {
        super.initUI()
        enableBackButton(binding.backButton)

    }

    override fun setListeners() {
        super.setListeners()
        with(binding) {

            registerButton.setOnClickListener {
                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString()
                val rePassword = etRePassword.text.toString()

                resetErrorStyles()

                when {
                    email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                        .matches() -> {
                        showErrorPopup("Geçerli bir e-posta adresi girin!")
                        tilEmail.setErrorStyle()
                    }

                    password.length < 6 -> {
                        showErrorPopup("Şifre en az 6 karakter olmalı!")
                        tilPassword.setErrorStyle()
                    }

                    password != rePassword -> {
                        showErrorPopup("Şifreler eşleşmiyor!")
                        tilRePassword.setErrorStyle()
                    }

                    else -> {
                        registerUser(email, password)
                    }
                }
            }
        }
    }

    private fun TextInputLayout.setErrorStyle() {
        this.boxStrokeColor = ContextCompat.getColor(context, R.color.red)
        this.error = " "
    }

    private fun resetErrorStyles() {
        with(binding) {
            tilEmail.boxStrokeColor =
                ContextCompat.getColor(root.context, R.color.primary_text_color)
            tilPassword.boxStrokeColor =
                ContextCompat.getColor(root.context, R.color.primary_text_color)
            tilRePassword.boxStrokeColor =
                ContextCompat.getColor(root.context, R.color.primary_text_color)

            tilEmail.error = null
            tilPassword.error = null
            tilRePassword.error = null
        }
    }

    private fun registerUser(email: String, password: String) {
        viewModel.register(email, password)

    }

    override fun setReceivers() {
        super.setReceivers()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.user.collect { user ->
                if (user != null) {
                    with(binding){
                        viewModel.addUser(User(id = user.uid, email = etEmail.text.toString(),20, false))
                    }
                    requireActivity().finish()
                    viewModel.goDashboardScreen()
                }
            }
        }
    }
}