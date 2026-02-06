package com.tracky.app.worker

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.Calendar
import java.util.concurrent.TimeUnit

object NotificationScheduler {

    fun scheduleDailyReminders(context: Context) {
        val workManager = WorkManager.getInstance(context)

        scheduleReminder(
            workManager,
            hour = 7,
            minute = 0,
            title = "Good morning!",
            message = "Kaon tarong ha :)",
            tag = "reminder_7am"
        )

        scheduleReminder(
            workManager,
            hour = 11,
            minute = 0,
            title = "Lunch Reminder",
            message = "Ayaw pag softdrinks ig kaon migo!",
            tag = "reminder_11am"
        )

        scheduleReminder(
            workManager,
            hour = 18,
            minute = 0,
            title = "Dinner Reminder",
            message = "Ayaw palabi kaon ha!",
            tag = "reminder_6pm"
        )
    }

    private fun scheduleReminder(
        workManager: WorkManager,
        hour: Int,
        minute: Int,
        title: String,
        message: String,
        tag: String
    ) {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (target.before(now)) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }

        val initialDelay = target.timeInMillis - now.timeInMillis

        val workRequest = PeriodicWorkRequestBuilder<DailyNotificationWorker>(
            24, TimeUnit.HOURS
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf(
                    DailyNotificationWorker.KEY_TITLE to title,
                    DailyNotificationWorker.KEY_MESSAGE to message
                )
            )
            .addTag(tag)
            .build()
        // Use UPDATE so we don't duplicate or cancel running jobs unnecessarily, 
        // but if we change the time/message logic, clean install or REPLACE might be needed. 
        // CANCEL_AND_REENQUEUE is safer during dev to ensure new times apply.
        // Actually UPDATE is API 26+? No, ExistingPeriodicWorkPolicy.UPDATE is recent.
        // Default to KEEP or UPDATE. UPDATE is best.
        // But to be safe with older WorkManager versions (just added 2.9.0), UPDATE is fine.
        workManager.enqueueUniquePeriodicWork(
            tag,
            ExistingPeriodicWorkPolicy.UPDATE, 
            workRequest
        )
    }
}
