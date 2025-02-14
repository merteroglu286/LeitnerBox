package com.merteroglu286.leitnerbox.os.uploadimage

import android.net.Uri
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkRequest

object PhotoWorkRequests {

    fun createUploadImageRequest(
        userId: String,
        imageUri: Uri,
        toBox: Int
    ): WorkRequest {
        val inputData = Data.Builder()
            .putString(UploadImageWorker.KEY_USER_ID, userId)
            .putString(UploadImageWorker.KEY_IMAGE_URI, imageUri.toString())
            .putInt(UploadImageWorker.KEY_TO_BOX, toBox)
            .build()

        return OneTimeWorkRequestBuilder<UploadImageWorker>()
            .setInputData(inputData)
            .build()
    }

    fun createMovePhotoRequest(
        userId: String,
        photoUrl: String,
        fromBox: Int,
        toBox: Int
    ): WorkRequest {
        val inputData = Data.Builder()
            .putString(MovePhotoWorker.KEY_USER_ID, userId)
            .putString(MovePhotoWorker.KEY_PHOTO_URL, photoUrl)
            .putInt(MovePhotoWorker.KEY_FROM_BOX, fromBox)
            .putInt(MovePhotoWorker.KEY_TO_BOX, toBox)
            .build()

        return OneTimeWorkRequestBuilder<MovePhotoWorker>()
            .setInputData(inputData)
            .build()
    }

    fun createMovePhotoWithStorageRequest(
        userId: String,
        newImageUri: Uri,
        oldImageUrl: String,
        fromBox: Int,
        toBox: Int
    ): WorkRequest {
        val inputData = Data.Builder()
            .putString(MovePhotoWithStorageWorker.KEY_USER_ID, userId)
            .putString(MovePhotoWithStorageWorker.KEY_NEW_IMAGE_URI, newImageUri.toString())
            .putString(MovePhotoWithStorageWorker.KEY_OLD_IMAGE_URL, oldImageUrl)
            .putInt(MovePhotoWithStorageWorker.KEY_FROM_BOX, fromBox)
            .putInt(MovePhotoWithStorageWorker.KEY_TO_BOX, toBox)
            .build()

        return OneTimeWorkRequestBuilder<MovePhotoWithStorageWorker>()
            .setInputData(inputData)
            .build()
    }
}