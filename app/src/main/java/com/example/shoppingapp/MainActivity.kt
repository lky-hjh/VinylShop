package com.example.shoppingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.shoppingapp.data.seed.SeedDataProvider
import com.example.shoppingapp.ui.navigation.AppNavHost
import com.example.shoppingapp.ui.navigation.Routes
import com.example.shoppingapp.ui.theme.VinylShopTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var seedDataProvider: SeedDataProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Seed data if first run
        seedDataProvider.seedIfNeeded()

        // Determine start destination
        val prefs = getSharedPreferences("vinylshop_prefs", MODE_PRIVATE)
        val isLoggedIn = prefs.getString("user_id", null) != null
        val startDestination = if (isLoggedIn) Routes.MAIN else Routes.LOGIN

        setContent {
            VinylShopTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    AppNavHost(
                        navController = navController,
                        startDestination = startDestination
                    )
                }
            }
        }
    }
}
