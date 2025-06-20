package com.example.m_commerce.features.profile.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.m_commerce.config.theme.Teal
import com.example.m_commerce.features.profile.domain.model.AddressItem

@Composable
fun AddressCard(
    item: AddressItem,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onSelect() }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = null,
                tint = Teal
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row {
                    Text(item.title, fontWeight = FontWeight.Bold)
                    if (isSelected) {
                        Text(
                            text = "Default Address",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Teal,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                Text(item.subtitle, fontSize = 12.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.width(8.dp))

//            RadioButton(
//                selected = isSelected,
//                onClick = onSelect,
//                colors = RadioButtonDefaults.colors(selectedColor = Teal)
//            )
        }

        IconButton(
            onClick = onDelete,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete address",
                tint = Color.Red,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}


