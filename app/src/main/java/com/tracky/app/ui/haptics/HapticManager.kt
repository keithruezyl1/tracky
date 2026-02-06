package com.tracky.app.ui.haptics

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.tracky.app.data.local.preferences.UserPreferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HapticManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesDataStore: UserPreferencesDataStore
) {
    private val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    fun vibrateSoft() {
        performHaptic {
             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(20)
            }
        }
    }
    
    fun vibrateSuccess() {
         performHaptic {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(50)
            }
        }
    }

    private fun performHaptic(action: () -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                if (preferencesDataStore.hapticsEnabled.first()) {
                    action()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
