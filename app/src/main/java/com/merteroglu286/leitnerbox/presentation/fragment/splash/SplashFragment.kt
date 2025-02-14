package com.merteroglu286.leitnerbox.presentation.fragment.splash

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.merteroglu286.leitnerbox.databinding.FragmentSplashBinding
import com.merteroglu286.leitnerbox.presentation.base.BaseFragment
import com.merteroglu286.leitnerbox.presentation.viewmodel.SharedViewModel
import com.merteroglu286.leitnerbox.utility.constant.AppConstants.SPLASH_DELAY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding, SplashVM>() {

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentSplashBinding {
        return FragmentSplashBinding.inflate(inflater, container, false)
    }

    override fun initUI() {
        super.initUI()
    }

    override fun runOnce() {
        super.runOnce()

        checkFirebaseUser()

        with(binding) {
            /*    lottieView.apply {
                    playAnimation()
                    addAnimatorListener(object : Animator.AnimatorListener {

                        override fun onAnimationStart(animation: Animator) {

                        }

                        override fun onAnimationEnd(animation: Animator) {

                        }

                        override fun onAnimationCancel(animation: Animator) {

                        }

                        override fun onAnimationRepeat(animation: Animator) {

                        }

                    })
                }*/


        }

    }

    private fun checkFirebaseUser() {
        viewModel.getCurrentUser().let { firebaseUser ->
            lifecycleScope.launchWhenCreated {
                delay(SPLASH_DELAY)
                requireActivity().finish()
                if (firebaseUser == null) {
                    viewModel.goAuthScreen()
                } else {
                    viewModel.goDashboardScreen()
                }
            }

        }

        /*    override fun setReceivers() {
        super.setReceivers()

        sharedViewModel.data.filterNotNull().onEach { data ->
            showToast(data)
            sharedViewModel.data.value = null
        }.launchIn(myScope)
    }*/

    }
}