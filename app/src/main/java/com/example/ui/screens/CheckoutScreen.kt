package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Contactless
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CartManager
import com.example.data.TransactionManager
import com.example.model.Transaction
import com.example.ui.theme.CaramelPrimary
import com.example.ui.theme.CaramelPrimaryContainer
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.OnPrimaryContainer
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.OnSurfaceWarm
import com.example.ui.theme.OutlineVariant
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceDark
import com.example.util.toRupiah

@Composable
fun CheckoutScreen(
    onBackClick: () -> Unit,
    onPaymentSuccess: (Transaction) -> Unit
) {
    val cartItems by CartManager.cartItems.collectAsState()
    val subtotal = cartItems.sumOf { it.itemTotal }
    val tax = subtotal * 0.08
    val totalAmount = subtotal + tax

    var selectedPaymentMethod by remember { mutableStateOf("Cash") }
    var rawReceivedInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val receivedAmount = rawReceivedInput.toDoubleOrNull() ?: 0.0
    val changeDue = if (receivedAmount >= totalAmount) receivedAmount - totalAmount else 0.0

    val scrollState = rememberScrollState()

    val handleDigitClick: (String) -> Unit = { digit ->
        if (rawReceivedInput.length < 9) {
            rawReceivedInput += digit
            errorMessage = null
        }
    }

    val handleBackspace: () -> Unit = {
        if (rawReceivedInput.isNotEmpty()) {
            rawReceivedInput = rawReceivedInput.dropLast(1)
            errorMessage = null
        }
    }

    val handleQuickTender: (Double) -> Unit = { amount ->
        rawReceivedInput = amount.toLong().toString()
        errorMessage = null
    }

    val handleCompleteTender: () -> Unit = {
        if (selectedPaymentMethod == "Cash" && receivedAmount < totalAmount) {
            errorMessage = "Received amount (${receivedAmount.toRupiah()}) is less than total due (${totalAmount.toRupiah()})"
        } else {
            val finalPayment = if (selectedPaymentMethod == "Cash") receivedAmount else totalAmount
            val finalChange = if (selectedPaymentMethod == "Cash") changeDue else 0.0

            val transaction = TransactionManager.createTransaction(
                items = cartItems,
                total = totalAmount,
                payment = finalPayment,
                change = finalChange,
                paymentMethod = selectedPaymentMethod
            )
            CartManager.clearCart()
            onPaymentSuccess(transaction)
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceDark)
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(SurfaceContainer)
                            .border(1.dp, OutlineVariant, CircleShape)
                            .testTag("checkout_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = OnSurfaceWarm
                        )
                    }

                    Text(
                        text = "Checkout",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = OnSurfaceWarm
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(CaramelPrimaryContainer.copy(alpha = 0.2f))
                        .border(1.dp, CaramelPrimary.copy(alpha = 0.3f), RoundedCornerShape(50))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Order #842",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = CaramelPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        },
        containerColor = SurfaceDark
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Total Amount Due Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(24.dp), spotColor = CaramelPrimary)
                    .testTag("checkout_total_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                border = androidx.compose.foundation.BorderStroke(1.dp, CaramelPrimary.copy(alpha = 0.2f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    CaramelPrimaryContainer.copy(alpha = 0.08f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(vertical = 24.dp, horizontal = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Total Amount Due",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.sp
                            ),
                            color = OnSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = totalAmount.toRupiah(),
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 34.sp,
                                color = CaramelPrimary
                            )
                        )
                    }
                }
            }

            // 2. Payment Method Selector
            Column {
                Text(
                    text = "Payment Method",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = OnSurfaceWarm,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PaymentMethodTab(
                        title = "Cash",
                        icon = Icons.Default.Payments,
                        isSelected = selectedPaymentMethod == "Cash",
                        modifier = Modifier.weight(1f),
                        onClick = { selectedPaymentMethod = "Cash" }
                    )

                    PaymentMethodTab(
                        title = "Card",
                        icon = Icons.Default.CreditCard,
                        isSelected = selectedPaymentMethod == "Card",
                        modifier = Modifier.weight(1f),
                        onClick = { selectedPaymentMethod = "Card" }
                    )

                    PaymentMethodTab(
                        title = "NFC / Tap",
                        icon = Icons.Default.Contactless,
                        isSelected = selectedPaymentMethod == "NFC / Tap",
                        modifier = Modifier.weight(1f),
                        onClick = { selectedPaymentMethod = "NFC / Tap" }
                    )
                }
            }

            // 3. Order Items Summary Tray
            Column {
                Text(
                    text = "Order Items (${cartItems.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = OnSurfaceWarm,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                ) {
                    Column {
                        cartItems.forEachIndexed { index, item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(SurfaceContainerHigh),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Coffee,
                                            contentDescription = null,
                                            tint = OnSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = "${item.quantity}x ${item.product.name}",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                            color = OnSurfaceWarm
                                        )
                                        Text(
                                            text = item.size,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = OnSurfaceVariant
                                        )
                                    }
                                }

                                Text(
                                    text = item.itemTotal.toRupiah(),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = OnSurfaceWarm
                                )
                            }

                            if (index < cartItems.size - 1) {
                                HorizontalDivider(color = SurfaceDark, thickness = 1.dp)
                            }
                        }
                    }
                }
            }

            // 4. Cash Tender Section (Interactive Keypad)
            if (selectedPaymentMethod == "Cash") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Received Display Row
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                            border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Received",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = OnSurfaceVariant
                                )
                                Text(
                                    text = if (rawReceivedInput.isEmpty()) "0" else receivedAmount.toRupiah(),
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = OnSurfaceWarm,
                                    modifier = Modifier.testTag("received_amount_text")
                                )
                            }
                        }

                        // Change Due Display Row
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CaramelPrimary.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Change Due",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                                    color = CaramelPrimary
                                )
                                Text(
                                    text = changeDue.toRupiah(),
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = CaramelPrimary
                                    ),
                                    modifier = Modifier.testTag("change_due_text")
                                )
                            }
                        }

                        // Error Banner if underpaid
                        AnimatedVisibility(visible = errorMessage != null) {
                            Text(
                                text = errorMessage ?: "",
                                color = ErrorRed,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }

                        // Numeric Keypad
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                KeypadKey("1", Modifier.weight(1f)) { handleDigitClick("1") }
                                KeypadKey("2", Modifier.weight(1f)) { handleDigitClick("2") }
                                KeypadKey("3", Modifier.weight(1f)) { handleDigitClick("3") }
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                KeypadKey("4", Modifier.weight(1f)) { handleDigitClick("4") }
                                KeypadKey("5", Modifier.weight(1f)) { handleDigitClick("5") }
                                KeypadKey("6", Modifier.weight(1f)) { handleDigitClick("6") }
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                KeypadKey("7", Modifier.weight(1f)) { handleDigitClick("7") }
                                KeypadKey("8", Modifier.weight(1f)) { handleDigitClick("8") }
                                KeypadKey("9", Modifier.weight(1f)) { handleDigitClick("9") }
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                KeypadKey("000", Modifier.weight(1f)) { handleDigitClick("000") }
                                KeypadKey("0", Modifier.weight(1f)) { handleDigitClick("0") }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(52.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(SurfaceContainer)
                                        .border(1.dp, OutlineVariant.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                        .clickable { handleBackspace() }
                                        .testTag("keypad_backspace"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Backspace,
                                        contentDescription = "Backspace",
                                        tint = ErrorRed,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            // Quick Tender Buttons
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp)
                            ) {
                                QuickTenderBtn("Rp 50k", Modifier.weight(1f)) { handleQuickTender(50000.0) }
                                QuickTenderBtn("Rp 100k", Modifier.weight(1f)) { handleQuickTender(100000.0) }
                                QuickTenderBtn("Exact", Modifier.weight(1f)) { handleQuickTender(totalAmount) }
                            }
                        }
                    }
                }
            }

            // 5. Complete Tender Action Button
            Button(
                onClick = handleCompleteTender,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .shadow(12.dp, RoundedCornerShape(50), spotColor = CaramelPrimary)
                    .testTag("complete_tender_button"),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CaramelPrimaryContainer,
                    contentColor = OnPrimaryContainer
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Complete Tender",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PaymentMethodTab(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) CaramelPrimaryContainer else SurfaceContainer)
            .border(
                width = 1.dp,
                color = if (isSelected) Color.Transparent else OutlineVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp)
            )
            .then(
                if (isSelected) Modifier.shadow(8.dp, RoundedCornerShape(16.dp), spotColor = CaramelPrimary)
                else Modifier
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = CaramelPrimary)
            ) { onClick() }
            .testTag("payment_method_$title"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) OnPrimaryContainer else OnSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = if (isSelected) OnPrimaryContainer else OnSurfaceVariant
            )
        }
    }
}

@Composable
private fun KeypadKey(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceContainer)
            .border(1.dp, OutlineVariant.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = CaramelPrimary)
            ) { onClick() }
            .testTag("keypad_btn_$label"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = OnSurfaceWarm
        )
    }
}

@Composable
private fun QuickTenderBtn(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceDark)
            .border(1.dp, OutlineVariant.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag("quick_tender_$label"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = OnSurfaceVariant
        )
    }
}
