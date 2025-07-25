package com.example.m_commerce.features.profile.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.m_commerce.R
import com.example.m_commerce.config.routes.AppRoutes
import com.example.m_commerce.config.theme.Background
import com.example.m_commerce.core.shared.components.CustomDialog
import com.example.m_commerce.core.shared.components.NetworkImage
import com.example.m_commerce.core.shared.components.default_top_bar.DefaultTopBar
import com.example.m_commerce.features.product.presentation.screen.capitalizeEachWord
import com.example.m_commerce.features.profile.domain.entity.ProfileOption
import com.example.m_commerce.features.profile.presentation.components.profile.ProfileOptionsList
import com.example.m_commerce.features.profile.presentation.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth


@Composable
fun ProfileScreenUI(
    navController: NavHostController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val name by viewModel.userNameState.collectAsStateWithLifecycle()
    val user = FirebaseAuth.getInstance().currentUser
    var showDefaultAddressDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (user != null && viewModel.isConnected()) {
            viewModel.loadName()
        }
    }
    val options: List<ProfileOption> = if (user != null) {
        listOf(
            ProfileOption("Manage Address", R.raw.address),
            ProfileOption("My Orders", R.raw.history),
            ProfileOption("My Wishlist", R.raw.heart_fill),
            ProfileOption("Currency", R.raw.currency),
            ProfileOption("Help Center", R.raw.help),
            ProfileOption("Logout", R.raw.logout),
        )
    } else {
        listOf(
            ProfileOption("Currency", R.raw.currency),
            ProfileOption("Help Center", R.raw.help),
            ProfileOption("Login", R.raw.login),
        )
    }


    Scaffold(
        topBar = {
            DefaultTopBar(title = "Profile ", navController = null)
        }) { padding ->

        CustomDialog(
            showDialog = showDefaultAddressDialog,
            title = "Confirmation",
            message = "Would you like to log out?",
            onConfirm = {
                showDefaultAddressDialog = false
                FirebaseAuth.getInstance().signOut()
                navController.navigate(AppRoutes.LoginScreen) {
                    popUpTo(AppRoutes.ProfileScreen) { inclusive = true }
                }

            },
            onDismiss = { showDefaultAddressDialog = false }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
//                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    NetworkImage(
                        url = (user?.photoUrl
                            ?: "https://images-ext-1.discordapp.net/external/7VlXibo_9bX2x9sVjwk_f6vnZxdJveVCOPfiohEzkho/https/i.pinimg.com/736x/17/c0/d1/17c0d1bfcef18ad4a83d5b5b95f328df.jpg?format=webp&width=920&height=920").toString(),
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .clip(CircleShape)
                            .border(2.dp, Gray, CircleShape)
                    )

                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    (if (user != null)name.capitalizeEachWord()  else "Guest")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))


            ProfileOptionsList(items = options) { option ->
                when (option.title) {
                    "Manage Address" -> navController.navigate(AppRoutes.ManageAddressScreen)
                    "My Orders" -> navController.navigate(AppRoutes.UserOrdersScreen)
                    "My Wishlist" -> navController.navigate(AppRoutes.WishListScreen)
                    "Currency" -> navController.navigate(AppRoutes.CurrencyScreen)
                    "Help Center" -> navController.navigate(AppRoutes.HelpCenterScreen)
                    "Login" -> navController.navigate(AppRoutes.LoginScreen)
                    "Logout" -> {
                       showDefaultAddressDialog = true
                    }
                }
            }
        }
    }
}

