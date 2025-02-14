package com.merteroglu286.leitnerbox.os.notification

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit
import java.util.*

fun scheduleDailyNotification(context: Context) {
    val currentTime = Calendar.getInstance()
    val scheduleTime = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 8)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }

    if (currentTime.after(scheduleTime)) {
        scheduleTime.add(Calendar.DAY_OF_MONTH, 1)
    }

    val delay = scheduleTime.timeInMillis - currentTime.timeInMillis
    val dailyWorkRequest = OneTimeWorkRequestBuilder<DailyNotificationWorker>()
        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
        .build()

    WorkManager.getInstance(context).enqueue(dailyWorkRequest)
}
