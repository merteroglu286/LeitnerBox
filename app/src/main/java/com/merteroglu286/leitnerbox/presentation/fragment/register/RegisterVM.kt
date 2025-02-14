package com.merteroglu286.leitnerbox.presentation.fragment.register

import android.util.Log
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
class RegisterVM @Inject constructor(
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val preferences: Preferences,
    private val firestoreRepository: FirestoreRepository
) : BaseViewModel() {

    private val _user = MutableStateFlow<FirebaseUser?>(null)
    val user: StateFlow<FirebaseUser?> = _user.asStateFlow()


    fun register(email: String, password: String) {
        viewModelScope.launch {
            setLoading(true)
            val result = firebaseAuthRepository.register(email, password)
            result.onSuccess { user ->
                _user.value = user
                Log.d("mertLog",user.uid)
                saveUserId(user.uid)
            }
            result.onFailure {
                setErrorMessage(it.message)
            }
            setLoading(false)
        }
    }

    fun goDashboardScreen(){
        navigate(RegisterFragmentDirections.actionRegisterFragmentToDashboardActivity())
    }

    private fun saveUserId(uid: String){
        preferences.saveUserId(uid)
    }

    fun addUser(user: User){
        viewModelScope.launch {
            setLoading(true)
            val result = firestoreRepository.addUser(user)
            result.onFailure {
                setErrorMessage(it.message)
            }
        }
    }
}