package com.orderpush.app.core.views

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp


@Composable
fun<T> PopupMenuButton(
    modifier: Modifier = Modifier,
    contentDescription: String = "More options",
    items: List<T>,
    onItemClick: (T) -> Unit,
    icon: @Composable (() -> Unit)? = {
        Icon(imageVector = Icons.Default.MoreVert, contentDescription = null, modifier = Modifier.size(24.dp))
    }
) {
    var expanded by remember { mutableStateOf(false) }

    // Button that toggles the menu
    IconButton(
        onClick = { expanded = true },
        modifier = modifier.semantics { this.contentDescription = contentDescription }
    ) {
        icon?.invoke()
    }

    // The popup menu itself
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        items.forEach { item ->
            DropdownMenuItem(
                text = { Text(item.toString()) },

                onClick = {
                    expanded = false
                    onItemClick(item)
                },
                enabled = true
            )
        }
    }
}