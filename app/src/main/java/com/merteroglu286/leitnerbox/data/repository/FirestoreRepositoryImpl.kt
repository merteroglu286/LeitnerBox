package com.merteroglu286.leitnerbox.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.merteroglu286.leitnerbox.domain.model.Box
import com.merteroglu286.leitnerbox.domain.model.User
import com.merteroglu286.leitnerbox.domain.repository.FirestoreRepository
import com.merteroglu286.leitnerbox.utility.enums.ImageProcessedEnum
import com.merteroglu286.leitnerbox.utility.enums.WhichBoxEnum
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import javax.inject.Inject

class FirestoreRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FirestoreRepository {
    private val usersCollection = "users"

    override suspend fun addUser(user: User): Result<String> {
        return try {
            val documentRef = firestore.collection(usersCollection).document(user.id)
            documentRef.set(user).await()
            Result.success(user.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun getUserById(userId: String): Result<User> {
        return try {
            val documentSnapshot = firestore.collection(usersCollection)
                .document(userId)
                .get()
                .await()

            if (documentSnapshot.exists()) {
                val user = documentSnapshot.toObject(User::class.java)
                user?.let {
                    Result.success(it)
                } ?: Result.failure(NullPointerException("Kullanıcı bulunamadı."))
            } else {
                Result.failure(Exception("Kullanıcı bulunamadı."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun addPhotoUrlToBox(
        userId: String,
        photoUrl: String,
        whichBox: Int
    ): Result<Unit> {
        return try {
            val userDocument = firestore.collection(usersCollection).document(userId)

            // Kullanıcının mevcut kutuları
            val documentSnapshot = userDocument.get().await()
            val currentUser = documentSnapshot.toObject(User::class.java) ?: User()

            // İlgili kutunun güncellenmesi
            val targetBoxList = when (whichBox) {
                WhichBoxEnum.FIRST_BOX.value -> currentUser.boxes.box1.toMutableList()
                WhichBoxEnum.SECOND_BOX.value -> currentUser.boxes.box2.toMutableList()
                WhichBoxEnum.THIRD_BOX.value -> currentUser.boxes.box3.toMutableList()
                WhichBoxEnum.FOURTH_BOX.value -> currentUser.boxes.box4.toMutableList()
                WhichBoxEnum.FIFTH_BOX.value -> currentUser.boxes.box5.toMutableList()
                else -> mutableListOf()
            }

            val currentDate = getCurrentDate()
            val newPhoto = Box(currentDate, photoUrl, ImageProcessedEnum.NO.value)

            targetBoxList.add(newPhoto)

            val updatedBoxes = when (whichBox) {
                WhichBoxEnum.FIRST_BOX.value -> currentUser.boxes.copy(box1 = targetBoxList)
                WhichBoxEnum.SECOND_BOX.value -> currentUser.boxes.copy(box2 = targetBoxList)
                WhichBoxEnum.THIRD_BOX.value -> currentUser.boxes.copy(box3 = targetBoxList)
                WhichBoxEnum.FOURTH_BOX.value -> currentUser.boxes.copy(box4 = targetBoxList)
                WhichBoxEnum.FIFTH_BOX.value -> currentUser.boxes.copy(box5 = targetBoxList)
                else -> currentUser.boxes
            }

            userDocument.set(currentUser.copy(boxes = updatedBoxes), SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    private fun getCurrentDate(): String {
        val sdf =
            java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", java.util.Locale.getDefault())
        return sdf.format(java.util.Date())
    }

    override suspend fun getCountImagesInBox(userId: String): Result<List<Int>> {
        return try {
            val userDocument = firestore.collection(usersCollection).document(userId)

            val documentSnapshot = userDocument.get().await()
            val currentUser = documentSnapshot.toObject(User::class.java) ?: User()

            val photoCountList = listOf(
                currentUser.boxes.box1.size, // Birinci kutudaki fotoğraf sayısı
                currentUser.boxes.box2.size, // İkinci kutudaki fotoğraf sayısı
                currentUser.boxes.box3.size, // Üçüncü kutudaki fotoğraf sayısı
                currentUser.boxes.box4.size, // Dördüncü kutudaki fotoğraf sayısı
                currentUser.boxes.box5.size  // Beşinci kutudaki fotoğraf sayısı
            )

            Result.success(photoCountList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getEligibleOldestPhotoForSpecificDays(
        userId: String,
        whichBox: Int
    ): Result<String> {
        return try {
            val userDocument = firestore.collection(usersCollection).document(userId)
            val documentSnapshot = userDocument.get().await()
            val currentUser = documentSnapshot.toObject(User::class.java) ?: User()

            // Kutuyu seç
            val targetBox = when (whichBox) {
                WhichBoxEnum.FIRST_BOX.value -> {
                    val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                    if (currentDay % 3 != 0) {
                        return Result.failure(Exception("Birinci kutu yalnızca ayın 3'ü ve katlarında seçilebilir."))
                    }
                    currentUser.boxes.box1
                }
                WhichBoxEnum.SECOND_BOX.value -> {
                    val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                    if (currentDay % 6 != 0) {
                        return Result.failure(Exception("İkinci kutu yalnızca ayın 6'sı ve katlarında seçilebilir."))
                    }
                    currentUser.boxes.box2
                }
                WhichBoxEnum.THIRD_BOX.value -> {
                    val validDaysForThirdBox = setOf(2, 8, 16, 24)
                    val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                    if (!validDaysForThirdBox.contains(currentDay)) {
                        return Result.failure(Exception("Üçüncü kutu yalnızca ayın 2, 8, 16 ve 24. günlerinde seçilebilir."))
                    }
                    currentUser.boxes.box3
                }
                WhichBoxEnum.FOURTH_BOX.value -> {
                    val validDaysForFourthBox = setOf(22, 30)
                    val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                    if (!validDaysForFourthBox.contains(currentDay)) {
                        return Result.failure(Exception("Dördüncü kutu yalnızca ayın 22 ve 30. günlerinde seçilebilir."))
                    }
                    currentUser.boxes.box4
                }
                WhichBoxEnum.FIFTH_BOX.value -> currentUser.boxes.box5
                else -> return Result.failure(Exception("Geçersiz kutu numarası."))
            }

            // Kutuda fotoğraf yoksa hata döner
            if (targetBox.isEmpty()) {
                return Result.failure(Exception("Seçilen kutuda fotoğraf bulunamadı."))
            }

            // En eski fotoğrafı bul
            val oldestPhoto = targetBox.minByOrNull { it.date }
                ?: return Result.failure(Exception("Kutuda geçerli bir fotoğraf bulunamadı."))

            // Tarih kontrolü (48 saat geçti mi?)
            val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", java.util.Locale.getDefault())
            val oldestPhotoDate = dateFormat.parse(oldestPhoto.date)
            val currentTime = System.currentTimeMillis()

            if (oldestPhotoDate == null || (currentTime - oldestPhotoDate.time) < 1 * 24 * 60 * 60 * 1000) {
                return Result.failure(Exception("Fotoğrafın üzerinden 48 saat geçmedi."))
            }

            // Uygun fotoğrafı döndür
            Result.success(oldestPhoto.image)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getEligibleBoxes(userId: String): Result<List<Boolean>> {
        return try {
            val userDocument = firestore.collection(usersCollection).document(userId)
            val documentSnapshot = userDocument.get().await()
            val currentUser = documentSnapshot.toObject(User::class.java) ?: User()

            val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

            // Şartlara uygunluk durumlarını kontrol et
            val boxStatuses = listOf(
                currentDay % 3 == 0 && currentUser.boxes.box1.any { photo ->
                    isPhotoEligible(photo.date)
                },
                currentDay % 6 == 0 && currentUser.boxes.box2.any { photo ->
                    isPhotoEligible(photo.date)
                },
                currentDay in setOf(2, 8, 16, 24) && currentUser.boxes.box3.any { photo ->
                    isPhotoEligible(photo.date)
                },
                currentDay in setOf(22, 30) && currentUser.boxes.box4.any { photo ->
                    isPhotoEligible(photo.date)
                }
            )

            // Sonucu döndür
            Result.success(boxStatuses)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Yardımcı metot: Fotoğrafın uygunluğunu kontrol eder
    private fun isPhotoEligible(photoDateString: String): Boolean {
        return try {
            val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", java.util.Locale.getDefault())
            val photoDate = dateFormat.parse(photoDateString)
            val currentTime = System.currentTimeMillis()
            photoDate != null && (currentTime - photoDate.time) >= 1 * 24 * 60 * 60 * 1000
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun movePhotoToAnotherBox(
        userId: String,
        photoUrl: String,
        fromBox: Int,
        toBox: Int
    ): Result<Unit> {
        return try {
            val userDocument = firestore.collection(usersCollection).document(userId)
            val documentSnapshot = userDocument.get().await()
            val currentUser = documentSnapshot.toObject(User::class.java) ?: User()

            val fromBoxList = when (fromBox) {
                WhichBoxEnum.FIRST_BOX.value -> currentUser.boxes.box1.toMutableList()
                WhichBoxEnum.SECOND_BOX.value -> currentUser.boxes.box2.toMutableList()
                WhichBoxEnum.THIRD_BOX.value -> currentUser.boxes.box3.toMutableList()
                WhichBoxEnum.FOURTH_BOX.value -> currentUser.boxes.box4.toMutableList()
                WhichBoxEnum.FIFTH_BOX.value -> currentUser.boxes.box5.toMutableList()
                else -> mutableListOf()
            }

            val toBoxList = when (toBox) {
                WhichBoxEnum.FIRST_BOX.value -> currentUser.boxes.box1.toMutableList()
                WhichBoxEnum.SECOND_BOX.value -> currentUser.boxes.box2.toMutableList()
                WhichBoxEnum.THIRD_BOX.value -> currentUser.boxes.box3.toMutableList()
                WhichBoxEnum.FOURTH_BOX.value -> currentUser.boxes.box4.toMutableList()
                WhichBoxEnum.FIFTH_BOX.value -> currentUser.boxes.box5.toMutableList()
                else -> mutableListOf()
            }

            val photoToMove = fromBoxList.find { it.image == photoUrl }
                ?: return Result.failure(Exception("Fotoğraf bulunamadı."))

            val updatedPhotoToMove = photoToMove.copy(date = getCurrentDate())
            fromBoxList.remove(photoToMove)
            toBoxList.add(updatedPhotoToMove)

            val updatedBoxes = when (fromBox) {
                WhichBoxEnum.FIRST_BOX.value -> currentUser.boxes.copy(box1 = fromBoxList)
                WhichBoxEnum.SECOND_BOX.value -> currentUser.boxes.copy(box2 = fromBoxList)
                WhichBoxEnum.THIRD_BOX.value -> currentUser.boxes.copy(box3 = fromBoxList)
                WhichBoxEnum.FOURTH_BOX.value -> currentUser.boxes.copy(box4 = fromBoxList)
                WhichBoxEnum.FIFTH_BOX.value -> currentUser.boxes.copy(box5 = fromBoxList)
                else -> currentUser.boxes
            }

            val updatedUser = currentUser.copy(
                boxes = when (toBox) {
                    WhichBoxEnum.FIRST_BOX.value -> updatedBoxes.copy(box1 = toBoxList)
                    WhichBoxEnum.SECOND_BOX.value -> updatedBoxes.copy(box2 = toBoxList)
                    WhichBoxEnum.THIRD_BOX.value -> updatedBoxes.copy(box3 = toBoxList)
                    WhichBoxEnum.FOURTH_BOX.value -> updatedBoxes.copy(box4 = toBoxList)
                    WhichBoxEnum.FIFTH_BOX.value -> updatedBoxes.copy(box5 = toBoxList)
                    else -> updatedBoxes
                }
            )

            userDocument.set(updatedUser, SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun replaceAndMovePhoto(
        userId: String,
        oldImageUrl: String,
        newImageUrl: String,
        fromBox: Int,
        toBox: Int
    ): Result<Unit> {
        return try {
            val userDocument = firestore.collection(usersCollection).document(userId)

            // Kullanıcı verilerini al
            val documentSnapshot = userDocument.get().await()
            val currentUser = documentSnapshot.toObject(User::class.java)
                ?: return Result.failure(Exception("Kullanıcı bulunamadı."))

            // Kaynak kutuyu seç
            val fromBoxList = when (fromBox) {
                WhichBoxEnum.FIRST_BOX.value -> currentUser.boxes.box1.toMutableList()
                WhichBoxEnum.SECOND_BOX.value -> currentUser.boxes.box2.toMutableList()
                WhichBoxEnum.THIRD_BOX.value -> currentUser.boxes.box3.toMutableList()
                WhichBoxEnum.FOURTH_BOX.value -> currentUser.boxes.box4.toMutableList()
                WhichBoxEnum.FIFTH_BOX.value -> currentUser.boxes.box5.toMutableList()
                else -> return Result.failure(Exception("Geçersiz kaynak kutu."))
            }

            // Hedef kutuyu seç
            val toBoxList = when (toBox) {
                WhichBoxEnum.FIRST_BOX.value -> currentUser.boxes.box1.toMutableList()
                WhichBoxEnum.SECOND_BOX.value -> currentUser.boxes.box2.toMutableList()
                WhichBoxEnum.THIRD_BOX.value -> currentUser.boxes.box3.toMutableList()
                WhichBoxEnum.FOURTH_BOX.value -> currentUser.boxes.box4.toMutableList()
                WhichBoxEnum.FIFTH_BOX.value -> currentUser.boxes.box5.toMutableList()
                else -> return Result.failure(Exception("Geçersiz hedef kutu."))
            }

            // Eski fotoğrafı bul ve kontrol et
            val photoIndex = fromBoxList.indexOfFirst { it.image == oldImageUrl }
            if (photoIndex == -1) {
                return Result.failure(Exception("Kaynak kutuda fotoğraf bulunamadı."))
            }

            // Eski fotoğrafın özelliklerini koru, sadece URL'i ve tarihini güncelle
            val oldPhoto = fromBoxList[photoIndex]
            val updatedPhoto = oldPhoto.copy(
                image = newImageUrl,
                date = getCurrentDate() // Tarih güncellenir
            )

            // Eski fotoğrafı kaynak kutudan kaldır
            fromBoxList.removeAt(photoIndex)

            // Yeni fotoğrafı hedef kutuya ekle
            toBoxList.add(updatedPhoto)

            // Önce kaynak kutuyu güncelle
            val updatedBoxes = when (fromBox) {
                WhichBoxEnum.FIRST_BOX.value -> currentUser.boxes.copy(box1 = fromBoxList)
                WhichBoxEnum.SECOND_BOX.value -> currentUser.boxes.copy(box2 = fromBoxList)
                WhichBoxEnum.THIRD_BOX.value -> currentUser.boxes.copy(box3 = fromBoxList)
                WhichBoxEnum.FOURTH_BOX.value -> currentUser.boxes.copy(box4 = fromBoxList)
                WhichBoxEnum.FIFTH_BOX.value -> currentUser.boxes.copy(box5 = fromBoxList)
                else -> currentUser.boxes
            }

            // Sonra hedef kutuyu güncelle
            val finalUpdatedBoxes = when (toBox) {
                WhichBoxEnum.FIRST_BOX.value -> updatedBoxes.copy(box1 = toBoxList)
                WhichBoxEnum.SECOND_BOX.value -> updatedBoxes.copy(box2 = toBoxList)
                WhichBoxEnum.THIRD_BOX.value -> updatedBoxes.copy(box3 = toBoxList)
                WhichBoxEnum.FOURTH_BOX.value -> updatedBoxes.copy(box4 = toBoxList)
                WhichBoxEnum.FIFTH_BOX.value -> updatedBoxes.copy(box5 = toBoxList)
                else -> updatedBoxes
            }

            // Güncellenmiş veriyi kaydet
            userDocument.set(
                currentUser.copy(boxes = finalUpdatedBoxes),
                SetOptions.merge()
            ).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Fotoğraf taşıma işlemi sırasında bir hata oluştu: ${e.message}"))
        }
    }


}
