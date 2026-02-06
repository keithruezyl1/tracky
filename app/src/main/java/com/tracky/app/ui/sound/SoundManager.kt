package com.tracky.app.ui.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.tracky.app.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val soundPool: SoundPool
    private val dingSoundId: Int
    private val popSoundId: Int
    private val crumpleSoundId: Int

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(3)
            .setAudioAttributes(audioAttributes)
            .build()
        
        // Load sounds from raw resources
        dingSoundId = loadSoundSafe(R.raw.ding)
        popSoundId = loadSoundSafe(R.raw.pop)
        crumpleSoundId = loadSoundSafe(R.raw.crumple)
    }

    private fun loadSoundSafe(resId: Int): Int {
        return try {
            soundPool.load(context, resId, 1)
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }
    
    fun playDing() {
        try {
            if (dingSoundId != 0) soundPool.play(dingSoundId, 1f, 1f, 1, 0, 1f)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playPop() {
        try {
            if (popSoundId != 0) soundPool.play(popSoundId, 1f, 1f, 1, 0, 1f)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playCrumple() {
        try {
            if (crumpleSoundId != 0) soundPool.play(crumpleSoundId, 1f, 1f, 1, 0, 1f)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun release() {
        soundPool.release()
    }
}
