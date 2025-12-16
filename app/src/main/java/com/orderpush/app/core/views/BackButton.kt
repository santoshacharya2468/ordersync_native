package com.orderpush.app.core.views

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import com.orderpush.app.core.router.LocalNavigation

@Composable
fun BackButton(){
    val navigator = LocalNavigation.current
    IconButton(
        onClick = {

          if( navigator.canPop()){
              navigator.pop()
         }
        }
    ) {

        Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = "go back")
    }
}