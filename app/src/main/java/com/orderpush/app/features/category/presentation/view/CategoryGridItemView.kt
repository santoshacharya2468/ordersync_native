package com.orderpush.app.features.category.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.orderpush.app.features.category.data.model.response.Category
import org.w3c.dom.Text


@Composable
fun CategoryGridItemView(category: Category){
    Card(

    ) {

        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ){
            Text(category.name)
            if (!category.description.isNullOrEmpty()) Text(
                category.description.toString(),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}