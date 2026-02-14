package com.tracky.app.receiver

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.tracky.app.MainActivity
import com.tracky.app.R
import java.util.Calendar
import kotlin.random.Random

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra(KEY_TYPE) ?: return
        val notificationId = intent.getIntExtra(KEY_NOTIFICATION_ID, 0)

        showNotification(context, type, notificationId)
        scheduleNextAlarm(context, type, notificationId)
    }

    private fun showNotification(context: Context, type: String, notificationId: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "tracky_reminders"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Daily Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders for meal tracking"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val (title, message) = getRandomMessage(type)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    private fun getRandomMessage(type: String): Pair<String, String> {
        return when (type) {
            TYPE_BREAKFAST -> {
                val messages = listOf(
                    "Review your breakfast" to "Don't forget to log your breakfast! It's the most important meal of the day.",
                    "Morning fuel" to "Have you had breakfast yet? Log it now to keep track!",
                    "Start right!" to "A good breakfast sets the tone for the day. Let's log it together!"
                )
                messages.random()
            }
            TYPE_LUNCH -> {
                val messages = listOf(
                    "Lunch time!" to "Hope you're having a nutritious lunch. Log it in Tracky!",
                    "Midday boost" to "Time to refuel! Don't forget to track your lunch.",
                    "Stay energized" to "Keep your energy up with a good lunch. Log it now!"
                )
                messages.random()
            }
            TYPE_DINNER -> {
                val messages = listOf(
                    "Dinner works" to "Wrapping up the day? Don't forget to log your dinner.",
                    "Evening meal" to "Enjoy your dinner! Remember to track it for better insights.",
                    "Daily wrap-up" to "Did you stick to your goals? Log your dinner and see!"
                )
                messages.random()
            }
            else -> "Tracky" to "Time to log your meal!"
        }
    }

    private fun scheduleNextAlarm(context: Context, type: String, notificationId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(KEY_TYPE, type)
            putExtra(KEY_NOTIFICATION_ID, notificationId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            add(Calendar.DAY_OF_YEAR, 1) // Schedule for same time tomorrow
            
            // Set hour/minute based on type to be safe, though adding 24h to current trigger time is usually enough if exact.
            // But to correct drift, better to set explicit hour/minute.
            when (type) {
                TYPE_BREAKFAST -> {
                    set(Calendar.HOUR_OF_DAY, 7)
                    set(Calendar.MINUTE, 0)
                }
                TYPE_LUNCH -> {
                    set(Calendar.HOUR_OF_DAY, 12)
                    set(Calendar.MINUTE, 0)
                }
                TYPE_DINNER -> {
                    set(Calendar.HOUR_OF_DAY, 18)
                    set(Calendar.MINUTE, 0)
                }
            }
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
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

    companion object {
        const val KEY_TYPE = "type"
        const val KEY_NOTIFICATION_ID = "notification_id"
        const val TYPE_BREAKFAST = "breakfast"
        const val TYPE_LUNCH = "lunch"
        const val TYPE_DINNER = "dinner"
    }
}
