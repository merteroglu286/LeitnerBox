package com.merteroglu286.leitnerbox.utility.extension

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.provider.MediaStore
import android.widget.ImageView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.merteroglu286.leitnerbox.presentation.fragment.image.DrawingView
import java.io.File
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

fun ImageView.loadImage(url: String) {
    if (url.isEmpty()) return
    Glide
        .with(context)
        .load(url)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .fitCenter()
        .into(this)
}

/*fun DrawingView.setBackgroundFromUrl(url: String) {
    Glide.with(context)
        .asBitmap()
        .load(url)
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                // Bitmap'i ölçeklendir ve arka plan olarak ayarla
                backgroundBitmap = Bitmap.createScaledBitmap(resource, width, height, true)
                invalidate() // Görünümü yeniden çiz
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                // Placeholder gerekirse buraya eklenebilir
            }
        })
}*/

/*fun DrawingView.setBackgroundFromUrl(url: String) {
    Glide.with(context)
        .asBitmap()
        .load(url)
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                // Bitmap'i ölçeklendir ve arka plan olarak ayarla
                backgroundBitmap = Bitmap.createScaledBitmap(resource, width, height, true)
                invalidate() // Görünümü yeniden çiz
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                // Placeholder gerekirse buraya eklenebilir
                backgroundBitmap = null
                invalidate() // Görünümü yeniden çiz
            }
        })
}*/

fun DrawingView.setBackgroundFromUrl(url: String) {
    Glide.with(this.context)
        .asBitmap()
        .load(url)
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                // Genişlik ve yükseklik kontrolü
                val width = resource.width
                val height = resource.height

                if (width > 0 && height > 0) {
                    val scaledBitmap = Bitmap.createScaledBitmap(resource, width, height, true)
                    this@setBackgroundFromUrl.background = BitmapDrawable(this@setBackgroundFromUrl.resources, scaledBitmap)
                } else {
                    // Geçersiz genişlik/yükseklik durumunda hata kaydını loglayın
                    Log.e("ImageViewExtension", "Invalid bitmap dimensions: width=$width, height=$height")
                }
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                // Yükleme iptal edildiğinde arka planı kaldırabilirsiniz
                this@setBackgroundFromUrl.background = null
            }
        })
}

fun DrawingView.getBitmap(): Bitmap {
    val bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    this.draw(canvas)
    return bitmap
}


fun uriToFile(activity: Activity, uri: Uri?): File? {
    if (uri == null)
        return null

    var filePath: String
    val cursor = activity.contentResolver.query(uri, null, null, null, null)
    if (cursor == null) {
        filePath = uri.path.orEmpty()
    } else {
        try {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            filePath = cursor.getString(idx)
            cursor.close()
        } catch (e: RuntimeException) {
            filePath =
                PathUtils.getFilePathForN(activity, uri)
        }
    }
    return File(filePath)
}
