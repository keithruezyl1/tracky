package com.tracky.app.ui.utils

import java.util.Locale

/**
 * Extension to format Float values "smartly":
 * - If the value is a whole number (e.g. 100.0), display as "100"
 * - If the value has decimals (e.g. 100.5), display as "100.5"
 */
fun Float.toSmartString(): String {
    return if (this % 1.0f == 0f) {
        String.format(Locale.getDefault(), "%.0f", this)
    } else {
        String.format(Locale.getDefault(), "%.1f", this)
    }
}

/**
 * Extension to format Double values "smartly":
 * - If the value is a whole number (e.g. 100.0), display as "100"
 * - If the value has decimals (e.g. 100.5), display as "100.5"
 */
fun Double.toSmartString(): String {
    return if (this % 1.0 == 0.0) {
        String.format(Locale.getDefault(), "%.0f", this)
    } else {
        String.format(Locale.getDefault(), "%.1f", this)
    }
}
