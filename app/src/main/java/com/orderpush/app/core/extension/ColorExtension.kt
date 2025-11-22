package com.orderpush.app.core.extension

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt

fun String.toUIColor(): Color {
    return try {
        Color(this.toColorInt())
    } catch (e: Exception) {
        Color.Black // fallback color if parsing fails
    }
}
