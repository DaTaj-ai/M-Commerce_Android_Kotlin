package com.example.m_commerce.features.profile.presentation.components.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.m_commerce.R
import com.example.m_commerce.config.theme.Teal
import com.example.m_commerce.core.shared.components.SvgImage
import com.example.m_commerce.features.profile.domain.entity.ProfileOption

@Composable
fun ProfileOptionItem(option: ProfileOption, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
        ,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SvgImage(
            option.icon,
            contentDescription = option.title,
            modifier = Modifier.size(24.dp),
            colorFilter = ColorFilter.tint(if (option.icon == R.raw.logout) Color.Red else Teal)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            option.title,
            modifier = Modifier.weight(1f),
            fontSize = 16.sp,
            color = if (option.icon == R.raw.logout) Color.Red else Color.Unspecified
        )
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Go",
            modifier = Modifier.size(width = 24.dp, height = 24.dp), tint = Teal
        )
    }
}

