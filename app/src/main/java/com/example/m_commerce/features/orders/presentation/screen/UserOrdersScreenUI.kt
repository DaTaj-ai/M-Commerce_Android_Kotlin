package com.example.m_commerce.features.orders.presentation.screen

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.m_commerce.core.shared.components.Empty
import com.example.m_commerce.core.shared.components.NoNetwork
import com.example.m_commerce.core.shared.components.default_top_bar.DefaultTopBar
import com.example.m_commerce.core.shared.components.screen_cases.FailedScreenCase
import com.example.m_commerce.core.shared.components.screen_cases.Loading
import com.example.m_commerce.features.orders.domain.entity.OrderHistory
import com.example.m_commerce.features.orders.presentation.components.OrderTrackingCard
import com.example.m_commerce.features.orders.presentation.ui_state.OrderHistoryUiState
import com.example.m_commerce.features.orders.presentation.viewmodel.OrderViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UserOrdersScreenUI(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: OrderViewModel = hiltViewModel()
) {

    val state by viewModel.ordersState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadOrders()
    }



    Scaffold(topBar = {
        DefaultTopBar(title = "My Orders", navController = navController)
    }) { padding ->
        when (state) {
            is OrderHistoryUiState.Error -> {
                val msg = (state as OrderHistoryUiState.Error).message
                Log.d("OrderHistory", "UserOrdersScreenUI: ${msg}")

                FailedScreenCase(msg = msg)

            }

            OrderHistoryUiState.Loading -> {
                Loading()
            }

            is OrderHistoryUiState.Success -> {
                val orders = (state as OrderHistoryUiState.Success).orders
                Log.d("OrderHistory", "UserOrdersScreenUI: ${orders}")
                LoadedData(padding, navController = navController, orders = orders)
            }

            OrderHistoryUiState.NoNetwork -> NoNetwork()
            OrderHistoryUiState.Empty -> Empty("No orders Found")
        }
    }

}

@Composable
private fun LoadedData(
    padding: PaddingValues,
    navController: NavHostController,
    orders: List<OrderHistory>
) {
    LazyColumn(
        modifier = Modifier.padding(padding), contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(orders.size) {
            OrderTrackingCard(order = orders[it])
        }
    }
}
