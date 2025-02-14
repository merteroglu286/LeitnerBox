package com.merteroglu286.leitnerbox.domain.repository

import android.net.Uri
import com.merteroglu286.leitnerbox.domain.model.User

interface FirestoreRepository {
    suspend fun addUser(user: User): Result<String>
    suspend fun getUserById(userId: String): Result<User>
    suspend fun addPhotoUrlToBox(userId: String, photoUrl: String, whicBox: Int): Result<Unit>
//    suspend fun getOldestPhotoFromBox(userId: String, whichBox: Int): Result<String>
    suspend fun getCountImagesInBox(userId: String): Result<List<Int>>
    suspend fun getEligibleOldestPhotoForSpecificDays(
        userId: String,
        whichBox: Int
    ): Result<String>

    suspend fun movePhotoToAnotherBox(
        userId: String,
        photoUrl: String,
        fromBox: Int,
        toBox: Int
    ): Result<Unit>

    suspend fun replaceAndMovePhoto(
        userId: String,
        oldImageUrl: String,
        newImageUrl: String,
        fromBox: Int,
        toBox: Int
    ): Result<Unit>

    suspend fun getEligibleBoxes(userId: String): Result<List<Boolean>>
}