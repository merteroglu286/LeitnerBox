package com.merteroglu286.leitnerbox.presentation.activity.main

import android.os.Bundle
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.merteroglu286.leitnerbox.databinding.ActivityMainBinding
import com.merteroglu286.leitnerbox.presentation.base.BaseActivity
import com.merteroglu286.leitnerbox.os.notification.scheduleDailyNotification
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, MainVM>()  {

    private val TAG = "NotificationService"

    override fun getViewBinding() = ActivityMainBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getToken()
        scheduleDailyNotification(this)
    }
    private fun getToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and handle the token as needed
            Log.d(TAG, "FCM token: $token")

            // todo send your token to backend

        })
    }
}