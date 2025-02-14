package com.merteroglu286.leitnerbox.presentation.fragment.history

import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import com.merteroglu286.leitnerbox.presentation.base.BaseViewModel
import com.merteroglu286.leitnerbox.utility.manager.MediaManager
import com.merteroglu286.leitnerbox.utility.manager.PermissionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class HistoryVM @Inject constructor(
    private val permissionManager: PermissionManager,
    private val mediaManager: MediaManager
) : BaseViewModel()  {

    private val _currentPhotoUri = MutableStateFlow<Uri?>(null)
    val currentPhotoUri: StateFlow<Uri?> = _currentPhotoUri.asStateFlow()

    fun checkAndRequestPermission(permissionLauncher: ActivityResultLauncher<String>, onPermissionGranted: () -> Unit) {
        if (permissionManager.hasCameraPermission()) {
            onPermissionGranted()
        } else {
            permissionManager.requestCameraPermission(permissionLauncher)
        }
    }

    fun startCamera(cameraLauncher: ActivityResultLauncher<Intent>) {
        mediaManager.startCameraIntent(cameraLauncher)
        _currentPhotoUri.value = mediaManager.getCurrentPhotoUri()
    }

    fun getCurrentPhotoUri(): Uri? {
        return mediaManager.getCurrentPhotoUri()
    }
}