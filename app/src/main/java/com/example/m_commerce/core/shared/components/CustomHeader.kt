package com.example.m_commerce.core.shared.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun CustomHeader(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: Int = 16,
    fontWeight: FontWeight = FontWeight.SemiBold,
) {
    return Text(text,
        fontSize = fontSize.sp,
        fontWeight = fontWeight,
        color = Color(0xFF424242),
        modifier = modifier,
    )
}