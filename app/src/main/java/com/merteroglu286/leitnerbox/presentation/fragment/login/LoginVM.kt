package com.merteroglu286.leitnerbox.presentation.fragment.login

import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.merteroglu286.leitnerbox.data.local.Preferences
import com.merteroglu286.leitnerbox.domain.model.User
import com.merteroglu286.leitnerbox.domain.repository.FirebaseAuthRepository
import com.merteroglu286.leitnerbox.domain.repository.FirestoreRepository
import com.merteroglu286.leitnerbox.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class LoginVM @Inject constructor(
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val preferences: Preferences
) : BaseViewModel() {

    private val _user = MutableStateFlow<FirebaseUser?>(null)
    val user: StateFlow<FirebaseUser?> = _user.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            setLoading(true)
            val result = firebaseAuthRepository.login(email, password)
            result.onSuccess { user ->
                _user.value = user
                saveUserId(user.uid)
            }
            result.onFailure {
                setErrorMessage(it.message)
            }
            setLoading(false)
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            setLoading(true)
            val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuthRepository.signInWithGoogle(credential)
            result.onSuccess { user ->
                _user.value = user
                saveUserId(user.uid)
            }
            result.onFailure {
                setErrorMessage(it.message)
            }
        }
    }

    fun goRegisterScreen(){
        navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
    }

    fun goDashboardScreen(){
        navigate(LoginFragmentDirections.actionLoginFragmentToDashboardActivity())
    }

    fun goResetPasswordScreen(){
        navigate(LoginFragmentDirections.actionLoginFragmentToResetPasswordFragment())
    }

    private fun saveUserId(uid: String){
        preferences.saveUserId(uid)
    }

}