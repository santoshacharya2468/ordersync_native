package com.orderpush.app.features.kds.presentation.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.orderpush.app.features.order.data.model.Order
import com.orderpush.app.features.order.data.model.OrderStatus
import com.orderpush.app.features.order.data.model.UpdateOrderRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KdsOrderActionDrawerView(onClose:()->Unit,
                             onUpdate:(UpdateOrderRequest)->Unit,
                             onPrint:()->Unit,
                             onPriority:()->Unit,
                             order: Order,){
    var showHoldOrderTimeSelectionDialog by remember { mutableStateOf(false) }
    ModalDrawerSheet(
        modifier = Modifier
            .width(200.dp)
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "#${order.externalOrderId}",
                    style = MaterialTheme.typography.titleMedium
                )

                // Close Drawer
                IconButton(onClick = { onClose() }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Drawer"
                    )
                }

            }

            // Priority (Star)
            DrawerActionButton(onClick =
                onPriority
            , label = "Priority", icon = Icons.Default.Star)

            // Print
            DrawerActionButton(onClick =
                onPrint

            ,
                label = "Print",
                icon = Icons.Default.Print
                )


            DrawerActionButton(onClick = {
                showHoldOrderTimeSelectionDialog=true
            },
                label = "Hold",
                icon = Icons.Default.Pause
                )
        }

    }

    AnimatedVisibility(
        visible = showHoldOrderTimeSelectionDialog,
        modifier = Modifier.padding(16.dp)
    ) {
        BasicAlertDialog(
            onDismissRequest = {
                showHoldOrderTimeSelectionDialog = false
            }, properties = DialogProperties(
                usePlatformDefaultWidth = false,
                )
        ) {
            OrderHoldTimeSelectionView(
                onDismiss = {
                    showHoldOrderTimeSelectionDialog = false
                },
                onSubmit = {
                    onUpdate(UpdateOrderRequest(
                        status = OrderStatus.Hold,
                        holdUnit = it
                    ))
                }
            )
        }
    }

}

@Composable
fun DrawerActionButton(onClick:()->Unit,label:String,icon:ImageVector){
    Box(
        modifier = Modifier.size(60.dp).background(color = MaterialTheme.colorScheme.onBackground,
             shape = RoundedCornerShape(4.dp)
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().clickable(onClick = onClick)
        ) {
            Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.background)
            Text(label, style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.background

            ))

        }
    }
}