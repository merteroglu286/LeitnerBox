package com.merteroglu286.leitnerbox.os.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.merteroglu286.leitnerbox.BuildConfig
import com.merteroglu286.leitnerbox.R
import com.merteroglu286.leitnerbox.presentation.activity.main.MainActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class NotificationService : FirebaseMessagingService() {
    private val TAG = "NotificationService"
    val REFRESHED_TOKEN = BuildConfig.APPLICATION_ID + "REFRESHED_TOKEN"
    val FOREGROUND = BuildConfig.APPLICATION_ID + "FOREGROUND"


    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        val intent = Intent(this, MainActivity::class.java)

        if (p0.data.isNotEmpty()) {
            val notificationData = p0.data
            for (entry in notificationData.entries) {
                intent.putExtra(entry.key, entry.value)
            }
            Log.d(TAG, "Message notificationData payload: " + p0.data)
        }

        try {
            showNotification(
                this,
                p0.notification?.title ?: getString(R.string.app_name),
                p0.notification?.body ?: "",
                intent
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun showNotification(context: Context, title: String, body: String, intent: Intent) {

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        val notificationId = 1
        val channelId = "default"
        val channelName = "LeitnerBox"



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(
                channelId, channelName, importance
            )
            notificationManager.createNotificationChannel(mChannel)


        }
        val mBuilder = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
            .setContentText(body)
            .setAutoCancel(true)

        val resultPendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_IMMUTABLE
        )


        mBuilder.setContentIntent(resultPendingIntent)

        notificationManager.notify(notificationId, mBuilder.build())

    }

}