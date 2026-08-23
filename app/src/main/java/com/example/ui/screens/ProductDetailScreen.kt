package com.example.ui.screens

import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
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

@Composable
fun ProductDetailScreen(
    productId: String,
    onBackClick: () -> Unit,
    onProceedToCart: () -> Unit,
) {
    val product = ProductRepository.getProductById(productId) ?: ProductRepository.getAllProducts().first()
    var selectedSize by remember { mutableStateOf(product.availableSizes.firstOrNull() ?: "Medium") }
    var quantity by remember { mutableIntStateOf(1) }
    var isFavorite by remember { mutableStateOf(value = false) }
    var isExpandedDescription by remember { mutableStateOf(value = false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    Scaffold(
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
                        .padding(horizontal = 20.dp, vertical = 16.dp)
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
                                    size = selectedSize
                                )
                                coroutineScope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message = "Added $quantity ${product.name} ($selectedSize) to cart",
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
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            // 1. Hero Curved Section with Coffee Cup
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
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
                        .padding(horizontal = 20.dp, vertical = 16.dp)
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

                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }
}
