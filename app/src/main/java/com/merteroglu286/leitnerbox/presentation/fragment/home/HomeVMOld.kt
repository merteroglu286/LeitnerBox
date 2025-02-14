/*
package com.merteroglu286.leitnerbox.presentation.fragment.home

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.viewModelScope
import com.merteroglu286.leitnerbox.data.local.Preferences
import com.merteroglu286.leitnerbox.domain.model.User
import com.merteroglu286.leitnerbox.domain.repository.FirebaseAuthRepository
import com.merteroglu286.leitnerbox.domain.repository.FirebaseStorageRepository
import com.merteroglu286.leitnerbox.domain.repository.FirestoreRepository
import com.merteroglu286.leitnerbox.presentation.base.BaseViewModel
import com.merteroglu286.leitnerbox.presentation.viewmodel.SingleFlowEvent
import com.merteroglu286.leitnerbox.utility.manager.MediaManager
import com.merteroglu286.leitnerbox.utility.manager.PermissionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class HomeVM @Inject constructor(
    private val firestoreRepository: FirestoreRepository,
    private val permissionManager: PermissionManager,
    private val mediaManager: MediaManager,
    private val firebaseStorageRepository: FirebaseStorageRepository,
    private val preferences: Preferences,
    private val firebaseAuthRepository: FirebaseAuthRepository
) : BaseViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _currentPhotoUri = MutableSharedFlow<Uri?>()
    val currentPhotoUri: SharedFlow<Uri?> = _currentPhotoUri.asSharedFlow()

    private val _photoUploadMessage = MutableStateFlow<SingleFlowEvent<String>?>(null)
    val photoUploadMessage: StateFlow<SingleFlowEvent<String>?> = _photoUploadMessage.asStateFlow()

    private val _lastImageUrl = MutableStateFlow<String?>(null)
    val lastImageUrl: StateFlow<String?> = _lastImageUrl.asStateFlow()

    private val _countImagesInBox = MutableStateFlow<List<Int>?>(null)
    val countImagesInBox: StateFlow<List<Int>?> = _countImagesInBox.asStateFlow()


    private val _imageInFirstBox = MutableStateFlow<SingleFlowEvent<String>?>(null)
    val imageInFirstBox: StateFlow<SingleFlowEvent<String>?> = _imageInFirstBox.asStateFlow()

    private val _imageInSecondBox = MutableStateFlow<SingleFlowEvent<String>?>(null)
    val imageInSecondBox: StateFlow<SingleFlowEvent<String>?> = _imageInSecondBox.asStateFlow()

    private val _imageInThirdBox = MutableStateFlow<SingleFlowEvent<String>?>(null)
    val imageInThirdBox: StateFlow<SingleFlowEvent<String>?> = _imageInThirdBox.asStateFlow()

    private val _imageInFourthBox = MutableStateFlow<SingleFlowEvent<String>?>(null)
    val imageInFourthBox: StateFlow<SingleFlowEvent<String>?> = _imageInFourthBox.asStateFlow()

    fun getUser(userId: String) {
        viewModelScope.launch {
            setLoading(true)
            val result = firestoreRepository.getUserById(userId)
            result.onSuccess {
                _user.value = it
                setLoading(false)
            }
            result.onFailure {
                setErrorMessage(it.message)
                setLoading(false)
            }
        }
    }

    fun checkAndRequesGalleryPermission(
        permissionLauncher: ActivityResultLauncher<String>,
        onPermissionGranted: () -> Unit
    ) {
        if (permissionManager.hasReadStoragePermission()) {
            onPermissionGranted()
        } else {
            permissionManager.requestReadStoragePermission(permissionLauncher)
        }
    }

    fun startGallery(galleryLauncher: ActivityResultLauncher<String>) {
        mediaManager.startGalleryIntent(galleryLauncher)
    }

    fun handleGalleryResult(uri: Uri) {
        mediaManager.handleGalleryResult(uri)
        _currentPhotoUri.tryEmit(uri)
    }

    fun checkAndRequesCameraPermission(
        permissionLauncher: ActivityResultLauncher<String>,
        onPermissionGranted: () -> Unit
    ) {
        if (permissionManager.hasCameraPermission()) {
            onPermissionGranted()
        } else {
            permissionManager.requestCameraPermission(permissionLauncher)
        }
    }

    fun startCamera(cameraLauncher: ActivityResultLauncher<Intent>) {
        viewModelScope.launch {
            mediaManager.startCameraIntent(cameraLauncher)
        }
        _currentPhotoUri.tryEmit(mediaManager.getCurrentPhotoUri())
    }

    fun getCurrentPhotoUri(): Uri? {
        return mediaManager.getCurrentPhotoUri()
    }

    fun uploadImageToFirebaseStorage(userId: String, imageUri: Uri, whicBox: Int) {
        viewModelScope.launch {
            setLoading(true)
            val result = firebaseStorageRepository.uploadImage(userId, imageUri, "images")
            result.onSuccess { imageUrl ->
                setLoading(false)
//                setSuccessMessage("Fotoğraf başarıyla yüklendi.")
                addPhotoUrlToBox(userId, imageUrl, whicBox)
            }
            result.onFailure {
                setLoading(false)
                setErrorMessage("Fotoğraf yüklenemedi.")
            }
        }
    }

    private fun addPhotoUrlToBox(userId: String, photoUrl: String, whicBox: Int) {
        viewModelScope.launch {
            setLoading(true)
            val result = firestoreRepository.addPhotoUrlToBox(userId, photoUrl, whicBox)
            result.onSuccess {
                setLoading(false)
                _photoUploadMessage.value = SingleFlowEvent("Fotoğraf başarıyla yüklendi.")
                getCountImagesInBox(userId)
            }
            result.onFailure {
                setLoading(false)
                setErrorMessage(it.message)
            }
        }
    }

    fun getOldestPhotoFromBox(userId: String, whichBox: Int) {
        viewModelScope.launch {
            setLoading(true)
            val result = firestoreRepository.getOldestPhotoFromBox(userId, whichBox)

            result.onSuccess { photoUrl ->
                setLoading(false)
                _lastImageUrl.tryEmit(photoUrl)
            }
            result.onFailure {
                setLoading(false)
                setErrorMessage(it.message)
            }
        }
    }

    fun getCountImagesInBox(userId: String) {
        viewModelScope.launch {
            setLoading(true)
            val result = firestoreRepository.getCountImagesInBox(userId)
            result.onSuccess { list ->
                setLoading(false)
                _countImagesInBox.value = list
            }
            result.onFailure {
                setLoading(false)
                setErrorMessage(it.message)
            }
        }
    }

    fun getEligibleOldestPhotoForSpecificDays(userId: String, whichBox: Int) {
        viewModelScope.launch {
            setLoading(true)

            // Önce gün kontrolü yapılır
            val validDays = getValidDaysForBox(whichBox)
            if (!isValidDayForBox(validDays)) {
                setLoading(false)
                //setErrorMessage("Bu kutuyu kontrol etmek için uygun bir gün değil.")
                return@launch
            }

            // Gün uygunsa, en eski fotoğraf getirilir
            val result = firestoreRepository.getEligibleOldestPhotoForSpecificDays(
                userId,
                whichBox,
                validDays
            )

            result.onSuccess { photoUrl ->
                setLoading(false)
                when (whichBox) {
                    1 -> _imageInFirstBox.value = SingleFlowEvent(photoUrl)
                    2 -> _imageInSecondBox.value = SingleFlowEvent(photoUrl)
                    3 -> _imageInThirdBox.value = SingleFlowEvent(photoUrl)
                    4 -> _imageInFourthBox.value = SingleFlowEvent(photoUrl)
                }
            }
            result.onFailure {
                setLoading(false)
                //setErrorMessage(it.message)
            }
        }
    }


    private fun isValidDayForBox(validDays: Set<Int>): Boolean {
        val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        return validDays.contains(currentDay)
    }

    private fun getValidDaysForBox(whichBox: Int): Set<Int> {
        return when (whichBox) {
            1 -> (3..31 step 3).toSet()  // Ayın 3 ve katları
            2 -> (6..31 step 6).toSet()  // Ayın 6 ve katları
            3 -> setOf(2, 8, 16, 24)    // Ayın 2, 8, 16, 24 günleri
            4 -> setOf(22, 30)          // Ayın 22 ve 30 günleri
            else -> emptySet()          // Geçersiz kutu
        }
    }

    fun movePhotoToAnotherBox(userId: String, photoUrl: String, fromBox: Int, toBox: Int) {
        viewModelScope.launch {
            setLoading(true)
            val result = firestoreRepository.movePhotoToAnotherBox(userId, photoUrl, fromBox, toBox)

            result.onSuccess {
                setLoading(false)
                _photoUploadMessage.value =
                    SingleFlowEvent("Fotoğraf $fromBox. kutudan $toBox. kutuya taşındı.")
                getCountImagesInBox(userId)
            }
            result.onFailure {
                setLoading(false)
                setErrorMessage(it.message)
            }
        }
    }

    fun updateImage(
        userId: String,
        imageUri: Uri,
        oldImageUrl: String,
        fromBox: Int,
        toBox: Int
    ) {
        viewModelScope.launch {
            try {
                setLoading(true)

                // 1. Yeni resmi yükle ve URL'ini al
                val updateResult =
                    firebaseStorageRepository.updateImage(userId, imageUri, oldImageUrl, "images")

                if (updateResult.isSuccess) {
                    val newImageUrl = updateResult.getOrNull()!!

                    // 2. Firestore'daki kutu değişikliğini yap
                    val moveResult = firestoreRepository.replaceAndMovePhoto(
                        userId = userId,
                        oldImageUrl = oldImageUrl,
                        newImageUrl = newImageUrl,
                        fromBox = fromBox,
                        toBox = toBox
                    )

                    if (moveResult.isSuccess) {
                        _photoUploadMessage.value =
                            SingleFlowEvent("Fotoğraf başarıyla güncellendi ve $fromBox. kutudan $toBox. kutuya taşındı.")
                        getCountImagesInBox(userId)
                    } else {
                        setErrorMessage("Fotoğraf taşınırken bir hata oluştu: ${moveResult.exceptionOrNull()?.message}")
                    }
                } else {
                    setErrorMessage("Fotoğraf güncellenirken bir hata oluştu: ${updateResult.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                setErrorMessage("Beklenmeyen bir hata oluştu: ${e.message}")
            } finally {
                setLoading(false)
            }
        }
    }

    fun replaceAndMovePhoto(
        userId: String,
        oldImageUrl: String,
        newImageUrl: String,
        fromBox: Int,
        toBox: Int
    ) {
        viewModelScope.launch {
            setLoading(true)
            val result = firestoreRepository.replaceAndMovePhoto(
                userId,
                oldImageUrl,
                newImageUrl,
                fromBox,
                toBox
            )
            result.onSuccess {
                setLoading(false)
                _photoUploadMessage.value =
                    SingleFlowEvent("Fotoğraf $fromBox. kutudan $toBox. kutuya taşındı.")
                getCountImagesInBox(userId)
            }
            result.onFailure {
                setLoading(false)
                setErrorMessage(it.message)

            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            firebaseAuthRepository.logout()
        }
    }

    fun goSplashScreen() {
        preferences.saveUserId("")
        navigate(HomeFragmentDirections.actionHomeFragmentToMainActivity())
    }

    fun goImageScreen(uri: Uri? = null, url: String? = null) {
        navigate(
            HomeFragmentDirections.actionHomeFragmentToImageFragment(
                uriString = uri.toString(),
                urlString = url.toString()
            )
        )
    }
}
*/
