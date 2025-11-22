package com.orderpush.app.core.views

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

@Composable
fun BackButton(){
    val navigator = LocalNavigator.currentOrThrow
    IconButton(
        onClick = {

          if( navigator.canPop){
              navigator.pop()
         }
        }
    ) {

        Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = "go back")
    }
}