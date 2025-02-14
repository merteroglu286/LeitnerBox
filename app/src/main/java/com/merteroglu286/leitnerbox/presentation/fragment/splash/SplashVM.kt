package com.merteroglu286.leitnerbox.presentation.fragment.splash

import com.merteroglu286.leitnerbox.domain.repository.FirebaseAuthRepository
import com.merteroglu286.leitnerbox.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class SplashVM @Inject constructor(
    private val firebaseAuthRepository: FirebaseAuthRepository
) : BaseViewModel() {

    fun getCurrentUser() = firebaseAuthRepository.getCurrentUser()

    fun goAuthScreen(){
        navigate(SplashFragmentDirections.actionSplashFragmentToAuthActivity())
    }

    fun goDashboardScreen(){
        navigate(SplashFragmentDirections.actionSplashFragmentToDashboardActivity())
    }

}