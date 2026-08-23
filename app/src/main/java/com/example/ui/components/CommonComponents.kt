package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AuthManager
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.CaramelPrimary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.OnPrimary
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.OnSurfaceWarm
import com.example.ui.theme.OutlineVariant
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.ThemeManager

enum class NavTab {
    DASHBOARD, MENU, CART, HISTORY
}

@Composable
fun ThemeTogglePill(
    modifier: Modifier = Modifier,
) {
    val themeMode by ThemeManager.themeMode.collectAsState()
    val isEspresso = themeMode == AppThemeMode.ESPRESSO

    val targetBgColor = if (isEspresso) SurfaceContainer else SurfaceContainerHigh
    val animatedBg by animateColorAsState(targetValue = targetBgColor, animationSpec = tween(300), label = "theme_bg")
    val animatedBorder by animateColorAsState(
        targetValue = if (isEspresso) CaramelPrimary.copy(alpha = 0.5f) else OutlineVariant,
        animationSpec = tween(300),
        label = "theme_border"
    )

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(animatedBg)
            .border(1.dp, animatedBorder, RoundedCornerShape(20.dp))
            .clickable { ThemeManager.toggleTheme() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag("theme_toggle_button"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(if (isEspresso) CaramelPrimary else Color(0xFFD99B65)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isEspresso) Icons.Default.Coffee else Icons.Default.LightMode,
                contentDescription = "Current Theme: ${themeMode.displayName}",
                tint = if (isEspresso) Color(0xFF1A120B) else Color.White,
                modifier = Modifier.size(13.dp)
            )
        }

        Text(
            text = themeMode.displayName,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            ),
            color = OnSurfaceWarm
        )
    }
}

@Composable
fun TopAppBarHeader(
    modifier: Modifier = Modifier,
    title: String = "Admin Cashier",
    subtitle: String = "Welcome back,",
    onNotificationClick: () -> Unit = {}
) {
    val currentUser by AuthManager.currentUser.collectAsState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceDark)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f, fill = false)) {
            Text(
                text = subtitle.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp
                ),
                color = OnSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = if ((title == "Open Shift") || (title == "Cash and Brew")) (currentUser?.name ?: "Admin Cashier") else title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = OnSurfaceWarm
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Theme Toggle Pill (Espresso <-> Cream)
            ThemeTogglePill()

            // Notification Button
            IconButton(
                onClick = onNotificationClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceContainer)
                    .border(1.dp, OutlineVariant, RoundedCornerShape(14.dp))
                    .testTag("notification_button")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = OnSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    // Notification indicator dot
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .align(Alignment.TopEnd)
                            .background(CaramelPrimary, CircleShape)
                    )
                }
            }

            // Staff avatar badge
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceContainer)
                    .border(1.dp, OutlineVariant, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(CaramelPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (currentUser?.name?.take(1) ?: "A").uppercase(),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnPrimary
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun AppBottomNavBar(
    currentTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier,
    cartItemCount: Int = 0
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = SurfaceContainerLow,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = OutlineVariant)
                .background(SurfaceContainerLow)
                .padding(horizontal = 8.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavBarItem(
                    title = "Home",
                    selected = currentTab == NavTab.DASHBOARD,
                    selectedIcon = Icons.Filled.Dashboard,
                    unselectedIcon = Icons.Outlined.Dashboard,
                    testTag = "nav_dashboard",
                ) { onTabSelected(NavTab.DASHBOARD) }

                NavBarItem(
                    title = "Menu",
                    selected = currentTab == NavTab.MENU,
                    selectedIcon = Icons.Filled.RestaurantMenu,
                    unselectedIcon = Icons.Outlined.RestaurantMenu,
                    testTag = "nav_menu",
                ) { onTabSelected(NavTab.MENU) }

                NavBarItem(
                    title = "Cart",
                    selected = currentTab == NavTab.CART,
                    selectedIcon = Icons.Filled.ShoppingCart,
                    unselectedIcon = Icons.Outlined.ShoppingCart,
                    badgeCount = cartItemCount,
                    testTag = "nav_cart",
                ) { onTabSelected(NavTab.CART) }

                NavBarItem(
                    title = "Activity",
                    selected = currentTab == NavTab.HISTORY,
                    selectedIcon = Icons.Filled.History,
                    unselectedIcon = Icons.Outlined.History,
                    testTag = "nav_history",
                ) { onTabSelected(NavTab.HISTORY) }
            }
        }
    }
}

@Composable
private fun NavBarItem(
    title: String,
    selected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    badgeCount: Int = 0,
    testTag: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .testTag(testTag)
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = CaramelPrimary)
            ) { onClick() }
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            BadgedBox(
                badge = {
                    if (badgeCount > 0) {
                        Badge(
                            containerColor = ErrorRed,
                            contentColor = Color.White
                        ) {
                            Text(
                                text = badgeCount.toString(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = if (selected) selectedIcon else unselectedIcon,
                    contentDescription = title,
                    tint = if (selected) CaramelPrimary else OnSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 11.sp
                ),
                color = if (selected) CaramelPrimary else OnSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}
