package com.orderpush.app.features.order.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orderpush.app.features.order.data.model.Order
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock


@Composable
fun OrderTimeRemainingView(order: Order) {
    var timeRemaining by remember { mutableStateOf(0L) }

    LaunchedEffect(order.id) {
        while (isActive) {
            val nowUtc = Clock.System.now()
            timeRemaining = (order.fulfillmentTime - nowUtc).inWholeMinutes
            delay(5000) // Update every 5 seconds
        }
    }

    val (backgroundColor, textColor, displayText) = when {
        timeRemaining < 0 -> Triple(
            Color(0xFFFFCDD2),
            Color(0xFFC62828),
            formatTimeRemaining(timeRemaining)
        )
        timeRemaining in 0..5 -> Triple(
            Color(0xFFFFE0B2),
            Color(0xFFE65100),
            formatTimeRemaining(timeRemaining)
        )
        timeRemaining in 6..15 -> Triple(
            Color(0xFFFFF9C4),
            Color(0xFFF57F17),
            formatTimeRemaining(timeRemaining)
        )
        else -> Triple(
            Color(0xFFE8F5E9),
            Color(0xFF2E7D32),
            formatTimeRemaining(timeRemaining)
        )
    }

    Box(
        modifier = Modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = displayText,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = textColor,
            fontSize = 12.sp
        )
    }
}

private fun formatTimeRemaining(totalMinutes: Long): String {
    val absMinutes = kotlin.math.abs(totalMinutes)
    val hours = absMinutes / 60
    val minutes = absMinutes % 60
    val prefix = if (totalMinutes < 0) "-" else ""

    return when {
        hours == 0L -> "${prefix}${minutes}mins"
        minutes == 0L -> "${prefix}${hours}hour${if (hours > 1) "s" else ""}"
        else -> "${prefix}${hours}h ${minutes}mins"
    }
}