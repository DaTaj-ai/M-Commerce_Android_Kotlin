package com.example.m_commerce.features.cart.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.m_commerce.config.theme.Black
import com.example.m_commerce.config.theme.Gray
import com.example.m_commerce.config.theme.Teal
import com.example.m_commerce.config.theme.White
import com.example.m_commerce.core.shared.components.CustomButton
import com.example.m_commerce.core.shared.components.SmallButton
import com.example.m_commerce.features.AddressMangment.domain.entity.Address
import com.example.m_commerce.features.AddressMangment.presentation.viewmodel.AddressViewModel
import com.example.m_commerce.features.orders.data.PaymentMethod

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutBottomSheet(
    modifier: Modifier = Modifier,
    showSheet: MutableState<Boolean>,
    addressViewModel: AddressViewModel = hiltViewModel(),
    navigateToAddresses: () -> Unit,
    onPlaceOrder: (PaymentMethod) -> Unit,
) {

    val addressState = addressViewModel.defaultAddress
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedPaymentMethod by remember { mutableStateOf(PaymentMethod.CashOnDelivery) }

    LaunchedEffect(Unit) {
        addressViewModel.getDefaultAddress()
    }


    if (showSheet.value) {
        ModalBottomSheet(
            onDismissRequest = { showSheet.value = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Shipping(navigateToAddresses, addressState)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Text("Payment Method", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(16.dp))


                PaymentOptionCard(
                    title = PaymentMethod.CashOnDelivery.value,
                    isSelected = selectedPaymentMethod == PaymentMethod.CashOnDelivery,
                    onClick = {
                        selectedPaymentMethod = PaymentMethod.CashOnDelivery
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                PaymentOptionCard(
                    title = PaymentMethod.CreditCard.value,
                    isSelected = selectedPaymentMethod == PaymentMethod.CreditCard,
                    onClick = {
                        selectedPaymentMethod = PaymentMethod.CreditCard
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                CustomButton(
                    "Place Order",
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    onPlaceOrder(selectedPaymentMethod)
                    showSheet.value = false
                }
            }
        }
    }
}

@Composable
private fun Shipping(
    navigateToAddresses: () -> Unit,
    addressState: State<Address?>
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Shipment Address", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(if (addressState.value != null) "Change" else "Add Address" , color = Teal, fontWeight = FontWeight.Bold, modifier = Modifier.clickable {
            navigateToAddresses()
        })
    }
    Spacer(modifier = Modifier.height(8.dp))
    CheckDefaultAddress(addressState)
}

@Composable
fun CheckDefaultAddress(addressState: State<Address?>) {
    if (addressState.value != null) {
        Text(addressState.value!!.address1)
    } else {
        Text("No default address found, please add one", color = Color.Red)
    }
}

@Composable
fun PaymentOptionCard(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = if (isSelected) Teal else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .shadow(2.dp, shape = RoundedCornerShape(8.dp), clip = true)
            .background(
                color = if (isSelected) Teal else White,
                shape = RoundedCornerShape(8.dp)
            )
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }

            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                color = if (isSelected) White else Black,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}