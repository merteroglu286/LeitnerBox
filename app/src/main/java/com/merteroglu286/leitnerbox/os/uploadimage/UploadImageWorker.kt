package com.merteroglu286.leitnerbox.os.uploadimage

import android.content.Context
import android.net.Uri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.merteroglu286.leitnerbox.domain.repository.FirebaseStorageRepository
import com.merteroglu286.leitnerbox.domain.repository.FirestoreRepository
import com.merteroglu286.leitnerbox.utility.constant.AppConstants.STORAGE_DIRECTORY
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UploadImageWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val firebaseStorageRepository: FirebaseStorageRepository,
    private val firestoreRepository: FirestoreRepository
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val KEY_USER_ID = "userId"
        const val KEY_IMAGE_URI = "imageUri"
        const val KEY_TO_BOX = "toBox"
        const val KEY_UPLOAD_RESULT = "uploadResult"
    }

    override suspend fun doWork(): Result {
        val userId = inputData.getString(KEY_USER_ID) ?: return Result.failure()
        val imageUriString = inputData.getString(KEY_IMAGE_URI) ?: return Result.failure()
        val toBox = inputData.getInt(KEY_TO_BOX, -1)
        if (toBox == -1) return Result.failure()

        return try {
            val imageUri = Uri.parse(imageUriString)

            val uploadResult = firebaseStorageRepository.uploadImage(userId, imageUri, STORAGE_DIRECTORY)

            if (uploadResult.isSuccess) {
                val imageUrl = uploadResult.getOrNull()!!
                val addResult = firestoreRepository.addPhotoUrlToBox(userId, imageUrl, toBox)

                if (addResult.isSuccess) {
                    Result.success()
                } else {
                    Result.failure()
                }
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }
}

@HiltWorker
class MovePhotoWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val firestoreRepository: FirestoreRepository
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val KEY_USER_ID = "userId"
        const val KEY_PHOTO_URL = "photoUrl"
        const val KEY_FROM_BOX = "fromBox"
        const val KEY_TO_BOX = "toBox"
    }

    override suspend fun doWork(): Result {
        val userId = inputData.getString(KEY_USER_ID) ?: return Result.failure()
        val photoUrl = inputData.getString(KEY_PHOTO_URL) ?: return Result.failure()
        val fromBox = inputData.getInt(KEY_FROM_BOX, -1)
        val toBox = inputData.getInt(KEY_TO_BOX, -1)

        if (fromBox == -1 || toBox == -1) return Result.failure()

        return try {
            val result = firestoreRepository.movePhotoToAnotherBox(userId, photoUrl, fromBox, toBox)

            if (result.isSuccess) {
                Result.success()
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }
}

@HiltWorker
class MovePhotoWithStorageWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val firebaseStorageRepository: FirebaseStorageRepository,
    private val firestoreRepository: FirestoreRepository
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val KEY_USER_ID = "userId"
        const val KEY_NEW_IMAGE_URI = "newImageUri"
        const val KEY_OLD_IMAGE_URL = "oldImageUrl"
        const val KEY_FROM_BOX = "fromBox"
        const val KEY_TO_BOX = "toBox"
    }

    override suspend fun doWork(): Result {
        val userId = inputData.getString(KEY_USER_ID) ?: return Result.failure()
        val newImageUriString = inputData.getString(KEY_NEW_IMAGE_URI) ?: return Result.failure()
        val oldImageUrl = inputData.getString(KEY_OLD_IMAGE_URL) ?: return Result.failure()
        val fromBox = inputData.getInt(KEY_FROM_BOX, -1)
        val toBox = inputData.getInt(KEY_TO_BOX, -1)

        if (fromBox == -1 || toBox == -1) return Result.failure()

        return try {
            val newImageUri = Uri.parse(newImageUriString)

            val updateResult = firebaseStorageRepository.updateImage(
                userId,
                newImageUri,
                oldImageUrl,
                STORAGE_DIRECTORY
            )

            if (updateResult.isSuccess) {
                val newImageUrl = updateResult.getOrNull()!!
                val moveResult = firestoreRepository.replaceAndMovePhoto(
                    userId,
                    oldImageUrl,
                    newImageUrl,
                    fromBox,
                    toBox
                )

                if (moveResult.isSuccess) {
                    Result.success()
                } else {
                    Result.failure()
                }
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }
}