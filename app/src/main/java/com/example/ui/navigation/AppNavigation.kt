package com.example.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.model.Transaction
import com.example.ui.components.NavTab
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CheckoutScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MenuScreen
import com.example.ui.screens.PaymentSuccessScreen
import com.example.ui.screens.ProductDetailScreen

object NavDestinations {
    const val LOGIN = "login"
    const val DASHBOARD = "dashboard"
    const val MENU = "menu"
    const val PRODUCT_DETAIL = "product_detail/{productId}"
    const val CART = "cart"
    const val CHECKOUT = "checkout"
    const val PAYMENT_SUCCESS = "payment_success"
    const val HISTORY = "history"

    fun productDetail(productId: String) = "product_detail/$productId"
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    var lastCompletedTransaction by remember { mutableStateOf<Transaction?>(null) }

    val handleTabNavigation: (NavTab) -> Unit = { tab ->
        when (tab) {
            NavTab.DASHBOARD -> navController.navigate(NavDestinations.DASHBOARD) {
                popUpTo(NavDestinations.DASHBOARD) { inclusive = false }
                launchSingleTop = true
            }
            NavTab.MENU -> navController.navigate(NavDestinations.MENU) {
                popUpTo(NavDestinations.DASHBOARD) { inclusive = false }
                launchSingleTop = true
            }
            NavTab.CART -> navController.navigate(NavDestinations.CART) {
                popUpTo(NavDestinations.DASHBOARD) { inclusive = false }
                launchSingleTop = true
            }
            NavTab.HISTORY -> navController.navigate(NavDestinations.HISTORY) {
                popUpTo(NavDestinations.DASHBOARD) { inclusive = false }
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = NavDestinations.LOGIN,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() },
        popEnterTransition = { fadeIn() },
        popExitTransition = { fadeOut() }
    ) {
        composable(NavDestinations.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(NavDestinations.DASHBOARD) {
                        popUpTo(NavDestinations.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(NavDestinations.DASHBOARD) {
            DashboardScreen(
                onNavigateToTab = handleTabNavigation,
                onLogout = {
                    navController.navigate(NavDestinations.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(NavDestinations.MENU) {
            MenuScreen(
                onNavigateToTab = handleTabNavigation,
                onProductClick = { productId ->
                    navController.navigate(NavDestinations.productDetail(productId))
                }
            )
        }

        composable(
            route = NavDestinations.PRODUCT_DETAIL,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            ProductDetailScreen(
                productId = productId,
                onBackClick = { navController.popBackStack() },
                onProceedToCart = {
                    navController.navigate(NavDestinations.CART) {
                        popUpTo(NavDestinations.MENU) { inclusive = false }
                    }
                }
            )
        }

        composable(NavDestinations.CART) {
            CartScreen(
                onNavigateToTab = handleTabNavigation,
                onProceedToCheckout = {
                    navController.navigate(NavDestinations.CHECKOUT)
                }
            )
        }

        composable(NavDestinations.CHECKOUT) {
            CheckoutScreen(
                onBackClick = { navController.popBackStack() },
                onPaymentSuccess = { transaction ->
                    lastCompletedTransaction = transaction
                    navController.navigate(NavDestinations.PAYMENT_SUCCESS) {
                        popUpTo(NavDestinations.CART) { inclusive = true }
                    }
                }
            )
        }

        composable(NavDestinations.PAYMENT_SUCCESS) {
            val fallbackTx = Transaction(
                id = "#ORD-8925",
                items = emptyList(),
                total = 58000.0,
                payment = 60000.0,
                change = 2000.0,
                date = "Today, 10:45 AM",
                status = "Completed",
                paymentMethod = "Cash"
            )
            PaymentSuccessScreen(
                transaction = lastCompletedTransaction ?: fallbackTx,
                onNewOrder = {
                    navController.navigate(NavDestinations.MENU) {
                        popUpTo(NavDestinations.DASHBOARD) { inclusive = false }
                    }
                },
                onViewHistory = {
                    navController.navigate(NavDestinations.HISTORY) {
                        popUpTo(NavDestinations.DASHBOARD) { inclusive = false }
                    }
                }
            )
        }

        composable(NavDestinations.HISTORY) {
            HistoryScreen(
                onNavigateToTab = handleTabNavigation
            )
        }
    }
}
