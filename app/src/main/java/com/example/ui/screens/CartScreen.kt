package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RemoveShoppingCart
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.SwipeLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.CartManager
import com.example.model.CartItem
import com.example.ui.components.AppBottomNavBar
import com.example.ui.components.NavTab
import com.example.ui.components.TopAppBarHeader
import com.example.ui.theme.CaramelPrimary
import com.example.ui.theme.CaramelPrimaryContainer
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.OnPrimaryContainer
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.OnSurfaceWarm
import com.example.ui.theme.OutlineVariant
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TertiaryGreen
import com.example.util.toRupiah

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onNavigateToTab: (NavTab) -> Unit,
    onProceedToCheckout: () -> Unit
) {
    val cartItems by CartManager.cartItems.collectAsState()
    val cartItemCount = cartItems.sumOf { it.quantity }
    var gratuityAdded by remember { mutableStateOf(false) }

    val subtotal = cartItems.sumOf { it.itemTotal }
    val tax = subtotal * 0.08
    val gratuity = if (gratuityAdded) 5000.0 else 0.0
    val total = subtotal + tax + gratuity

    Scaffold(
        topBar = {
            TopAppBarHeader(
                title = "Active Order",
                subtitle = "Review & Checkout",
                onNotificationClick = { /* no-op */ }
            )
        },
        bottomBar = {
            AppBottomNavBar(
                currentTab = NavTab.CART,
                cartItemCount = cartItemCount,
                onTabSelected = onNavigateToTab
            )
        },
        containerColor = SurfaceDark
    ) { innerPadding ->
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(SurfaceContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.RemoveShoppingCart,
                            contentDescription = null,
                            tint = OnSurfaceVariant,
                            modifier = Modifier.size(44.dp)
                        )
                    }

                    Text(
                        text = "Your Cart is Empty",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = OnSurfaceWarm
                    )
                    Text(
                        text = "Add espresso drinks, cold brews, and fresh pastries from the menu catalog.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { onNavigateToTab(NavTab.MENU) },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CaramelPrimaryContainer,
                            contentColor = OnPrimaryContainer
                        ),
                        modifier = Modifier.testTag("empty_cart_browse_menu")
                    ) {
                        Icon(Icons.Default.RestaurantMenu, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Browse Menu")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Your Order",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp
                                ),
                                color = OnSurfaceWarm
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SwipeLeft,
                                    contentDescription = null,
                                    tint = OnSurfaceVariant,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "Swipe item to remove",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    color = OnSurfaceVariant
                                )
                            }
                        }

                        Text(
                            text = "Clear All",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = ErrorRed,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier
                                .clickable { CartManager.clearCart() }
                                .padding(vertical = 4.dp, horizontal = 8.dp)
                                .testTag("clear_cart_button")
                        )
                    }
                }

                // Cart Items List with Swipe-To-Delete
                items(cartItems, key = { "${it.product.id}_${it.size}" }) { cartItem ->
                    SwipeableCartItemRow(
                        cartItem = cartItem,
                        onIncrease = { CartManager.increaseQuantity(cartItem.product.id, cartItem.size) },
                        onDecrease = { CartManager.decreaseQuantity(cartItem.product.id, cartItem.size) },
                        onRemove = { CartManager.removeItem(cartItem.product.id, cartItem.size) }
                    )
                }

                // Order Summary Card
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("order_summary_card"),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "ORDER SUMMARY",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    letterSpacing = 1.5.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = OnSurfaceVariant
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Subtotal", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceWarm)
                                Text(subtotal.toRupiah(), style = MaterialTheme.typography.bodyMedium, color = OnSurfaceWarm)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Tax (8%)", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceWarm)
                                Text(tax.toRupiah(), style = MaterialTheme.typography.bodyMedium, color = OnSurfaceWarm)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Gratuity (Optional)", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceWarm)
                                Text(
                                    text = if (gratuityAdded) "Rp 5.000 (Added)" else "Add",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = if (gratuityAdded) TertiaryGreen else CaramelPrimary,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    modifier = Modifier
                                        .clickable { gratuityAdded = !gratuityAdded }
                                        .padding(4.dp)
                                        .testTag("toggle_gratuity")
                                )
                            }

                            HorizontalDivider(
                                color = OutlineVariant.copy(alpha = 0.5f),
                                thickness = 1.dp,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Text(
                                    text = "Total",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = OnSurfaceWarm
                                )
                                Text(
                                    text = total.toRupiah(),
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = CaramelPrimary,
                                        fontSize = 24.sp
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Proceed to Payment Button
                            Button(
                                onClick = onProceedToCheckout,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .shadow(10.dp, RoundedCornerShape(16.dp), spotColor = CaramelPrimary)
                                    .testTag("proceed_to_payment_button"),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CaramelPrimary,
                                    contentColor = SurfaceDark
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Proceed to Payment",
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableCartItemRow(
    cartItem: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart || dismissValue == SwipeToDismissBoxValue.StartToEnd) {
                onRemove()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            val isSwipingEndToStart = dismissState.targetValue == SwipeToDismissBoxValue.EndToStart
            val isSwipingStartToEnd = dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd
            val isSwiping = isSwipingEndToStart || isSwipingStartToEnd

            val backgroundColor by animateColorAsState(
                targetValue = if (isSwiping) ErrorRed.copy(alpha = 0.95f) else Color(0xFF3B1A1A),
                label = "swipe_bg_color"
            )

            val iconScale by animateFloatAsState(
                targetValue = if (isSwiping) 1.25f else 1.0f,
                label = "swipe_icon_scale"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
                    .background(backgroundColor)
                    .padding(horizontal = 22.dp),
                contentAlignment = if (isSwipingStartToEnd) Alignment.CenterStart else Alignment.CenterEnd
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.scale(iconScale)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete item",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Remove",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        },
        content = {
            CartItemRow(
                cartItem = cartItem,
                onIncrease = onIncrease,
                onDecrease = onDecrease,
                onRemove = onRemove
            )
        }
    )
}

@Composable
private fun CartItemRow(
    cartItem: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("cart_item_${cartItem.product.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Product Thumbnail
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(SurfaceDark)
                        .shadow(4.dp, CircleShape)
                ) {
                    AsyncImage(
                        model = cartItem.product.image,
                        contentDescription = cartItem.product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Column {
                    Text(
                        text = cartItem.product.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = OnSurfaceWarm
                    )
                    Text(
                        text = "${cartItem.size}${if (cartItem.notes.isNotEmpty()) ", " + cartItem.notes else ""}",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = cartItem.itemTotal.toRupiah(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = CaramelPrimary
                        )
                    )
                }
            }

            // Stepper Pill
            Row(
                modifier = Modifier
                    .height(42.dp)
                    .clip(RoundedCornerShape(50))
                    .background(SurfaceDark)
                    .border(1.dp, OutlineVariant, RoundedCornerShape(50))
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (cartItem.quantity == 1) {
                            onRemove()
                        } else {
                            onDecrease()
                        }
                    },
                    modifier = Modifier.size(32.dp).testTag("decrease_cart_item_${cartItem.product.id}")
                ) {
                    Icon(
                        imageVector = if (cartItem.quantity == 1) Icons.Default.DeleteOutline else Icons.Default.Remove,
                        contentDescription = "Decrease",
                        tint = if (cartItem.quantity == 1) ErrorRed else OnSurfaceWarm,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = cartItem.quantity.toString(),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = OnSurfaceWarm,
                    modifier = Modifier.width(24.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                IconButton(
                    onClick = onIncrease,
                    modifier = Modifier.size(32.dp).testTag("increase_cart_item_${cartItem.product.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase",
                        tint = OnSurfaceWarm,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

