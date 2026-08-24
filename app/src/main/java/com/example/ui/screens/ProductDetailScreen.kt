package com.example.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StickyNote2
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.CartManager
import com.example.data.ProductRepository
import com.example.ui.theme.CaramelPrimary
import com.example.ui.theme.CaramelPrimaryContainer
import com.example.ui.theme.OnPrimaryContainer
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.OnSurfaceWarm
import com.example.ui.theme.OutlineVariant
import com.example.ui.theme.OutlineWarm
import com.example.ui.theme.SecondaryContainer
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceDark
import com.example.util.toRupiah
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    onBackClick: () -> Unit,
    onProceedToCart: () -> Unit,
) {
    val product = ProductRepository.getProductById(productId) ?: ProductRepository.getAllProducts().first()
    var selectedSize by remember { mutableStateOf(product.availableSizes.firstOrNull() ?: "Medium") }
    var quantity by remember { mutableIntStateOf(1) }
    var baristaNotes by remember { mutableStateOf("") }
    var isFavorite by remember { mutableStateOf(value = false) }
    var isExpandedDescription by remember { mutableStateOf(value = false) }

    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Quick barista instruction presets
    val quickInstructions = listOf(
        "Extra Sugar",
        "No Sugar",
        "Less Sugar (50%)",
        "No Ice",
        "Less Ice",
        "Extra Ice",
        "Oat Milk",
        "Almond Milk",
        "Extra Shot (+1)",
        "Decaf",
        "Extra Hot"
    )

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            // Fixed Bottom Action Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = SurfaceContainerLow,
                shadowElevation = 16.dp,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = OutlineVariant.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                        )
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Quantity Stepper Pill
                        Row(
                            modifier = Modifier
                                .height(52.dp)
                                .clip(RoundedCornerShape(50))
                                .background(SurfaceContainer)
                                .border(1.dp, OutlineVariant, RoundedCornerShape(50))
                                .padding(horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            IconButton(
                                onClick = { if (quantity > 1) quantity-- },
                                modifier = Modifier.size(36.dp).testTag("decrease_quantity_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Decrease",
                                    tint = OnSurfaceWarm,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Text(
                                text = quantity.toString(),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = OnSurfaceWarm,
                                modifier = Modifier
                                    .width(28.dp)
                                    .testTag("product_quantity_display"),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )

                            IconButton(
                                onClick = { if (quantity < 99) quantity++ },
                                modifier = Modifier.size(36.dp).testTag("increase_quantity_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Increase",
                                    tint = OnSurfaceWarm,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Add to Cart / Buy Now Button
                        Button(
                            onClick = {
                                CartManager.addToCart(
                                    product = product,
                                    quantity = quantity,
                                    size = selectedSize,
                                    notes = baristaNotes.trim()
                                )
                                coroutineScope.launch {
                                    val noteSuffix = if (baristaNotes.isNotBlank()) " • \"${baristaNotes.trim()}\"" else ""
                                    val result = snackbarHostState.showSnackbar(
                                        message = "Added $quantity ${product.name} ($selectedSize$noteSuffix) to cart",
                                        actionLabel = "View Cart"
                                    )
                                    if (result == androidx.compose.material3.SnackbarResult.ActionPerformed) {
                                        onProceedToCart()
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .shadow(10.dp, RoundedCornerShape(50), spotColor = CaramelPrimary)
                                .testTag("buy_now_button"),
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
                                    imageVector = Icons.Default.ShoppingBag,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Buy now",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }
        },
        containerColor = SurfaceDark
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .verticalScroll(scrollState)
        ) {
            // 1. Hero Curved Section with Coffee Cup
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                SurfaceContainerLow,
                                SurfaceContainer
                            )
                        )
                    )
            ) {
                // Background ambient glow circles
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .align(Alignment.Center)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    CaramelPrimaryContainer.copy(alpha = 0.25f),
                                    SecondaryContainer.copy(alpha = 0.1f),
                                    Color.Transparent
                                )
                            ),
                            CircleShape
                        )
                )

                // Top Navigation Icons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(SurfaceContainer)
                            .border(1.dp, OutlineVariant, CircleShape)
                            .testTag("detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = OnSurfaceWarm
                        )
                    }

                    IconButton(
                        onClick = { isFavorite = !isFavorite },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(SurfaceContainer)
                            .border(1.dp, OutlineVariant, CircleShape)
                            .testTag("detail_favorite_button")
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) CaramelPrimary else OnSurfaceWarm
                        )
                    }
                }

                // Large Hero Coffee Cup Image in Center with Rating
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .align(Alignment.Center)
                        .offset(y = 20.dp)
                ) {
                    // Floating Coffee Cup
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .shadow(24.dp, CircleShape, spotColor = Color.Black)
                            .clip(CircleShape)
                            .border(4.dp, SurfaceContainerLow, CircleShape)
                    ) {
                        AsyncImage(
                            model = product.image,
                            contentDescription = product.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Floating Rating Badge on the left
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .offset(x = (-10).dp, y = 14.dp)
                            .clip(RoundedCornerShape(50))
                            .background(SurfaceContainerHigh.copy(alpha = 0.95f))
                            .border(1.dp, OutlineVariant, RoundedCornerShape(50))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = CaramelPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = product.rating.toString(),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = OnSurfaceWarm
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // 2. Product Information Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                // Title and Price Badge Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp
                            ),
                            color = OnSurfaceWarm
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = product.subtitle.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 2.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = OnSurfaceVariant
                        )
                    }

                    // Price Tag Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceContainer)
                            .border(1.dp, OutlineVariant.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = product.price.toRupiah(),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = CaramelPrimary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 3. About Section
                Text(
                    text = "About",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = OnSurfaceWarm
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                    color = OnSurfaceVariant,
                    maxLines = if (isExpandedDescription) 10 else 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.animateContentSize()
                )
                Text(
                    text = if (isExpandedDescription) "Read less" else "Read more",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = CaramelPrimary
                    ),
                    modifier = Modifier
                        .clickable { isExpandedDescription = !isExpandedDescription }
                        .padding(vertical = 4.dp)
                        .testTag("toggle_read_more")
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 4. Size Selector Section
                Text(
                    text = "Coffee size",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = OnSurfaceWarm
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    product.availableSizes.forEach { sizeOption ->
                        val isSelected = selectedSize == sizeOption
                        val interactionSource = remember { MutableInteractionSource() }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(50))
                                .background(if (isSelected) CaramelPrimaryContainer else SurfaceContainer)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) Color.Transparent else OutlineVariant,
                                    shape = RoundedCornerShape(50)
                                )
                                .then(
                                    if (isSelected) Modifier.shadow(8.dp, RoundedCornerShape(50), spotColor = CaramelPrimary)
                                    else Modifier
                                )
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = ripple(bounded = true, color = CaramelPrimary)
                                ) { selectedSize = sizeOption }
                                .testTag("size_option_$sizeOption"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = sizeOption,
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = if (isSelected) OnPrimaryContainer else OnSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // 5. Barista Note / Special Instructions Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = null,
                            tint = CaramelPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Add Note & Special Instructions",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = OnSurfaceWarm
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = SurfaceContainerHigh,
                        border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "Optional",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.sp
                            ),
                            color = OnSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Attach custom instructions for the barista (e.g., extra sugar, no ice, warm oat milk).",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = OnSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Quick instruction chips for fast barista POS entry
                Text(
                    text = "Quick Presets:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        fontSize = 11.sp
                    ),
                    color = CaramelPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    quickInstructions.forEach { instruction ->
                        val isPresetActive = baristaNotes.contains(instruction, ignoreCase = true)

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isPresetActive) CaramelPrimaryContainer.copy(alpha = 0.85f) else SurfaceContainer,
                            border = BorderStroke(
                                1.dp,
                                if (isPresetActive) CaramelPrimary else OutlineVariant.copy(alpha = 0.7f)
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    if (isPresetActive) {
                                        // Remove the instruction
                                        val parts = baristaNotes
                                            .split(",")
                                            .map { it.trim() }
                                            .filter { !it.equals(instruction, ignoreCase = true) && it.isNotBlank() }
                                        baristaNotes = parts.joinToString(", ")
                                    } else {
                                        // Append the instruction
                                        val trimmed = baristaNotes.trim()
                                        baristaNotes = if (trimmed.isEmpty()) {
                                            instruction
                                        } else {
                                            "$trimmed, $instruction"
                                        }
                                    }
                                }
                                .testTag("quick_note_chip_${instruction.lowercase().replace(" ", "_")}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                if (isPresetActive) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = OnPrimaryContainer,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                                Text(
                                    text = instruction,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isPresetActive) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 12.sp
                                    ),
                                    color = if (isPresetActive) OnPrimaryContainer else OnSurfaceWarm
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Custom Note Outlined Text Field
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                    border = BorderStroke(
                        1.dp,
                        if (baristaNotes.isNotBlank()) CaramelPrimary.copy(alpha = 0.5f) else OutlineVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        OutlinedTextField(
                            value = baristaNotes,
                            onValueChange = { if (it.length <= 150) baristaNotes = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("add_note_field"),
                            placeholder = {
                                Text(
                                    text = "e.g., extra sugar, no ice, extra shot, oat milk...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = OnSurfaceVariant.copy(alpha = 0.6f)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.StickyNote2,
                                    contentDescription = "Note Icon",
                                    tint = if (baristaNotes.isNotBlank()) CaramelPrimary else OnSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            trailingIcon = {
                                if (baristaNotes.isNotBlank()) {
                                    IconButton(
                                        onClick = { baristaNotes = "" },
                                        modifier = Modifier.size(28.dp).testTag("clear_note_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Clear Note",
                                            tint = OnSurfaceVariant,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            },
                            minLines = 2,
                            maxLines = 4,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = SurfaceDark,
                                unfocusedContainerColor = SurfaceDark,
                                focusedBorderColor = CaramelPrimary,
                                unfocusedBorderColor = OutlineVariant,
                                focusedTextColor = OnSurfaceWarm,
                                unfocusedTextColor = OnSurfaceWarm,
                                cursorColor = CaramelPrimary
                            ),
                            shape = RoundedCornerShape(14.dp),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { focusManager.clearFocus() }
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (baristaNotes.isNotBlank()) "Instructions attached to ticket" else "Type or select presets above",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = if (baristaNotes.isNotBlank()) CaramelPrimary else OnSurfaceVariant
                            )

                            Text(
                                text = "${baristaNotes.length}/150",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = OnSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }
}
