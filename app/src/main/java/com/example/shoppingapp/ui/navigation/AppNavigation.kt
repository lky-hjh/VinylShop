package com.example.shoppingapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val MAIN = "main"
    const val PRODUCT_DETAIL = "product_detail/{productId}"
    const val CHECKOUT = "checkout"
    const val ORDER_DETAIL = "order_detail/{orderId}"

    fun productDetail(productId: String) = "product_detail/$productId"
    fun orderDetail(orderId: String) = "order_detail/$orderId"
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.LOGIN) {
            com.example.shoppingapp.ui.profile.LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

        composable(Routes.REGISTER) {
            com.example.shoppingapp.ui.profile.RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.MAIN) {
            MainScreen(
                onProductClick = { productId ->
                    navController.navigate(Routes.productDetail(productId))
                },
                onCheckoutClick = {
                    navController.navigate(Routes.CHECKOUT)
                },
                onOrderClick = { orderId ->
                    navController.navigate(Routes.orderDetail(orderId))
                },
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Routes.PRODUCT_DETAIL,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: return@composable
            com.example.shoppingapp.ui.detail.ProductDetailScreen(
                productId = productId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.CHECKOUT) {
            com.example.shoppingapp.ui.checkout.CheckoutScreen(
                onBackClick = { navController.popBackStack() },
                onOrderSuccess = { orderId ->
                    navController.navigate(Routes.orderDetail(orderId)) {
                        popUpTo(Routes.MAIN)
                    }
                }
            )
        }

        composable(
            route = Routes.ORDER_DETAIL,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: return@composable
            com.example.shoppingapp.ui.order.OrderDetailScreen(
                orderId = orderId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
