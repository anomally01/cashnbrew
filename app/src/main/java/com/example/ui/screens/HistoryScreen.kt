package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CartManager
import com.example.data.TransactionManager
import com.example.model.Transaction
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
import com.example.ui.theme.SurfaceContainerHighest
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TertiaryContainerGreen
import com.example.ui.theme.TertiaryGreen
import com.example.util.toRupiah

@Composable
fun HistoryScreen(
    onNavigateToTab: (NavTab) -> Unit,
) {
    val transactions by TransactionManager.transactions.collectAsState()
    val cartItems by CartManager.cartItems.collectAsState()
    val cartItemCount = cartItems.sumOf { it.quantity }

    var searchQuery by remember { mutableStateOf("") }

    val filteredTransactions = transactions.filter { tx ->
        searchQuery.isBlank() ||
                tx.id.contains(searchQuery, ignoreCase = true) ||
                tx.orderSummaryText.contains(searchQuery, ignoreCase = true) ||
                tx.paymentMethod.contains(searchQuery, ignoreCase = true)
    }

    val todayTransactions = filteredTransactions.filter { it.date.startsWith("Today") }
    val olderTransactions = filteredTransactions.filter { !it.date.startsWith("Today") }

    Scaffold(
        topBar = {
            TopAppBarHeader(
                title = "Cash and Brew",
                subtitle = "Transaction Logs"
            ) { /* no-op */ }
        },
        bottomBar = {
            AppBottomNavBar(
                currentTab = NavTab.HISTORY,
                cartItemCount = cartItemCount,
                onTabSelected = onNavigateToTab
            )
        },
        containerColor = SurfaceDark
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Search Bar
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
                                "Search orders...",
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("history_search_input")
                    )
                }
            }

            if (filteredTransactions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(SurfaceContainerHighest),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = null,
                                    tint = OnSurfaceVariant,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            Text(
                                text = "No transactions found",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = OnSurfaceWarm
                            )
                        }
                    }
                }
            } else {
                // Today's Group
                if (todayTransactions.isNotEmpty()) {
                    item {
                        Text(
                            text = "TODAY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 1.5.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = OnSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                        )
                    }

                    items(todayTransactions, key = { it.id }) { tx ->
                        TransactionHistoryCard(transaction = tx)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                // Older / Yesterday's Group
                if (olderTransactions.isNotEmpty()) {
                    item {
                        Text(
                            text = "PREVIOUS TRANSACTIONS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 1.5.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = OnSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                        )
                    }

                    items(olderTransactions, key = { it.id }) { tx ->
                        TransactionHistoryCard(transaction = tx)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionHistoryCard(transaction: Transaction) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .testTag("history_card_${transaction.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Order ${transaction.id}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = OnSurfaceWarm
                )

                // Status Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (transaction.status == "Completed") TertiaryContainerGreen.copy(alpha = 0.2f)
                            else ErrorRed.copy(alpha = 0.2f)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = transaction.status,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (transaction.status == "Completed") TertiaryGreen else ErrorRed
                    )
                }
            }

            Text(
                text = transaction.orderSummaryText,
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = if (transaction.paymentMethod == "Cash") Icons.Default.Payments else Icons.Default.CreditCard,
                        contentDescription = null,
                        tint = OnSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${transaction.paymentMethod} • ${transaction.date}",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant
                    )
                }

                Text(
                    text = transaction.total.toRupiah(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = CaramelPrimary
                    )
                )
            }
        }
    }
}
