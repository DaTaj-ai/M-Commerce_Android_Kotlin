package com.example.m_commerce.features.cart.presentation.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.m_commerce.BuildConfig
import com.example.m_commerce.R
import com.example.m_commerce.config.theme.Background
import com.example.m_commerce.config.theme.Black
import com.example.m_commerce.config.theme.OfferColor
import com.example.m_commerce.config.theme.Teal
import com.example.m_commerce.config.theme.TextBackground
import com.example.m_commerce.config.theme.White
import com.example.m_commerce.core.shared.components.CustomButton
import com.example.m_commerce.core.shared.components.DashedDivider
import com.example.m_commerce.core.shared.components.LottieAlertDialog
import com.example.m_commerce.core.utils.containsPositiveNumber
import com.example.m_commerce.features.cart.data.model.ReceiptItem
import com.example.m_commerce.features.cart.domain.entity.Cart
import com.example.m_commerce.features.cart.presentation.viewmodel.CartViewModel
import com.example.m_commerce.features.orders.data.PaymentMethod
import com.example.m_commerce.features.orders.data.model.variables.LineItem
import com.example.m_commerce.features.orders.presentation.ui_state.OrderUiState
import com.example.m_commerce.features.orders.presentation.viewmodel.OrderViewModel
import com.example.m_commerce.features.payment.presentation.screen.createPaymentIntent
import com.example.m_commerce.features.profile.presentation.viewmodel.CurrencyViewModel
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import kotlinx.coroutines.launch

private fun onPaymentSheetResult(
    result: PaymentSheetResult,
    items: List<LineItem>,
    onSuccess: () -> Unit
) {
    when (result) {
        is PaymentSheetResult.Completed -> {
            Log.d("Stripe", "✅ Payment completed")
            onSuccess()
        }

        is PaymentSheetResult.Canceled -> {
            Log.d("Stripe", "⚠️ Payment canceled")
        }

        is PaymentSheetResult.Failed -> {
            Log.e("Stripe", "❌ Payment failed: ${result.error.localizedMessage}")
        }
    }
}


