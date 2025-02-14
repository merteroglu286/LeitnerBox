package com.merteroglu286.leitnerbox.domain.repository

import android.net.Uri

interface FirebaseStorageRepository {
    suspend fun uploadImage(userId: String, imageUri: Uri, directory: String): Result<String>
    suspend fun deleteImage(imageUrl: String): Result<Unit>
    suspend fun updateImage(userId: String,
                            newImageUri: Uri,
                            oldImageUrl: String,
                            directory: String): Result<String>
}
