package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalPrintshop
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
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
import com.example.ui.theme.SurfaceContainerHigh
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
    var showReceiptPdfModal by remember { mutableStateOf(false) }

    // Coordinated Gentle Scale-In and Fade-In Entrance Animation States
    val iconScale = remember { Animatable(0.4f) }
    val iconAlpha = remember { Animatable(0f) }
    val glowScale = remember { Animatable(0.6f) }
    val glowAlpha = remember { Animatable(0f) }

    val textAlpha = remember { Animatable(0f) }
    val textScale = remember { Animatable(0.88f) }
    val textOffsetY = remember { Animatable(16f) }

    val cardAlpha = remember { Animatable(0f) }
    val cardScale = remember { Animatable(0.92f) }
    val cardOffsetY = remember { Animatable(20f) }

    val buttonsAlpha = remember { Animatable(0f) }
    val buttonsScale = remember { Animatable(0.94f) }
    val buttonsOffsetY = remember { Animatable(20f) }

    // Pulse transition for check badge
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
        // Step 1: Ambient background glow blooms gently
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

        // Step 2: Check icon gently springs & scales in
        launch {
            iconAlpha.animateTo(1f, animationSpec = tween(300, easing = FastOutSlowInEasing))
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

        delay(160)

        // Step 3: Header Title & Subtitle scale-in and fade-in
        launch {
            textAlpha.animateTo(1f, animationSpec = tween(450, easing = FastOutSlowInEasing))
        }
        launch {
            textScale.animateTo(1f, animationSpec = tween(450, easing = FastOutSlowInEasing))
        }
        launch {
            textOffsetY.animateTo(0f, animationSpec = tween(450, easing = FastOutSlowInEasing))
        }

        delay(120)

        // Step 4: Receipt Summary Card scales-in and fades-in
        launch {
            cardAlpha.animateTo(1f, animationSpec = tween(500, easing = FastOutSlowInEasing))
        }
        launch {
            cardScale.animateTo(1f, animationSpec = tween(500, easing = FastOutSlowInEasing))
        }
        launch {
            cardOffsetY.animateTo(0f, animationSpec = tween(500, easing = FastOutSlowInEasing))
        }

        delay(120)

        // Step 5: Action Buttons scale-in and fade-in
        launch {
            buttonsAlpha.animateTo(1f, animationSpec = tween(450, easing = FastOutSlowInEasing))
        }
        launch {
            buttonsScale.animateTo(1f, animationSpec = tween(450, easing = FastOutSlowInEasing))
        }
        launch {
            buttonsOffsetY.animateTo(0f, animationSpec = tween(450, easing = FastOutSlowInEasing))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        // Ambient Radial Glow
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
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            // Success Check Badge
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

            Spacer(modifier = Modifier.height(20.dp))

            // Animated Header Texts
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .offset { IntOffset(0, textOffsetY.value.dp.roundToPx()) }
                    .scale(textScale.value)
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

            Spacer(modifier = Modifier.height(24.dp))

            // Order Summary Card with quick preview link
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset { IntOffset(0, cardOffsetY.value.dp.roundToPx()) }
                    .scale(cardScale.value)
                    .alpha(cardAlpha.value)
                    .shadow(16.dp, RoundedCornerShape(24.dp), spotColor = Color.Black.copy(alpha = 0.3f))
                    .clickable { showReceiptPdfModal = true }
                    .testTag("receipt_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                border = BorderStroke(1.dp, OutlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Order ID", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(transaction.id, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = OnSurfaceWarm)
                            Icon(Icons.Default.Visibility, contentDescription = "Preview PDF", tint = CaramelPrimary, modifier = Modifier.size(16.dp))
                        }
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

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .offset { IntOffset(0, buttonsOffsetY.value.dp.roundToPx()) }
                    .scale(buttonsScale.value)
                    .alpha(buttonsAlpha.value)
            ) {
                // PDF Bill Preview & Print Mockup Button
                Button(
                    onClick = { showReceiptPdfModal = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("preview_print_receipt_button"),
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
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = "PDF Bill Mockup",
                            tint = OnPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            "Preview Bill & Print (PDF)",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        )
                    }
                }

                // New Order Secondary Button
                OutlinedButton(
                    onClick = onNewOrder,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("success_new_order_button"),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, OutlineVariant),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = SurfaceContainer
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.RestaurantMenu, contentDescription = null, tint = CaramelPrimary, modifier = Modifier.size(18.dp))
                        Text("Start New Order", color = OnSurfaceWarm, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold))
                    }
                }

                // View Activity History
                OutlinedButton(
                    onClick = onViewHistory,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("success_view_history_button"),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.History, contentDescription = null, tint = SecondaryWarm, modifier = Modifier.size(18.dp))
                        Text("View Activity History", color = OnSurfaceVariant, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium))
                    }
                }
            }
        }
    }

    // Interactive PDF-Style Bill & Print Mockup Visualizer Modal
    if (showReceiptPdfModal) {
        PdfBillVisualizerDialog(
            transaction = transaction,
            onDismiss = { showReceiptPdfModal = false },
            onCloseOrder = {
                showReceiptPdfModal = false
                onNewOrder()
            }
        )
    }
}

