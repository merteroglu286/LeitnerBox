package com.merteroglu286.leitnerbox.presentation.fragment.resetpassword

import androidx.lifecycle.viewModelScope
import com.merteroglu286.leitnerbox.domain.repository.FirebaseAuthRepository
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
class ResetPasswordVM @Inject constructor(
    private val firebaseAuthRepository: FirebaseAuthRepository
) : BaseViewModel() {

    private val _isSend = MutableStateFlow<Boolean>(false)
    val isSend: StateFlow<Boolean> = _isSend.asStateFlow()

    fun resetPassword(email: String) {
        viewModelScope.launch {
            setLoading(true)
            val result = firebaseAuthRepository.resetPassword(email)
            result.onSuccess { isSend ->
                _isSend.value = isSend
            }
            result.onFailure {
                setErrorMessage(it.message)
            }
            setLoading(false)
        }
    }
}