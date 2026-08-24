package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.example.ui.theme.TertiaryContainerGreen
import com.example.ui.theme.TertiaryGreen
import com.example.util.toRupiah
import kotlinx.coroutines.delay

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
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = SurfaceDark
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
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
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PaymentMethodTab(
                        title = "Cash",
                        icon = Icons.Default.Payments,
                        isSelected = selectedPaymentMethod == "Cash",
                        modifier = Modifier.weight(1f),
                        onClick = { selectedPaymentMethod = "Cash" }
                    )

                    PaymentMethodTab(
                        title = "QRIS",
                        icon = Icons.Default.QrCode2,
                        isSelected = selectedPaymentMethod == "QRIS",
                        modifier = Modifier.weight(1f),
                        onClick = { selectedPaymentMethod = "QRIS" }
                    )

                    PaymentMethodTab(
                        title = "Card",
                        icon = Icons.Default.CreditCard,
                        isSelected = selectedPaymentMethod == "Card",
                        modifier = Modifier.weight(1f),
                        onClick = { selectedPaymentMethod = "Card" }
                    )

                    PaymentMethodTab(
                        title = "NFC",
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
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = item.size,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = OnSurfaceVariant
                                            )
                                            if (item.notes.isNotBlank()) {
                                                Text(
                                                    text = "• ${item.notes}",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                                    color = CaramelPrimary
                                                )
                                            }
                                        }
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

            // 4. Payment Tender Section based on selected method
            when (selectedPaymentMethod) {
                "Cash" -> {
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
                "QRIS" -> {
                    QrisDummyDisplay(
                        totalAmount = totalAmount,
                        onSimulatePayment = handleCompleteTender
                    )
                }
                "Card" -> {
                    CardTenderDisplay(
                        totalAmount = totalAmount,
                        onProcessPayment = handleCompleteTender
                    )
                }
                "NFC / Tap" -> {
                    NfcTenderDisplay(
                        totalAmount = totalAmount,
                        onProcessPayment = handleCompleteTender
                    )
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
                        imageVector = when (selectedPaymentMethod) {
                            "QRIS" -> Icons.Default.QrCode2
                            "Card" -> Icons.Default.CreditCard
                            "NFC / Tap" -> Icons.Default.Contactless
                            else -> Icons.Default.CheckCircle
                        },
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = when (selectedPaymentMethod) {
                            "QRIS" -> "Konfirmasi Pembayaran QRIS"
                            "Card" -> "Proses Pembayaran EDC"
                            "NFC / Tap" -> "Konfirmasi Tap / NFC"
                            else -> "Complete Tender"
                        },
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

@Composable
private fun QrisDummyDisplay(
    totalAmount: Double,
    onSimulatePayment: () -> Unit
) {
    var countdownSeconds by remember { mutableIntStateOf(300) }

    LaunchedEffect(Unit) {
        while (countdownSeconds > 0) {
            delay(1000)
            countdownSeconds--
        }
    }

    val minutes = countdownSeconds / 60
    val seconds = countdownSeconds % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

    val infiniteTransition = rememberInfiniteTransition(label = "qris_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(24.dp))
            .testTag("qris_dummy_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF7F2)),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFE5DDD0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // QRIS Official Header Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // QRIS Brand Tag
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFCE1126)) // Authentic QRIS red
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "QRIS",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        )
                    }

                    Column {
                        Text(
                            text = "STANDAR PEMBAYARAN NASIONAL",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E1A16),
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Bank Indonesia & ASPI",
                            fontSize = 8.sp,
                            color = Color(0xFF756A5D)
                        )
                    }
                }

                // GPN Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF003B73)) // GPN Blue
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "GPN",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFE5DDD0), thickness = 1.dp)

            // Merchant Information
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "CASH & BREW SPECIALTY COFFEE",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = Color(0xFF1E1A16),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "NMID: ID1024395819001 • A01",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp
                    ),
                    color = Color(0xFF756A5D)
                )
            }

            // QR Code Matrix Box
            Card(
                modifier = Modifier
                    .size(220.dp)
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0D8CE))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    QrisMatrixCanvas(
                        modifier = Modifier.fillMaxSize()
                    )

                    // Center Coffee Emblem
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(2.dp, CaramelPrimaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Coffee,
                            contentDescription = null,
                            tint = CaramelPrimaryContainer,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Amount to Pay Display
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFEDE6DA)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Total Pembayaran",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF5D4037)
                        )
                        Text(
                            text = totalAmount.toRupiah(),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF2C1810)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFE8F5E9))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "MDR 0% Gratis",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }

            // Real-time Status and Countdown Timer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .alpha(pulseAlpha)
                            .clip(CircleShape)
                            .background(Color(0xFFE65100))
                    )
                    Text(
                        text = "Menunggu scan pelanggan...",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        color = Color(0xFF5D4037)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = Color(0xFF756A5D),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = Color(0xFF756A5D)
                    )
                }
            }

            // Customer Payment Simulator Button
            OutlinedButton(
                onClick = onSimulatePayment,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("simulate_qris_payment_btn"),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF1E1A16)
                ),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, CaramelPrimaryContainer)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = TertiaryGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Simulasikan Scan & Bayar Sukses",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            // Accepted Wallets Footer Note
            Text(
                text = "Menerima: BCA • Mandiri • BRI • BNI • GoPay • OVO • DANA • ShopeePay • LinkAja",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = Color(0xFF8D7F71),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun QrisMatrixCanvas(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val moduleCount = 21
        val moduleW = w / moduleCount
        val moduleH = h / moduleCount
        val dotColor = Color(0xFF1A1412)

        // Helper to draw QR finder pattern (7x7 module square)
        fun drawFinder(startX: Int, startY: Int) {
            // Outer 7x7 black box
            drawRect(
                color = dotColor,
                topLeft = Offset(startX * moduleW, startY * moduleH),
                size = Size(7 * moduleW, 7 * moduleH)
            )
            // Inner 5x5 white cutout
            drawRect(
                color = Color.White,
                topLeft = Offset((startX + 1) * moduleW, (startY + 1) * moduleH),
                size = Size(5 * moduleW, 5 * moduleH)
            )
            // Inner 3x3 solid black center
            drawRect(
                color = dotColor,
                topLeft = Offset((startX + 2) * moduleW, (startY + 2) * moduleH),
                size = Size(3 * moduleW, 3 * moduleH)
            )
        }

        // 3 Standard Position Finder Patterns
        drawFinder(0, 0)
        drawFinder(moduleCount - 7, 0)
        drawFinder(0, moduleCount - 7)

        // Deterministic QR data module pattern generator for authentic look
        val seedPattern = intArrayOf(
            1, 0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 1,
            0, 1, 0, 0, 1, 1, 0, 1, 0, 1, 0, 0, 1, 1, 0, 1, 1, 0, 1, 0, 1,
            1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 0,
            0, 0, 1, 1, 0, 1, 1, 0, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 1, 0,
            1, 0, 0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1
        )

        for (r in 0 until moduleCount) {
            for (c in 0 until moduleCount) {
                // Skip finder areas
                val inTopLeftFinder = r < 8 && c < 8
                val inTopRightFinder = r < 8 && c >= moduleCount - 8
                val inBottomLeftFinder = r >= moduleCount - 8 && c < 8
                val inCenterLogo = r in 8..12 && c in 8..12

                if (!inTopLeftFinder && !inTopRightFinder && !inBottomLeftFinder && !inCenterLogo) {
                    val idx = (r * 11 + c * 7 + (r xor c)) % seedPattern.size
                    if (seedPattern[idx] == 1) {
                        drawRect(
                            color = dotColor,
                            topLeft = Offset(c * moduleW + 0.5f, r * moduleH + 0.5f),
                            size = Size(moduleW - 1f, moduleH - 1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CardTenderDisplay(
    totalAmount: Double,
    onProcessPayment: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(CaramelPrimaryContainer.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CreditCard,
                    contentDescription = null,
                    tint = CaramelPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "EDC Machine Terminal",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = OnSurfaceWarm
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Swipe, Dip, or Insert Card on POS Terminal",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = SurfaceDark
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Amount to Charge", color = OnSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                    Text(text = totalAmount.toRupiah(), color = CaramelPrimary, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
            }

            OutlinedButton(
                onClick = onProcessPayment,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CaramelPrimary),
                border = androidx.compose.foundation.BorderStroke(1.dp, CaramelPrimary)
            ) {
                Text("Simulate Card Approved (EDC)", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun NfcTenderDisplay(
    totalAmount: Double,
    onProcessPayment: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(TertiaryContainerGreen.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Contactless,
                    contentDescription = null,
                    tint = TertiaryGreen,
                    modifier = Modifier.size(28.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Contactless / Tap to Pay",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = OnSurfaceWarm
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tap phone, smartwatch, or contactless card on reader",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = SurfaceDark
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Amount to Charge", color = OnSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                    Text(text = totalAmount.toRupiah(), color = CaramelPrimary, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
            }

            OutlinedButton(
                onClick = onProcessPayment,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TertiaryGreen),
                border = androidx.compose.foundation.BorderStroke(1.dp, TertiaryGreen)
            ) {
                Text("Simulate NFC Tap Success", fontWeight = FontWeight.Bold)
            }
        }
    }
}
