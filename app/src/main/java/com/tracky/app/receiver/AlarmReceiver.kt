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
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra(KEY_TYPE) ?: return
        val notificationId = intent.getIntExtra(KEY_NOTIFICATION_ID, 0)

        // Check if notifications are enabled
        val pendingResult = goAsync()
        val scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.SupervisorJob() + kotlinx.coroutines.Dispatchers.Main)
        
        scope.launch {
            try {
                val preferences = com.tracky.app.data.local.preferences.UserPreferencesDataStore(context)
                preferences.notificationsEnabled.collect { enabled ->
                    if (enabled) {
                        showNotification(context, type, notificationId)
                        scheduleNextAlarm(context, type, notificationId)
                    }
                    this@launch.cancel() // Stop collecting after one check
                }
            } finally {
                pendingResult.finish()
            }
        }
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
            notificationId, // Use notificationId for uniqueness
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
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
                    "Rise and Shine! ☀️" to "Start your day with a healthy breakfast. Log it now to stay on track!",
                    "Good Morning, Champion! 🏆" to "Fuel up for the day ahead. Don't forget to track your breakfast.",
                    "Morning Energy Boost ⚡" to "A good breakfast sets the tone. Log it in seconds!"
                )
                messages.random()
            }
            TYPE_LUNCH -> {
                val messages = listOf(
                    "It's Lunchtime! 🥗" to "Time to refuel! Record your lunch and keep your streak alive.",
                    "Midday Fuel Stop ⛽" to "Taking a break? Log your meal to see how your macros look.",
                    "Don't Skip a Beat 🎵" to "Enjoy your lunch and track it instantly in Tracky."
                )
                messages.random()
            }
            TYPE_DINNER -> {
                val messages = listOf(
                    "Dinner is Served 🍽️" to "Winding down? Finish your day strong by logging your dinner.",
                    "Evening Wrap-Up 🌙" to "What's on the menu tonight? One tap to log your final meal.",
                    "Close Your Rings 🎯" to "Last meal of the day! Log it now to complete your daily diary."
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
                    set(Calendar.HOUR_OF_DAY, 6) // Updated to 6 AM
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
