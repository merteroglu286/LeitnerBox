package com.merteroglu286.leitnerbox.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.merteroglu286.leitnerbox.utility.NavigationCommand
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
open class BaseViewModel : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> get() = _successMessage.asStateFlow()

    private val _navigate = Channel<NavigationCommand>(Channel.BUFFERED)
    val navigate = _navigate.receiveAsFlow()

    protected fun setLoading(isLoading: Boolean) {
        _loading.value = isLoading
    }

    protected fun setErrorMessage(message: String?) {
        _errorMessage.value = message
    }

    protected fun setSuccessMessage(message: String?) {
        _successMessage.value = message
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    protected fun navigate(navDirections: NavDirections) {
        viewModelScope.launch { _navigate.send(NavigationCommand.ToDirection(navDirections)) }
    }

    fun navigateBack() {
        viewModelScope.launch { _navigate.send(NavigationCommand.Back) }
    }
}
