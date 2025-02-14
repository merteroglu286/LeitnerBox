package com.merteroglu286.leitnerbox.data.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.merteroglu286.leitnerbox.domain.repository.FirebaseStorageRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseStorageRepositoryImpl @Inject constructor(
    private val firebaseStorage: FirebaseStorage
) : FirebaseStorageRepository {

    override suspend fun uploadImage(userId: String,imageUri: Uri, directory: String): Result<String> {
        return try {
            val fileName = "${System.currentTimeMillis()}.jpg"

            val storageReference = firebaseStorage.reference.child("$directory/$userId/$fileName")

            storageReference.putFile(imageUri).await()

            val downloadUrl = storageReference.downloadUrl.await().toString()
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteImage(imageUrl: String): Result<Unit> {
        return try {
            val imageRef = getStorageReferenceFromUrl(imageUrl)
                ?: throw IllegalArgumentException("Invalid image URL")

            Log.d("StorageDebug", "Deleting image from path: ${imageRef.path}")
            imageRef.delete().await()
            Log.d("StorageDebug", "Image deleted successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("StorageDebug", "Error deleting image", e)
            Result.failure(e)
        }
    }


    override suspend fun updateImage(
        userId: String,
        newImageUri: Uri,
        oldImageUrl: String,
        directory: String
    ): Result<String> {
        return try {
            // 1. Önce yeni resmi yükle
            val uploadResult = uploadImage(userId, newImageUri, directory)

            if (uploadResult.isSuccess) {
                val newImageUrl = uploadResult.getOrNull()!!

                // 2. Yeni resim başarıyla yüklendiyse, eski resmi sil
                val deleteResult = deleteImage(oldImageUrl)

                if (deleteResult.isFailure) {
                    // Eski resim silinemese bile yeni resmin URL'ini dön
                    Log.w("StorageDebug", "Eski resim silinemedi ama işlem devam ediyor", deleteResult.exceptionOrNull())
                }

                Result.success(newImageUrl)
            } else {
                // Yükleme başarısız olduysa hatayı dön
                Result.failure(uploadResult.exceptionOrNull() ?: Exception("Upload failed"))
            }
        } catch (e: Exception) {
            Log.e("StorageDebug", "Update image failed", e)
            Result.failure(e)
        }
    }

    private fun getStorageReferenceFromUrl(imageUrl: String): StorageReference? {
        return try {
            // Firebase Storage URL'sini parçalara ayır
            val segments = imageUrl.split("/o/", "?")[1].split("?")[0]
            // URL decode işlemi
            val decodedPath = Uri.decode(segments)
            // Storage referansını oluştur
            firebaseStorage.reference.child(decodedPath)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
