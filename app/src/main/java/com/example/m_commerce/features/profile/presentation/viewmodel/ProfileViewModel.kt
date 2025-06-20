package com.example.m_commerce.features.profile.presentation.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.lifecycle.ViewModel
import com.example.m_commerce.features.profile.domain.model.ProfileOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel : ViewModel() {
    private val _profileState = MutableStateFlow(ProfileUiState())
    val profileState: StateFlow<ProfileUiState> = _profileState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        _profileState.value = ProfileUiState(
            profileName = "UserName",
            profileOptions = getProfileOptions()
        )
    }

    private fun getProfileOptions(): List<ProfileOption> {
        return listOf(
            ProfileOption("Your profile", Icons.Default.Person),
            ProfileOption("Manage Address", Icons.Default.LocationOn),
            ProfileOption("Payment Methods", Icons.Default.CreditCard),
            ProfileOption("My Orders", Icons.Default.ShoppingCart),
            ProfileOption("My Wishlist", Icons.Default.Favorite),
            ProfileOption("Currency", Icons.Default.CurrencyExchange),
            ProfileOption("Settings", Icons.Default.Settings),
            ProfileOption("Help Center", Icons.Default.LocationOn),
        )
    }
}

data class ProfileUiState(
    val profileName: String = "",
    val profileOptions: List<ProfileOption> = emptyList()
)