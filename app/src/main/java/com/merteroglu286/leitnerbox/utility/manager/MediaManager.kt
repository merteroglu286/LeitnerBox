package com.merteroglu286.leitnerbox.utility.manager

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.FileProvider
import com.merteroglu286.leitnerbox.BuildConfig
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject


class MediaManager @Inject constructor(
    private val context: Context
) {
    private var currentPhotoUri: Uri? = null

    fun startGalleryIntent(galleryLauncher: ActivityResultLauncher<String>) {
        galleryLauncher.launch("image/*")
    }

    fun handleGalleryResult(uri: Uri?) {
        uri?.let {
            currentPhotoUri = it
        }
    }

    fun startCameraIntent(cameraLauncher: ActivityResultLauncher<Intent>) {
        val photoFile = createImageFile()
        currentPhotoUri = FileProvider.getUriForFile(
            context,
            "${BuildConfig.APPLICATION_ID}.provider",
            photoFile
        )

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri)
        }
        cameraLauncher.launch(intent)
    }

    fun getCurrentPhotoUri(): Uri? = currentPhotoUri

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }
}