@Composable
private fun PdfBillVisualizerDialog(
    transaction: Transaction,
    onDismiss: () -> Unit,
    onCloseOrder: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var selectedFormatTab by remember { mutableIntStateOf(0) } // 0: PDF Document, 1: Thermal Slip
    var isPrinting by remember { mutableStateOf(false) }
    var printSuccess by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = { if (!isPrinting) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.82f))
                .padding(horizontal = 14.dp, vertical = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // PDF Viewer Top Toolbar
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                    color = Color(0xFF231F1C)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFE53935)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "PDF",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }

                            Column {
                                Text(
                                    "BILL_#${transaction.id}.pdf",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFFF5F2ED)
                                )
                                Text(
                                    "Page 1 of 1 • 100% Zoom • Tax Invoice",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = Color(0xFFA68E74)
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Share Button
                            IconButton(
                                onClick = { shareReceiptText(context, transaction) },
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF332D28))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share",
                                    tint = CaramelPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            // Close Icon
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF332D28))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color(0xFFD4C8BC),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                // Format Switcher Bar (PDF Document vs Thermal Slip)
                TabRow(
                    selectedTabIndex = selectedFormatTab,
                    containerColor = Color(0xFF1E1A17),
                    contentColor = CaramelPrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedFormatTab]),
                            color = CaramelPrimary,
                            height = 2.5.dp
                        )
                    }
                ) {
                    Tab(
                        selected = selectedFormatTab == 0,
                        onClick = { selectedFormatTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(15.dp))
                                Text("A4 Tax Invoice (PDF)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        selectedContentColor = CaramelPrimary,
                        unselectedContentColor = Color(0xFFA68E74)
                    )
                    Tab(
                        selected = selectedFormatTab == 1,
                        onClick = { selectedFormatTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(15.dp))
                                Text("80mm Thermal Receipt", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        selectedContentColor = CaramelPrimary,
                        unselectedContentColor = Color(0xFFA68E74)
                    )
                }

                // Document Render Canvas
                if (selectedFormatTab == 0) {
                    // ==========================================
                    // REALISTIC A4 PDF TAX INVOICE BILL LAYOUT
                    // ==========================================
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(20.dp, RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
                            .border(1.dp, Color(0xFFDDD5CA), RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
                            .testTag("pdf_bill_canvas"),
                        color = Color(0xFFFFFFFF),
                        contentColor = Color(0xFF1F1A15)
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            // Coffee Cup Watermark in background
                            Box(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .alpha(0.04f)
                                    .size(240.dp)
                                    .border(12.dp, Color.Black, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "CASH & BREW",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.Black,
                                    textAlign = TextAlign.Center
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(22.dp)
                            ) {
                                // PDF Header: Logo & Company Information
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column {
                                        Text(
                                            text = "CASH & BREW",
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.Black,
                                                letterSpacing = 1.5.sp,
                                                fontSize = 22.sp
                                            ),
                                            color = Color(0xFF3E2723)
                                        )
                                        Text(
                                            text = "PT. CASH AND BREW INDONESIA",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                            color = Color(0xFF5D4037)
                                        )
                                        Text(
                                            text = "Jl. Senopati Raya No. 42, Kebayoran Baru, Jakarta",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                            color = Color(0xFF757575)
                                        )
                                        Text(
                                            text = "NPWP: 01.345.678.9-012.000 • PB1 Resto",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                            color = Color(0xFF757575)
                                        )
                                    }

                                    // Invoice Badge
                                    Column(horizontalAlignment = Alignment.End) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFFE8F5E9),
                                            border = BorderStroke(1.dp, Color(0xFF81C784))
                                        ) {
                                            Text(
                                                "TAX INVOICE (PAID)",
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 10.sp
                                                ),
                                                color = Color(0xFF2E7D32)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            "INV-CB-${transaction.id}",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            ),
                                            color = Color(0xFF3E2723)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))
                                HorizontalDivider(color = Color(0xFF3E2723), thickness = 1.5.dp)
                                Spacer(modifier = Modifier.height(12.dp))

                                // Bill Metadata Box
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = Color(0xFFF9F6F0),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, Color(0xFFE6DDCE))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                            Text(
                                                "Bill To: Walk-in Customer",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                                color = Color(0xFF2C241D)
                                            )
                                            Text(
                                                "Cashier: ${AuthManager.currentUser.value?.name ?: "Admin Cashier"}",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                color = Color(0xFF6B5E52)
                                            )
                                            Text(
                                                "Station: Terminal #01 (Main Bar)",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                                color = Color(0xFF8C7E72)
                                            )
                                        }

                                        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                            Text(
                                                "Date: ${transaction.date}",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                color = Color(0xFF2C241D)
                                            )
                                            Text(
                                                "Payment: ${transaction.paymentMethod}",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                                color = Color(0xFF8D5B2F)
                                            )
                                            Text(
                                                "Status: Settled & Closed",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, fontSize = 9.sp),
                                                color = Color(0xFF2E7D32)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Itemized Bill Table Header
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = Color(0xFF3E2723),
                                    shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp, vertical = 7.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("ITEM / DESCRIPTION", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp), color = Color.White, modifier = Modifier.weight(2f))
                                        Text("SIZE", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp), color = Color.White, modifier = Modifier.weight(0.8f), textAlign = TextAlign.Center)
                                        Text("QTY", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp), color = Color.White, modifier = Modifier.weight(0.5f), textAlign = TextAlign.Center)
                                        Text("UNIT", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp), color = Color.White, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                                        Text("TOTAL", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp), color = Color.White, modifier = Modifier.weight(1.2f), textAlign = TextAlign.End)
                                    }
                                }

                                // Table Line Items
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, Color(0xFFE0D7CB), RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp))
                                ) {
                                    if (transaction.items.isNotEmpty()) {
                                        transaction.items.forEachIndexed { index, item ->
                                            val rowBg = if (index % 2 == 0) Color(0xFFFFFFFF) else Color(0xFFFAF7F2)
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(rowBg)
                                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(2f)) {
                                                    Text(
                                                        text = item.product.name,
                                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                                                        color = Color(0xFF1E1A16)
                                                    )
                                                    Text(
                                                        text = item.product.category,
                                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                                        color = Color(0xFF8C7E72)
                                                    )
                                                }
                                                Text(
                                                    text = item.size,
                                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                                    color = Color(0xFF5D4037),
                                                    modifier = Modifier.weight(0.8f),
                                                    textAlign = TextAlign.Center
                                                )
                                                Text(
                                                    text = "${item.quantity}",
                                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                                                    color = Color(0xFF1E1A16),
                                                    modifier = Modifier.weight(0.5f),
                                                    textAlign = TextAlign.Center
                                                )
                                                Text(
                                                    text = item.product.price.toRupiah(),
                                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                                    color = Color(0xFF6B5E52),
                                                    modifier = Modifier.weight(1f),
                                                    textAlign = TextAlign.End
                                                )
                                                Text(
                                                    text = item.itemTotal.toRupiah(),
                                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, fontSize = 11.sp),
                                                    color = Color(0xFF1E1A16),
                                                    modifier = Modifier.weight(1.2f),
                                                    textAlign = TextAlign.End
                                                )
                                            }
                                            if (index < transaction.items.size - 1) {
                                                HorizontalDivider(color = Color(0xFFEBE3D7), thickness = 0.8.dp)
                                            }
                                        }
                                    } else {
                                        Text(
                                            text = transaction.orderSummaryText,
                                            modifier = Modifier.padding(12.dp),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF1E1A16)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Totals & Accounting Section
                                val subtotal = transaction.total / 1.08
                                val tax = transaction.total - subtotal

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    // Left: Authorized Cashier Signature Box & QR
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.Start
                                    ) {
                                        Text(
                                            "Payment Verification",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                            color = Color(0xFF757575)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        BarcodeVisualizer(seed = transaction.id)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            "E-Signature: [VERIFIED POS #${transaction.id.take(4)}]",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 8.sp,
                                                fontFamily = FontFamily.Monospace
                                            ),
                                            color = Color(0xFF2E7D32)
                                        )
                                    }

                                    // Right: Calculations Card
                                    Surface(
                                        modifier = Modifier.width(180.dp),
                                        color = Color(0xFFF9F6F0),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, Color(0xFFE0D7CB))
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(10.dp),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text("Subtotal", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = Color(0xFF6B5E52))
                                                Text(subtotal.toRupiah(), style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp), color = Color(0xFF1E1A16))
                                            }

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text("Tax (PB1 8%)", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = Color(0xFF6B5E52))
                                                Text(tax.toRupiah(), style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp), color = Color(0xFF1E1A16))
                                            }

                                            HorizontalDivider(color = Color(0xFFD4C8BC), thickness = 1.dp, modifier = Modifier.padding(vertical = 2.dp))

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("TOTAL BILL", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black, fontSize = 11.sp), color = Color(0xFF1E1A16))
                                                Text(
                                                    transaction.total.toRupiah(),
                                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, fontSize = 13.sp),
                                                    color = Color(0xFF8D5B2F)
                                                )
                                            }

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text("Paid (${transaction.paymentMethod})", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = Color(0xFF757575))
                                                Text(transaction.payment.toRupiah(), style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 9.sp), color = Color(0xFF1E1A16))
                                            }

                                            if (transaction.change > 0) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text("Change", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp), color = Color(0xFF2E7D32))
                                                    Text(transaction.change.toRupiah(), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, fontSize = 9.sp), color = Color(0xFF2E7D32))
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))
                                HorizontalDivider(color = Color(0xFFE6DDCE), thickness = 1.dp)
                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "This is a computer-generated tax invoice. No signature required. Thank you for your patronage.",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp, textAlign = TextAlign.Center),
                                    color = Color(0xFF9E9E9E),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                } else {
                    // ==========================================
                    // 80MM THERMAL SLIP LAYOUT
                    // ==========================================
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(20.dp, RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
                            .testTag("thermal_receipt_paper"),
                        shape = RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp),
                        color = Color(0xFFFAF7F2),
                        contentColor = Color(0xFF1E1A16)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "CASH AND BREW",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 2.sp,
                                    fontSize = 20.sp
                                ),
                                color = Color(0xFF1A120B)
                            )
                            Text(
                                text = "Specialty Coffee & Bakery",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium, fontSize = 11.sp),
                                color = Color(0xFF6B5E52)
                            )
                            Text(
                                text = "Jl. Senopati No. 42, Jakarta Selatan",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = Color(0xFF8C7E72)
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            DashedDivider()
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("REC: #${transaction.id}", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp), color = Color(0xFF1A120B))
                                Text(transaction.date, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = Color(0xFF6B5E52))
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("CASHIER: ${AuthManager.currentUser.value?.name ?: "Admin Cashier"}", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = Color(0xFF6B5E52))
                                Text("POS #01", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = Color(0xFF6B5E52))
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            DashedDivider()
                            Spacer(modifier = Modifier.height(12.dp))

                            // Table Items
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
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 12.sp),
                                                color = Color(0xFF1A120B)
                                            )
                                            Text(
                                                text = "${item.size} (${item.product.price.toRupiah()})",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                color = Color(0xFF8C7E72)
                                            )
                                        }
                                        Text(
                                            text = item.itemTotal.toRupiah(),
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, fontSize = 12.sp),
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

                            Spacer(modifier = Modifier.height(10.dp))
                            DashedDivider()
                            Spacer(modifier = Modifier.height(10.dp))

                            val subtotal = transaction.total / 1.08
                            val tax = transaction.total - subtotal

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Subtotal", style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B5E52))
                                Text(subtotal.toRupiah(), style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace), color = Color(0xFF1A120B))
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Tax (PB1 8%)", style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B5E52))
                                Text(tax.toRupiah(), style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace), color = Color(0xFF1A120B))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("TOTAL", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, fontSize = 15.sp), color = Color(0xFF1A120B))
                                Text(transaction.total.toRupiah(), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, fontSize = 17.sp), color = Color(0xFF1A120B))
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            DashedDivider()
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Payment (${transaction.paymentMethod})", style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B5E52))
                                Text(transaction.payment.toRupiah(), style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace), color = Color(0xFF1A120B))
                            }

                            if (transaction.change > 0) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Change Returned", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF2E7D32))
                                    Text(transaction.change.toRupiah(), style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace), color = Color(0xFF2E7D32))
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            BarcodeVisualizer(seed = transaction.id)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "THANK YOU FOR BREWING WITH US!",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp, fontSize = 9.sp),
                                color = Color(0xFF6B5E52)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom Action Bar: Print, Export & Close Order
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xFF1E1A17),
                    border = BorderStroke(1.dp, Color(0xFF332D28))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Thermal / PDF Print Button
                            Button(
                                onClick = {
                                    if (!isPrinting) {
                                        isPrinting = true
                                        printSuccess = false
                                        coroutineScope.launch {
                                            delay(1200) // Simulating printer spooling
                                            isPrinting = false
                                            printSuccess = true
                                            val formatName = if (selectedFormatTab == 0) "A4 PDF Invoice" else "80mm Thermal Receipt"
                                            Toast.makeText(context, "Spooling $formatName to POS Printer...", Toast.LENGTH_SHORT).show()
                                            delay(1500)
                                            printSuccess = false
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("modal_print_button"),
                                shape = RoundedCornerShape(12.dp),
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
                                            modifier = Modifier.size(16.dp),
                                            color = OnPrimary,
                                            strokeWidth = 2.dp
                                        )
                                        Text("Printing...", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                                    } else if (printSuccess) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Text("Sent to Printer!", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                                    } else {
                                        Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Text("Print Bill", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                                    }
                                }
                            }

                            // Share / Export File Button
                            OutlinedButton(
                                onClick = { shareReceiptText(context, transaction) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("modal_export_button"),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, CaramelPrimary),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color(0xFF28221D)
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = null, tint = CaramelPrimary, modifier = Modifier.size(18.dp))
                                    Text("Export PDF", color = CaramelPrimary, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                                }
                            }
                        }

                        // Close & Complete Order Action Button
                        Button(
                            onClick = onCloseOrder,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("close_and_complete_order_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2E7D32),
                                contentColor = Color.White
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                                Text("Close & Complete Order", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
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
            .height(28.dp)
            .padding(horizontal = 4.dp),
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
                    .height(24.dp)
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
        ========================================
                 CASH AND BREW COFFEE
               Specialty Coffee & Bakery
        ========================================
        TAX INVOICE: #INV-CB-${transaction.id}
        Date: ${transaction.date}
        Cashier: ${AuthManager.currentUser.value?.name ?: "Admin Cashier"}
        Terminal: POS #01 (Main Bar)
        ----------------------------------------
        ITEMS:
        $itemsSummary
        ----------------------------------------
        Total: ${transaction.total.toRupiah()}
        Payment (${transaction.paymentMethod}): ${transaction.payment.toRupiah()}
        Change: ${transaction.change.toRupiah()}
        ========================================
        Thank you for brewing with us!
        Follow us @cashandbrew.coffee
        ========================================
    """.trimIndent()

    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, receiptString)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, "Export Cash & Brew Invoice (PDF)")
    context.startActivity(shareIntent)
}