@Composable
fun CartReceipt(
    paddingValues: PaddingValues,
    cartViewModel: CartViewModel,
    currencyViewModel: CurrencyViewModel,
//    paymentSheet: PaymentSheet,
    orderViewModel: OrderViewModel,
    cart: Cart,
    snackBarHostState: SnackbarHostState,
    navigateToAddress: () -> Unit,
) {
    val uiState by cartViewModel.uiState.collectAsState()
    var promoCode by rememberSaveable { mutableStateOf("") }

    val orderState = orderViewModel.state.collectAsStateWithLifecycle()

    val context = LocalContext.current
    var paymentIntentClientSecret by remember { mutableStateOf<String?>(null) }
    val publishableKey = BuildConfig.PAYMENT_PUBLISHABLE_KEY
    val scope = rememberCoroutineScope()
    val showSheet: MutableState<Boolean> = remember { mutableStateOf(false) }
    var showCompleteDialog by remember { mutableStateOf(false) }
    var shouldClearCart by remember { mutableStateOf(false) }


    val items = remember { mutableStateOf<List<LineItem>>(emptyList()) }

    val paymentSheet = remember {
        PaymentSheet.Builder { result ->
            onPaymentSheetResult(result, items.value) {
                orderViewModel.createOrderAndSendEmail(
                    items = items.value,
                    paymentMethod = PaymentMethod.CreditCard,
                    priceAndCurrency =  currencyViewModel.formatPrice(cart.totalAmountWithTax)
                )
            }
        }
    }.build()

    LaunchedEffect(Unit) {
        PaymentConfiguration.init(context, publishableKey)
         var rate = currencyViewModel.exchangeRate.value ?: 1.0f

        createPaymentIntent(
            amount = (cart.totalAmountWithTax.toDouble() * 100 * rate).toInt(),
            currency = currencyViewModel.defaultCurrencyCode ?: "EGP",
            callback =
            { result ->
                result.onSuccess { clientSecret ->
                    Log.d("Stripe", "✅ Got client secret: $clientSecret")
                    paymentIntentClientSecret = clientSecret
                }.onFailure { error ->
                    Log.e("Stripe", "❌ Failed to create intent: ${error.message}")
                    scope.launch {
                        snackBarHostState.showSnackbar("Payment initialization failed")
                    }
                }
            })
    }



    LaunchedEffect(orderState.value) {
        Log.d(
            "OrderItem",
            "CartReceipt: Entered Launch Effect ${orderState.value}"
        )
        when (val state = orderState.value) {
            is OrderUiState.Error -> {
                scope.launch {
                    Log.d("Order", "Error: ${state.message}")
                    snackBarHostState.showSnackbar("Error: ${state.message}")
                }
                Log.d(
                    "OrderItem",
                    "CartReceipt: Error State ${orderState.value}"
                )
            }

            is OrderUiState.Success -> {
                Log.d(
                    "OrderItem",
                    "CartReceipt: Success State ${orderState.value}"
                )
                showCompleteDialog = true
                shouldClearCart = true
            }

            OrderUiState.Idle -> {
                Log.d(
                    "OrderItem",
                    "CartReceipt: Idle"
                )

            }

            OrderUiState.Loading -> {
                Log.d(
                    "OrderItem",
                    "CartReceipt: Loading"
                )
            }
        }
    }

    LottieAlertDialog(
        showDialog = showCompleteDialog,
        onDismiss = {
            showCompleteDialog = false
            if (shouldClearCart) {
                cartViewModel.clearCart(cart.lines)
                shouldClearCart = false
            }
        },
        lottieRawResId = R.raw.success_lottie,
        btnLabel = "Continue Shopping",
        lottieSize = 240,
        text = "Order Placed Successfully"
    )

    Column(modifier = Modifier.background(Background)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(topEnd = 24.dp, topStart = 24.dp))
                .background(TextBackground)
                .padding(0.dp)
        ) {
            PromoCodeInput(
                promoCode = promoCode,
                onPromoCodeChange = { promoCode = it },
                onApplyClick = {
                    cartViewModel.applyCoupon(promoCode)
                },
                modifier = Modifier.padding(16.dp),
                cartViewModel
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                cart.let {
                    val receiptItems = listOf(
                        ReceiptItem("Subtotal", currencyViewModel.formatPrice(it.subtotalAmount)),
                        ReceiptItem(
                            "Tax",
                            currencyViewModel.formatPrice(cart.calculatedTaxAmount)
                        ),
                        ReceiptItem(
                            "Discount",
                            currencyViewModel.formatPrice(cart.discountAmount),
                            isDiscount = true
                        )
                    )
                    receiptItems.forEach { item ->
                        CartReceiptItem(item)
                    }

                    DashedDivider(Modifier.padding(vertical = 8.dp, horizontal = 16.dp))

                    CartReceiptItem(
                        ReceiptItem(
                            "Total",
                            currencyViewModel.formatPrice(cart.totalAmountWithTax)
                        )
                    )
                }
            }

            var state by remember { mutableStateOf(false) }

            CheckoutBottomSheet(showSheet = showSheet, navigateToAddresses = navigateToAddress) { paymentMethod ->

                val lineItems = cart.lines.map {
                    LineItem(
                        variantId = it.id,
                        title = it.productTitle,
                        quantity = it.quantity,
                    )

                }

                items.value = lineItems

                if (!cartViewModel.isConnected()) return@CheckoutBottomSheet

                if (paymentMethod == PaymentMethod.CreditCard) {
                    state = true
                    val lineItems = cart.lines.map {
                        LineItem(
                            variantId = it.id,
                            title = it.productTitle,
                            quantity = it.quantity
                        )
                    }
                    items.value = lineItems

                    paymentIntentClientSecret?.let {
                        paymentSheet.presentWithPaymentIntent(
                            it,
                            PaymentSheet.Configuration("My Test Store")
                        )
                    }
                    state = false
                } else {

                    Log.d("Cart", "CartReceipt: ${cart.totalAmountWithTax}")
                    val priceAndCurrency = currencyViewModel.formatPrice(cart.totalAmountWithTax)
                    Log.d("Cart", "CartReceipt: ${priceAndCurrency}")
                    orderViewModel.createOrderAndSendEmail(
                        items = lineItems,
                        paymentMethod = paymentMethod,
                        priceAndCurrency = priceAndCurrency,)

                }
            }

            CustomButton(
                modifier = Modifier
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = paddingValues.calculateBottomPadding(),
                        top = 16.dp
                    )
                    .fillMaxWidth(),
                isLoading = state,
                text = "Checkout",
                backgroundColor = Teal,
                textColor = White,
                height = 50,
                onClick = {
                    showSheet.value = true
                }
            )
        }
    }
}


@Composable
fun CartReceiptItem(item: ReceiptItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (containsPositiveNumber(item.price)) {
            Text(
                text = item.title,
                fontWeight = FontWeight.Bold,
                color = if (item.isDiscount) OfferColor else Teal
            )
            Text(
                text = item.price, color = if (item.isDiscount) OfferColor else Black
            )

        }
    }


}




