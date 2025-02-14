package com.merteroglu286.leitnerbox.presentation.fragment.home

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.merteroglu286.leitnerbox.data.local.Preferences
import com.merteroglu286.leitnerbox.domain.model.User
import com.merteroglu286.leitnerbox.domain.repository.FirebaseAuthRepository
import com.merteroglu286.leitnerbox.domain.repository.FirebaseStorageRepository
import com.merteroglu286.leitnerbox.domain.repository.FirestoreRepository
import com.merteroglu286.leitnerbox.os.uploadimage.PhotoWorkRequests
import com.merteroglu286.leitnerbox.presentation.base.BaseViewModel
import com.merteroglu286.leitnerbox.presentation.viewmodel.SingleFlowEvent
import com.merteroglu286.leitnerbox.utility.constant.AppConstants.STORAGE_DIRECTORY
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
import javax.inject.Inject
import androidx.lifecycle.Observer
import androidx.work.WorkInfo

@ExperimentalCoroutinesApi
@HiltViewModel
class HomeVM @Inject constructor(
    private val firestoreRepository: FirestoreRepository,
    private val permissionManager: PermissionManager,
    private val mediaManager: MediaManager,
    private val firebaseStorageRepository: FirebaseStorageRepository,
    private val preferences: Preferences,
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val workManager: WorkManager,
    application: Application
) : BaseViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _countImagesInBox = MutableStateFlow<List<Int>?>(null)
    val countImagesInBox: StateFlow<List<Int>?> = _countImagesInBox.asStateFlow()

    private val _currentPhotoUri = MutableSharedFlow<Uri?>()
    val currentPhotoUri: SharedFlow<Uri?> = _currentPhotoUri.asSharedFlow()

    private val _photoUploadMessage = MutableStateFlow<SingleFlowEvent<String>?>(null)
    val photoUploadMessage: StateFlow<SingleFlowEvent<String>?> = _photoUploadMessage.asStateFlow()

    private val _lastImageUrl = MutableStateFlow<SingleFlowEvent<String>?>(null)
    val lastImageUrl: StateFlow<SingleFlowEvent<String>?> = _lastImageUrl.asStateFlow()

    private val _boxesState = MutableStateFlow<List<Boolean>?>(null)
    val boxesState: StateFlow<List<Boolean>?> = _boxesState.asStateFlow()

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

    fun getCountImagesInBox(userId: String) {
        viewModelScope.launch {
            setLoading(true)
            val result = firestoreRepository.getCountImagesInBox(userId)
            result.onSuccess { list ->
                setLoading(false)
                getEligiableBoxes(userId)
                _countImagesInBox.value = list
            }
            result.onFailure {
                setLoading(false)
                setErrorMessage(it.message)
            }
        }
    }

    /*fun uploadImageToFirebaseStorage(userId: String, imageUri: Uri, toBox: Int) {
        viewModelScope.launch {
            setLoading(true)
            val result = firebaseStorageRepository.uploadImage(userId, imageUri, STORAGE_DIRECTORY)
            result.onSuccess { imageUrl ->
                setLoading(false)
                addPhotoUrlToBox(userId, imageUrl, toBox)
            }
            result.onFailure {
                setLoading(false)
                setErrorMessage("Fotoğraf yüklenemedi.")
            }
        }
    }*/

    fun uploadImageToFirebaseStorage(userId: String, imageUri: Uri, toBox: Int) {
        viewModelScope.launch {
            setLoading(true)

            val uploadWorkRequest = PhotoWorkRequests.createUploadImageRequest(userId, imageUri, toBox)
            workManager.enqueue(uploadWorkRequest)

            workManager.getWorkInfoByIdLiveData(uploadWorkRequest.id)
                .observeForever { workInfo ->
                    when (workInfo?.state) {
                        WorkInfo.State.SUCCEEDED -> {
                            setLoading(false)
                            _photoUploadMessage.value = SingleFlowEvent("Fotoğraf başarıyla yüklendi.")
                            getCountImagesInBox(userId)
                            workManager.getWorkInfoByIdLiveData(uploadWorkRequest.id).removeObserver { this }
                        }
                        WorkInfo.State.FAILED -> {
                            setLoading(false)
                            setErrorMessage("Fotoğraf yüklenemedi.")
                            workManager.getWorkInfoByIdLiveData(uploadWorkRequest.id).removeObserver { this }
                        }
                        WorkInfo.State.CANCELLED -> {
                            setLoading(false)
                            setErrorMessage("Yükleme iptal edildi.")
                            workManager.getWorkInfoByIdLiveData(uploadWorkRequest.id).removeObserver { this }
                        }
                        else -> {
                            // Work devam ediyor
                        }
                    }
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

            // Gün uygunsa, en eski fotoğraf getirilir
            val result = firestoreRepository.getEligibleOldestPhotoForSpecificDays(
                userId,
                whichBox
            )

            result.onSuccess { photoUrl ->
                setLoading(false)
                _lastImageUrl.value = SingleFlowEvent(photoUrl)
            }
            result.onFailure {
                setLoading(false)
                setErrorMessage(it.message)
            }
        }
    }

    private fun getEligiableBoxes(userId: String){
        viewModelScope.launch {
            setLoading(true)
            val result = firestoreRepository.getEligibleBoxes(userId)

            result.onSuccess { list ->
                setLoading(false)
                _boxesState.value = list
            }
            result.onFailure {
                setLoading(false)
                setErrorMessage(it.message)
            }
        }
    }

    /*fun movePhotoToAnotherBoxWithoutStorage(userId: String, photoUrl: String, fromBox: Int, toBox: Int) {
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

    fun movePhotoToAnotherBoxWithStorage(
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
                    firebaseStorageRepository.updateImage(userId, imageUri, oldImageUrl, STORAGE_DIRECTORY)

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
    }*/


    fun movePhotoToAnotherBoxWithoutStorage(userId: String, photoUrl: String, fromBox: Int, toBox: Int) {
        viewModelScope.launch {
            setLoading(true)

            val moveWorkRequest = PhotoWorkRequests.createMovePhotoRequest(
                userId, photoUrl, fromBox, toBox
            )
            workManager.enqueue(moveWorkRequest)

            workManager.getWorkInfoByIdLiveData(moveWorkRequest.id)
                .observeForever { workInfo ->
                    when (workInfo?.state) {
                        WorkInfo.State.SUCCEEDED -> {
                            setLoading(false)
                            _photoUploadMessage.value = SingleFlowEvent(
                                "Fotoğraf $fromBox. kutudan $toBox. kutuya taşındı."
                            )
                            getCountImagesInBox(userId)
                            workManager.getWorkInfoByIdLiveData(moveWorkRequest.id).removeObserver { this }
                        }
                        WorkInfo.State.FAILED -> {
                            setLoading(false)
                            setErrorMessage("Fotoğraf taşınamadı.")
                            workManager.getWorkInfoByIdLiveData(moveWorkRequest.id).removeObserver { this }
                        }
                        WorkInfo.State.CANCELLED -> {
                            setLoading(false)
                            setErrorMessage("Taşıma işlemi iptal edildi.")
                            workManager.getWorkInfoByIdLiveData(moveWorkRequest.id).removeObserver { this }
                        }
                        else -> {
                            // Work devam ediyor
                        }
                    }
                }
        }
    }

    fun movePhotoToAnotherBoxWithStorage(
        userId: String,
        imageUri: Uri,
        oldImageUrl: String,
        fromBox: Int,
        toBox: Int
    ) {
        viewModelScope.launch {
            setLoading(true)

            val moveWithStorageWorkRequest = PhotoWorkRequests.createMovePhotoWithStorageRequest(
                userId, imageUri, oldImageUrl, fromBox, toBox
            )
            workManager.enqueue(moveWithStorageWorkRequest)

            workManager.getWorkInfoByIdLiveData(moveWithStorageWorkRequest.id)
                .observeForever { workInfo ->
                    when (workInfo?.state) {
                        WorkInfo.State.SUCCEEDED -> {
                            setLoading(false)
                            _photoUploadMessage.value = SingleFlowEvent(
                                "Fotoğraf başarıyla güncellendi ve $fromBox. kutudan $toBox. kutuya taşındı."
                            )
                            getCountImagesInBox(userId)
                            workManager.getWorkInfoByIdLiveData(moveWithStorageWorkRequest.id).removeObserver { this }
                        }
                        WorkInfo.State.FAILED -> {
                            setLoading(false)
                            setErrorMessage("Fotoğraf güncellenemedi ve taşınamadı.")
                            workManager.getWorkInfoByIdLiveData(moveWithStorageWorkRequest.id).removeObserver { this }
                        }
                        WorkInfo.State.CANCELLED -> {
                            setLoading(false)
                            setErrorMessage("Güncelleme ve taşıma işlemi iptal edildi.")
                            workManager.getWorkInfoByIdLiveData(moveWithStorageWorkRequest.id).removeObserver { this }
                        }
                        else -> {
                            // Work devam ediyor
                        }
                    }
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
