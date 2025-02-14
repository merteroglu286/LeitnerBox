package com.merteroglu286.leitnerbox.presentation.fragment.login

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.textfield.TextInputLayout
import com.merteroglu286.leitnerbox.R
import com.merteroglu286.leitnerbox.databinding.FragmentLoginBinding
import com.merteroglu286.leitnerbox.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding, LoginVM>() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001


    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(layoutInflater, container, false)
    }

    override fun setListeners() {
        super.setListeners()
        with(binding) {
            loginButton.setOnClickListener{
                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString()

                resetErrorStyles()

                when {
                    email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                        showErrorPopup("Geçerli bir e-posta adresi girin!")
                        tilEmail.setErrorStyle()
                    }
                    password.length < 6 -> {
                        showErrorPopup("Şifre en az 6 karakter olmalı!")
                        tilPassword.setErrorStyle()
                    }
                    else -> {
                        viewModel.login(email, password)
                    }
                }
            }

            registerButton.setOnClickListener{
                viewModel.goRegisterScreen()
            }

            val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("463832392482-2jv2cqnsijm3it236uuv1bvlfn9bbll4.apps.googleusercontent.com")
                .requestEmail()
                .build()
            googleSignInClient = GoogleSignIn.getClient(requireContext(), googleSignInOptions)

            binding.googleButton.setOnClickListener {
                signInWithGoogle()
            }

            resetPasswordButton.setOnClickListener{
                viewModel.goResetPasswordScreen()
            }
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            if (task.isSuccessful) {
                val account = task.result
                account?.let {
                    val idToken = it.idToken
                    if (idToken != null) {
                        viewModel.signInWithGoogle(idToken)
                    }
                }
            } else {
                // Google Sign-In hatası
                Log.e("LoginFragment", "Google Sign-In failed", task.exception)
            }
        }
    }

    private fun TextInputLayout.setErrorStyle() {
        this.boxStrokeColor = ContextCompat.getColor(context, R.color.red)
        this.error = " "
    }

    private fun resetErrorStyles() {
        with(binding) {
            tilEmail.boxStrokeColor = ContextCompat.getColor(root.context, R.color.primary_text_color)
            tilPassword.boxStrokeColor = ContextCompat.getColor(root.context, R.color.primary_text_color)

            tilEmail.error = null
            tilPassword.error = null
        }
    }

    override fun setReceivers() {
        super.setReceivers()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.user.collect { user ->
                if (user != null) {
                    requireActivity().finish()
                    viewModel.goDashboardScreen()
                }
            }
        }
    }
}