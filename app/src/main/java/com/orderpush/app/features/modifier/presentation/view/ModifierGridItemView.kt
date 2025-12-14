package com.orderpush.app.features.modifier.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.orderpush.app.features.modifier.data.model.response.MenuModifier


@Composable
fun ModifierGridItemView(modifier: MenuModifier){
    Card() {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(modifier.name)
            if (!modifier.description.isNullOrEmpty()) Text(
                modifier.description,
                style = MaterialTheme.typography.bodySmall
            )

        }
    }
}