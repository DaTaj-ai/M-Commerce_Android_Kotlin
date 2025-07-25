package com.example.m_commerce.core.shared.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.m_commerce.R

@Composable
fun Empty(message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimateLottie(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp),
            resourceId = R.raw.empty
        )
        Text(
            text = message,
            modifier = Modifier.padding(horizontal = 24.dp),
            fontSize = 18.sp,
            color = Color(0xFF808080),
            textAlign = TextAlign.Center
        )
    }
}