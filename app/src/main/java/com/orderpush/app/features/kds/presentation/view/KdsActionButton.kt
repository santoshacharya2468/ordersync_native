package com.orderpush.app.features.kds.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun KdsActionButton(icon: (@Composable ()->Unit),text:String,
                    counterValue:Int=0,
                    onClick:()->Unit,
                    ){
    Box(){
        Column(
            modifier = Modifier
                .padding(4.dp)
                .clickable {
                    onClick()
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            icon()
            Text(text = text)
        }
        if (counterValue!=0)Box(
            modifier = Modifier.size(18.dp).background(Color.Red, CircleShape),
            contentAlignment = Alignment.Center
        ){
            Text("$counterValue", style = MaterialTheme.typography.labelSmall.copy(
                 color = Color.White,

            ))
        }
    }
}