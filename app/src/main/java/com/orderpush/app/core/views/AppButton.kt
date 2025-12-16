package com.orderpush.app.core.views

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    variant: AppButtonVariant = AppButtonVariant.Primary,
    size: AppButtonSize = AppButtonSize.Large,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val buttonColors = when (variant) {
        AppButtonVariant.Primary -> ButtonDefaults.buttonColors()
        AppButtonVariant.Secondary -> ButtonDefaults.outlinedButtonColors()
        AppButtonVariant.Tertiary -> ButtonDefaults.textButtonColors()
        AppButtonVariant.Error -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError
        )
    }

    val buttonModifier = modifier
        .then(
            when (size) {
                AppButtonSize.Small -> Modifier.height(32.dp)
                AppButtonSize.Medium -> Modifier.height(40.dp)
                AppButtonSize.Large -> Modifier.height(56.dp)
            }
        )

    val contentPadding = when (size) {
        AppButtonSize.Small -> PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        AppButtonSize.Medium -> PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        AppButtonSize.Large -> PaddingValues(horizontal = 20.dp, vertical = 12.dp)
    }

    val textStyle = when (size) {
        AppButtonSize.Small -> MaterialTheme.typography.labelSmall
        AppButtonSize.Medium -> MaterialTheme.typography.labelMedium
        AppButtonSize.Large -> MaterialTheme.typography.labelLarge
    }


            Button(
                onClick = onClick,
                modifier = buttonModifier,
                enabled = enabled && !loading,
                colors = buttonColors,
                contentPadding = contentPadding,
                interactionSource = interactionSource,
                shape = RoundedCornerShape(10.dp)

            ) {
                ButtonContent(
                    text = text,
                    loading = loading,
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                    textStyle = textStyle
                )
            }



}

@Composable
private fun ButtonContent(
    text: String,
    loading: Boolean,
    leadingIcon: (@Composable () -> Unit)?,
    trailingIcon: (@Composable () -> Unit)?,
    textStyle: androidx.compose.ui.text.TextStyle
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp,
                color = LocalContentColor.current
            )
            Spacer(modifier = Modifier.width(8.dp))
        } else {
            leadingIcon?.let {
                it()
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        Text(
            text = text,
            style = textStyle
        )

        if (!loading) {
            trailingIcon?.let {
                Spacer(modifier = Modifier.width(8.dp))
                it()
            }
        }
    }
}

enum class AppButtonVariant {
    Primary,    // Filled button
    Secondary,  // Outlined button
    Tertiary,   // Text button
    Error       // Error state button
}

enum class AppButtonSize {
    Small,
    Medium,
    Large
}