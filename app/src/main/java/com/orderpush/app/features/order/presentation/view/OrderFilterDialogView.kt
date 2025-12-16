package com.orderpush.app.features.order.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.orderpush.app.core.views.AppButton
import com.orderpush.app.core.views.AppButtonVariant
import com.orderpush.app.core.views.AppDropDownFormField
import com.orderpush.app.core.views.AppDropDownFormFieldMultiSelect
import com.orderpush.app.core.views.BaseView
import com.orderpush.app.core.views.DateTimeField
import com.orderpush.app.features.order.data.model.OrderFilter
import com.orderpush.app.features.order.data.model.OrderMode
import com.orderpush.app.features.order.data.model.OrderStatus
import com.orderpush.app.features.order.data.model.namePlaceHolder
import kotlinx.datetime.LocalDateTime


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderFilterDialogView(
    filter: OrderFilter,
    onChange: (OrderFilter) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier= Modifier
) {
    var mode by remember { mutableStateOf(filter.mode) }
    var statues by remember { mutableStateOf(filter.statues) }
    var orderFrom by remember { mutableStateOf(filter.fromDate) }
    var orderTo by remember { mutableStateOf(filter.toDate) }

    BaseView(
        title = "Filter Orders",

    ) {
        Column(
            modifier = modifier
                .background(color = MaterialTheme.colorScheme.background)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppDropDownFormFieldMultiSelect(
                 items = OrderStatus.entries,
                multiSelect = true,
                label = "Status",
                 selectedItems = statues,
                itemLabel = {it.name.replace("_"," ")},
                onItemsSelected = {
                    statues = it

                }
            )
            AppDropDownFormField(
                items = OrderMode.entries.toList(),
                label = "Mode",
                selectedItem = mode,
                itemLabel = { m -> m.namePlaceHolder() },
                onItemSelected = { mode = it }
            )

            DateTimeField(
                label = "Order From",
                dateTime = orderFrom,
                onDateTimeSelected = {
                  orderFrom=it
                },
                onClear = { orderFrom = null }
            )

            // Order To DateTime Field
            DateTimeField(
                label = "Order To",
                dateTime = orderTo,
                onDateTimeSelected = {   orderTo = LocalDateTime(year = it.year,monthNumber = it.monthNumber,dayOfMonth = it.dayOfMonth,hour = 23,minute = 59,second = 59)},
                onClear = { orderTo = null }
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AppButton(
                    text = "Apply",
                    onClick = {
                        val newFilter = filter.copy(
                            mode = mode,
                            statues = statues,
                            toDate = orderTo,
                            fromDate = orderFrom
                        )
                        onChange(newFilter)
                        onClose()
                    },
                    modifier = Modifier.fillMaxWidth(.5f)
                )

                Spacer(modifier = Modifier.width(20.dp))

                AppButton(
                    text = "Clear",
                    variant = AppButtonVariant.Tertiary,
                    onClick = {
                        val newFilter = filter.copy(
                            mode = null,
                            statues = emptyList(),
                            toDate = null,
                            fromDate = null
                        )
                        onChange(newFilter)
                        onClose()
                    },
                    modifier = Modifier.fillMaxWidth(1f).border(color=Color.White,shape= RoundedCornerShape(8.dp), width = 1.dp)
                )
            }
        }
    }
}

