package com.tracky.app.worker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.tracky.app.receiver.AlarmReceiver
import java.util.Calendar

object NotificationScheduler {

    fun scheduleDailyReminders(context: Context) {
        // Schedule Breakfast (7 AM)
        scheduleAlarm(context, 7, 0, AlarmReceiver.TYPE_BREAKFAST, 1001)

        // Schedule Lunch (12 PM)
        scheduleAlarm(context, 12, 0, AlarmReceiver.TYPE_LUNCH, 1002)

        // Schedule Dinner (6 PM)
        scheduleAlarm(context, 18, 0, AlarmReceiver.TYPE_DINNER, 1003)
    }

    private fun scheduleAlarm(
        context: Context,
        hour: Int,
        minute: Int,
        type: String,
        notificationId: Int
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.KEY_TYPE, type)
            putExtra(AlarmReceiver.KEY_NOTIFICATION_ID, notificationId)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // If time has passed today, schedule for tomorrow
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                 alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                // Fallback or ask for permission. For now, try non-exact or just log.
                // Best practice is to prompt user, but for now we attempt to set it.
                 alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }
}
