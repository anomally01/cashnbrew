package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalPrintshop
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.AuthManager
import com.example.model.Transaction
import com.example.ui.theme.CaramelPrimary
import com.example.ui.theme.OnPrimary
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.OnSurfaceWarm
import com.example.ui.theme.OutlineVariant
import com.example.ui.theme.SecondaryWarm
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHighest
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TertiaryContainerGreen
import com.example.ui.theme.TertiaryGreen
import com.example.util.toRupiah
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PaymentSuccessScreen(
    transaction: Transaction,
    onNewOrder: () -> Unit,
    onViewHistory: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var showReceiptModal by remember { mutableStateOf(false) }

    // Entrance Animation States
    val iconScale = remember { Animatable(0f) }
    val iconAlpha = remember { Animatable(0f) }
    val glowScale = remember { Animatable(0.4f) }
    val glowAlpha = remember { Animatable(0f) }

    val textAlpha = remember { Animatable(0f) }
    val textOffsetY = remember { Animatable(30f) }

    val cardAlpha = remember { Animatable(0f) }
    val cardOffsetY = remember { Animatable(40f) }

    val buttonsAlpha = remember { Animatable(0f) }
    val buttonsOffsetY = remember { Animatable(40f) }

    // Subtle Continuous Pulse on Check Badge Glow
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_transition")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    val pulseRingAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_ring_alpha"
    )

    LaunchedEffect(Unit) {
        // Step 1: Ambient background glow blooms
        launch {
            glowScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
            )
        }
        launch {
            glowAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
            )
        }

        // Step 2: Check icon bounces in with spring dynamics
        launch {
            iconAlpha.animateTo(1f, animationSpec = tween(200))
        }
        launch {
            iconScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }

        delay(200)

        // Step 3: Header Texts slide & fade in
        launch {
            textAlpha.animateTo(1f, animationSpec = tween(400, easing = FastOutSlowInEasing))
        }
        launch {
            textOffsetY.animateTo(0f, animationSpec = tween(400, easing = FastOutSlowInEasing))
        }

        delay(150)

        // Step 4: Receipt Card slides & fades in
        launch {
            cardAlpha.animateTo(1f, animationSpec = tween(450, easing = FastOutSlowInEasing))
        }
        launch {
            cardOffsetY.animateTo(0f, animationSpec = tween(450, easing = FastOutSlowInEasing))
        }

        delay(150)

        // Step 5: Buttons fade & slide in
        launch {
            buttonsAlpha.animateTo(1f, animationSpec = tween(400, easing = FastOutSlowInEasing))
        }
        launch {
            buttonsOffsetY.animateTo(0f, animationSpec = tween(400, easing = FastOutSlowInEasing))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        // Ambient Green Radial Glow in background
        Box(
            modifier = Modifier
                .size(340.dp)
                .scale(glowScale.value)
                .alpha(glowAlpha.value)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            TertiaryGreen.copy(alpha = 0.2f),
                            TertiaryContainerGreen.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Success Check Icon with Glow & Ripple Ring
            Box(
                modifier = Modifier
                    .scale(iconScale.value)
                    .alpha(iconAlpha.value),
                contentAlignment = Alignment.Center
            ) {
                // Outer Ripple Ring
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .border(1.5.dp, TertiaryGreen.copy(alpha = pulseRingAlpha), CircleShape)
                )

                // Main Check Badge
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .shadow(24.dp, CircleShape, spotColor = TertiaryGreen)
                        .clip(CircleShape)
                        .background(TertiaryGreen.copy(alpha = 0.18f))
                        .border(2.dp, TertiaryGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Success",
                        tint = TertiaryGreen,
                        modifier = Modifier.size(46.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Animated Header Texts
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .offset { IntOffset(0, textOffsetY.value.dp.roundToPx()) }
                    .alpha(textAlpha.value)
            ) {
                Text(
                    text = "Payment Successful",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    ),
                    color = OnSurfaceWarm
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Transaction completed successfully",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Animated Order Receipt Card with Clickable preview
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset { IntOffset(0, cardOffsetY.value.dp.roundToPx()) }
                    .alpha(cardAlpha.value)
                    .shadow(16.dp, RoundedCornerShape(24.dp), spotColor = Color.Black.copy(alpha = 0.4f))
                    .clickable { showReceiptModal = true }
                    .testTag("receipt_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Order ID", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                        Text(transaction.id, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = OnSurfaceWarm)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Time", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                        Text(transaction.date, style = MaterialTheme.typography.bodyMedium, color = OnSurfaceWarm)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Payment Method", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                        Text(transaction.paymentMethod, style = MaterialTheme.typography.bodyMedium, color = OnSurfaceWarm)
                    }

                    HorizontalDivider(color = OutlineVariant.copy(alpha = 0.5f), thickness = 1.dp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total Paid", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = OnSurfaceWarm)
                        Text(
                            text = transaction.total.toRupiah(),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = CaramelPrimary
                            )
                        )
                    }

                    if (transaction.change > 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Change Returned", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                            Text(transaction.change.toRupiah(), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = TertiaryGreen)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Animated Action Buttons
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .offset { IntOffset(0, buttonsOffsetY.value.dp.roundToPx()) }
                    .alpha(buttonsAlpha.value)
            ) {
                // Print / Export Receipt Action Button
                OutlinedButton(
                    onClick = { showReceiptModal = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("print_export_receipt_button"),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CaramelPrimary.copy(alpha = 0.6f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = CaramelPrimary.copy(alpha = 0.08f)
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Print,
                            contentDescription = "Print or Export Receipt",
                            tint = CaramelPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            "Print / Export Receipt",
                            color = CaramelPrimary,
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                // New Order Primary Button
                Button(
                    onClick = onNewOrder,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .shadow(12.dp, RoundedCornerShape(16.dp), spotColor = CaramelPrimary.copy(alpha = 0.3f))
                        .testTag("success_new_order_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CaramelPrimary,
                        contentColor = OnPrimary
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.RestaurantMenu, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text("New Order", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                    }
                }

                // View History Secondary Button
                OutlinedButton(
                    onClick = onViewHistory,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("success_view_history_button"),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = SurfaceContainerHighest.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.History, contentDescription = null, tint = SecondaryWarm, modifier = Modifier.size(18.dp))
                        Text("View Activity History", color = OnSurfaceWarm, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold))
                    }
                }
            }
        }
    }

    // Receipt Visualizer Modal / Dialog
    if (showReceiptModal) {
        ReceiptVisualizerDialog(
            transaction = transaction,
            onDismiss = { showReceiptModal = false },
            onShare = {
                shareReceiptText(context, transaction)
            }
        )
    }
}

@Composable
private fun ReceiptVisualizerDialog(
    transaction: Transaction,
    onDismiss: () -> Unit,
    onShare: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isPrinting by remember { mutableStateOf(false) }
    var printSuccess by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = { if (!isPrinting) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.75f))
                .padding(horizontal = 20.dp, vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Bar with Close button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Receipt,
                            contentDescription = null,
                            tint = CaramelPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            "Transaction Receipt",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnSurfaceWarm
                            )
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SurfaceContainer)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = OnSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Authentic Visual Thermal Receipt Canvas
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(16.dp, RoundedCornerShape(16.dp))
                        .testTag("thermal_receipt_paper"),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFAF7F2), // Thermal Paper Off-White
                    contentColor = Color(0xFF1E1A16)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Store Branding
                        Text(
                            text = "CASH AND BREW",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp,
                                fontSize = 22.sp
                            ),
                            color = Color(0xFF1A120B)
                        )
                        Text(
                            text = "Specialty Coffee & Bakery",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp
                            ),
                            color = Color(0xFF6B5E52)
                        )
                        Text(
                            text = "Jl. Senopati No. 42, Jakarta Selatan",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp
                            ),
                            color = Color(0xFF8C7E72)
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        DashedDivider()
                        Spacer(modifier = Modifier.height(12.dp))

                        // Transaction Metadata
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "REC: #${transaction.id}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = Color(0xFF1A120B)
                            )
                            Text(
                                transaction.date,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp
                                ),
                                color = Color(0xFF6B5E52)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "CASHIER: ${AuthManager.currentUser.value?.name ?: "Admin Cashier"}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp
                                ),
                                color = Color(0xFF6B5E52)
                            )
                            Text(
                                "POS #01",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp
                                ),
                                color = Color(0xFF6B5E52)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        DashedDivider()
                        Spacer(modifier = Modifier.height(14.dp))

                        // Itemized Table Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "QTY / ITEM",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = Color(0xFF1A120B)
                            )
                            Text(
                                "AMOUNT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = Color(0xFF1A120B)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Items Breakdown
                        if (transaction.items.isNotEmpty()) {
                            transaction.items.forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${item.quantity}x ${item.product.name}",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 13.sp
                                            ),
                                            color = Color(0xFF1A120B)
                                        )
                                        Text(
                                            text = "${item.size} (${item.product.price.toRupiah()})",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 11.sp
                                            ),
                                            color = Color(0xFF8C7E72)
                                        )
                                    }
                                    Text(
                                        text = item.itemTotal.toRupiah(),
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 13.sp
                                        ),
                                        color = Color(0xFF1A120B)
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = transaction.orderSummaryText,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF1A120B)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        DashedDivider()
                        Spacer(modifier = Modifier.height(12.dp))

                        // Subtotal & Financial Breakdown
                        val subtotal = transaction.total / 1.08
                        val tax = transaction.total - subtotal

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Subtotal", style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B5E52))
                            Text(
                                subtotal.toRupiah(),
                                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                                color = Color(0xFF1A120B)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Tax (PB1 8%)", style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B5E52))
                            Text(
                                tax.toRupiah(),
                                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                                color = Color(0xFF1A120B)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "TOTAL",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp
                                ),
                                color = Color(0xFF1A120B)
                            )
                            Text(
                                transaction.total.toRupiah(),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 18.sp
                                ),
                                color = Color(0xFF1A120B)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        DashedDivider()
                        Spacer(modifier = Modifier.height(10.dp))

                        // Payment Details
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Payment (${transaction.paymentMethod})", style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B5E52))
                            Text(
                                transaction.payment.toRupiah(),
                                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                                color = Color(0xFF1A120B)
                            )
                        }

                        if (transaction.change > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Change Returned", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF2E7D32))
                                Text(
                                    transaction.change.toRupiah(),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Barcode & Footer Greeting
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Simulated Barcode
                            BarcodeVisualizer(seed = transaction.id)

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "THANK YOU FOR BREWING WITH US!",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp,
                                    fontSize = 10.sp
                                ),
                                color = Color(0xFF6B5E52)
                            )
                            Text(
                                text = "Follow us @cashandbrew.coffee",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp
                                ),
                                color = Color(0xFF8C7E72)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Modal Actions: Print & Export
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Export / Share Button
                    OutlinedButton(
                        onClick = onShare,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("export_share_button"),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CaramelPrimary),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = SurfaceContainer
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = CaramelPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                "Share / Export",
                                color = CaramelPrimary,
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    // Print Receipt Button
                    Button(
                        onClick = {
                            if (!isPrinting) {
                                isPrinting = true
                                printSuccess = false
                                coroutineScope.launch {
                                    delay(1200) // Simulating thermal print command
                                    isPrinting = false
                                    printSuccess = true
                                    Toast.makeText(context, "Receipt sent to Thermal Printer (80mm)", Toast.LENGTH_SHORT).show()
                                    delay(1500)
                                    printSuccess = false
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("print_thermal_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (printSuccess) TertiaryGreen else CaramelPrimary,
                            contentColor = OnPrimary
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (isPrinting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = OnPrimary,
                                    strokeWidth = 2.dp
                                )
                                Text("Printing...", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                            } else if (printSuccess) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                Text("Printed!", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                            } else {
                                Icon(Icons.Default.LocalPrintshop, contentDescription = null, modifier = Modifier.size(18.dp))
                                Text("Print", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DashedDivider(
    color: Color = Color(0xFFD4C8BC),
    dashLength: Float = 10f,
    gapLength: Float = 6f
) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
    ) {
        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashLength, gapLength), 0f)
        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            pathEffect = pathEffect,
            strokeWidth = 2f
        )
    }
}

@Composable
private fun BarcodeVisualizer(seed: String) {
    Row(
        modifier = Modifier
            .height(36.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val barWeights = listOf(
            2, 1, 3, 1, 2, 4, 1, 3, 2, 1, 4, 2, 1, 3, 1, 2, 3, 1, 4, 2, 1, 3, 2, 1, 3, 1, 2
        )
        barWeights.forEach { width ->
            Box(
                modifier = Modifier
                    .width(width.dp)
                    .height(32.dp)
                    .background(Color(0xFF1A120B))
            )
        }
    }
}

private fun shareReceiptText(context: Context, transaction: Transaction) {
    val itemsSummary = if (transaction.items.isNotEmpty()) {
        transaction.items.joinToString("\n") {
            "  ${it.quantity}x ${it.product.name} (${it.size}) - ${it.itemTotal.toRupiah()}"
        }
    } else {
        "  ${transaction.orderSummaryText}"
    }

    val receiptString = """
        ================================
               CASH AND BREW COFFEE
            Specialty Coffee & Bakery
        ================================
        Receipt: #${transaction.id}
        Date: ${transaction.date}
        Cashier: ${AuthManager.currentUser.value?.name ?: "Admin Cashier"}
        --------------------------------
        ITEMS:
        $itemsSummary
        --------------------------------
        Total: ${transaction.total.toRupiah()}
        Payment (${transaction.paymentMethod}): ${transaction.payment.toRupiah()}
        Change: ${transaction.change.toRupiah()}
        ================================
        Thank you for brewing with us!
        Follow us @cashandbrew.coffee
        ================================
    """.trimIndent()

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, receiptString)
        putExtra(Intent.EXTRA_SUBJECT, "Receipt #${transaction.id} - Cash and Brew")
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, "Export or Share Receipt")
    context.startActivity(shareIntent)
}

