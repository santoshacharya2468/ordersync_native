package com.orderpush.app.core.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp



@Composable
fun<T> SegmentedTabView(
    tabs: List<T>,
    selectedValue: T?,
    onTabSelected: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(30.dp))
            .border(1.dp,Color.White, shape = RoundedCornerShape(30.dp))
    ) {
        tabs.forEach {  menu ->
            val isSelected =  menu == selectedValue

            val backgroundColor = if (isSelected) Color.White else MaterialTheme.colorScheme.background
            val textColor = if (isSelected) Color.Black else Color.White

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(30.dp))
                    .background(backgroundColor)
                    .clickable { onTabSelected(menu) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = menu.toString(),
                    color = textColor,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
