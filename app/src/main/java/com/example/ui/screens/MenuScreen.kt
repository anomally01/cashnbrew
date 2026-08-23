package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.CoffeeMaker
import androidx.compose.material.icons.filled.EmojiFoodBeverage
import androidx.compose.material.icons.filled.Icecream
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
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
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.CartManager
import com.example.data.ProductRepository
import com.example.model.Product
import com.example.ui.components.AppBottomNavBar
import com.example.ui.components.NavTab
import com.example.ui.components.TopAppBarHeader
import com.example.ui.theme.CaramelPrimary
import com.example.ui.theme.CaramelPrimaryContainer
import com.example.ui.theme.OnPrimaryContainer
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.OnSurfaceWarm
import com.example.ui.theme.OutlineVariant
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceDark
import com.example.util.toRupiah
import kotlinx.coroutines.launch

@Composable
fun MenuScreen(
    onNavigateToTab: (NavTab) -> Unit,
    onProductClick: (String) -> Unit
) {
    val cartItems by CartManager.cartItems.collectAsState()
    val cartItemCount = cartItems.sumOf { it.quantity }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val allProducts = ProductRepository.getAllProducts()
    val filteredProducts = allProducts.filter { product ->
        val matchesCategory = when (selectedCategory) {
            "All" -> true
            "Coffee" -> product.category == "Coffee"
            "Non Coffee" -> product.category == "Non Coffee"
            "Food" -> product.category == "Food"
            "Cappuccino" -> product.name.contains("Cappuccino", ignoreCase = true)
            "Latte" -> product.name.contains("Latte", ignoreCase = true)
            "Espresso" -> product.name.contains("Espresso", ignoreCase = true)
            "Cold Brew" -> product.name.contains("Cold Brew", ignoreCase = true)
            else -> product.category.equals(selectedCategory, ignoreCase = true)
        }
        val matchesSearch = searchQuery.isBlank() ||
                product.name.contains(searchQuery, ignoreCase = true) ||
                product.description.contains(searchQuery, ignoreCase = true)

        matchesCategory && matchesSearch
    }

    Scaffold(
        topBar = {
            TopAppBarHeader(
                title = "Menu Catalog",
                subtitle = "Select Items",
                onNotificationClick = { /* no-op */ }
            )
        },
        bottomBar = {
            AppBottomNavBar(
                currentTab = NavTab.MENU,
                cartItemCount = cartItemCount,
                onTabSelected = onNavigateToTab
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = SurfaceDark
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // 1. Search Bar
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                "Search menu...",
                                color = OnSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = OnSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            Box(
                                modifier = Modifier
                                    .padding(end = 4.dp)
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(CaramelPrimaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = "Filter",
                                    tint = OnPrimaryContainer,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        },
                        shape = RoundedCornerShape(50),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SurfaceContainer,
                            unfocusedContainerColor = SurfaceContainer,
                            focusedBorderColor = CaramelPrimaryContainer,
                            unfocusedBorderColor = OutlineVariant.copy(alpha = 0.3f),
                            focusedTextColor = OnSurfaceWarm,
                            unfocusedTextColor = OnSurfaceWarm
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("menu_search_input")
                    )
                }
            }

            // 2. Categories Horizontal Pills
            item {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Text(
                        text = "Categories",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = OnSurfaceWarm,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )

                    val categories = listOf(
                        CategoryPillData("All", Icons.Default.Restaurant),
                        CategoryPillData("Cappuccino", Icons.Default.LocalCafe),
                        CategoryPillData("Latte", Icons.Default.CoffeeMaker),
                        CategoryPillData("Espresso", Icons.Default.EmojiFoodBeverage),
                        CategoryPillData("Cold Brew", Icons.Default.Icecream),
                        CategoryPillData("Non Coffee", Icons.Default.Coffee),
                        CategoryPillData("Food", Icons.Default.Restaurant)
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(categories) { cat ->
                            val isSelected = selectedCategory == cat.name
                            CategoryChip(
                                title = cat.name,
                                icon = cat.icon,
                                selected = isSelected,
                                onClick = { selectedCategory = cat.name }
                            )
                        }
                    }
                }
            }

            // 3. Spacing for Floating Product Cards
            item {
                Spacer(modifier = Modifier.height(28.dp))
            }

            // 4. Products List
            if (filteredProducts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No products found matching \"$searchQuery\"",
                            color = OnSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                items(filteredProducts, key = { it.id }) { product ->
                    ProductCard(
                        product = product,
                        onClick = { onProductClick(product.id) },
                        onAddToCart = {
                            CartManager.addToCart(product, quantity = 1)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Added ${product.name} to cart")
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(44.dp))
                }
            }
        }
    }
}

private data class CategoryPillData(val name: String, val icon: ImageVector)

@Composable
private fun CategoryChip(
    title: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (selected) CaramelPrimaryContainer else SurfaceContainer)
            .border(
                width = 1.dp,
                color = if (selected) Color.Transparent else OutlineVariant,
                shape = RoundedCornerShape(50)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = CaramelPrimary)
            ) { onClick() }
            .padding(horizontal = 18.dp, vertical = 10.dp)
            .testTag("category_chip_$title"),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) OnPrimaryContainer else OnSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = if (selected) OnPrimaryContainer else OnSurfaceVariant
            )
        }
    }
}

@Composable
private fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        // Main Container
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp)
                .clickable { onClick() }
                .testTag("product_card_${product.id}"),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 70.dp, bottom = 20.dp, start = 20.dp, end = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Product Title
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    ),
                    color = OnSurfaceWarm
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Rating & Review Count
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = CaramelPrimary,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "${product.rating} (${product.reviewCount})",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Bottom Row: Volume/Price + Add to Cart Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "Volume ${product.volume}",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = product.price.toRupiah(),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = CaramelPrimary,
                                fontSize = 22.sp
                            )
                        )
                    }

                    // Add Button
                    IconButton(
                        onClick = onAddToCart,
                        modifier = Modifier
                            .size(48.dp)
                            .shadow(8.dp, CircleShape, spotColor = CaramelPrimary)
                            .clip(CircleShape)
                            .background(CaramelPrimaryContainer)
                            .testTag("add_to_cart_${product.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add to Cart",
                            tint = OnPrimaryContainer,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // Floating Round Product Image with 3D drop shadow
        Box(
            modifier = Modifier
                .size(130.dp)
                .offset(y = (-5).dp)
                .shadow(16.dp, CircleShape, spotColor = Color.Black)
                .clip(CircleShape)
                .border(4.dp, SurfaceContainer, CircleShape)
                .background(SurfaceDark)
        ) {
            AsyncImage(
                model = product.image,
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
