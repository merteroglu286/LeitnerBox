package com.merteroglu286.leitnerbox.presentation.fragment.resetpassword


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.merteroglu286.leitnerbox.databinding.FragmentResetPasswordBinding
import com.merteroglu286.leitnerbox.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ResetPasswordFragment : BaseFragment<FragmentResetPasswordBinding, ResetPasswordVM>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentResetPasswordBinding {
        return FragmentResetPasswordBinding.inflate(layoutInflater, container, false)
    }

    override fun initUI() {
        super.initUI()
        with(binding) {
            enableBackButton(backButton)
        }
    }

    override fun setListeners() {
        super.setListeners()
        with(binding) {
            resetPasswordButton.setOnClickListener {
                val email = etEmail.text.toString().trim()
                viewModel.resetPassword(email)
            }
        }
    }

    override fun setReceivers() {
        super.setReceivers()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isSend.collect { isSend ->
                if (isSend) {
                    showSuccessMessage("Link başarıyla gönderildi.") {
                        findNavController().navigateUp()
                    }
                }
            }
        }
    }

}