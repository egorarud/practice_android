package com.example.practice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.practice.ui.BottomBar
import com.example.practice.ui.detail.DetailScreen
import com.example.practice.ui.favorites.FavoritesScreen
import com.example.practice.ui.filter.FilterScreen
import com.example.practice.ui.list.ListScreen
import com.example.practice.ui.theme.PracticeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PracticeTheme {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { BottomBar(navController) }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // Tab 1: home stack (list + detail)
                        composable("home") {
                            ListScreen(
                                onItemClick = { itemId ->
                                    navController.navigate("home/detail/$itemId")
                                },
                                onFiltersClick = {
                                    navController.navigate("home/filters")
                                }
                            )
                        }
                        composable("home/filters") {
                            FilterScreen(
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable(
                            route = "home/detail/{itemId}",
                            arguments = listOf(navArgument("itemId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("itemId") ?: ""
                            DetailScreen(itemId = id)
                        }

                        // Tab 2: favorites
                        composable("favorites") {
                            FavoritesScreen(
                                onItemClick = { itemId ->
                                    navController.navigate("favorites/detail/$itemId")
                                }
                            )
                        }
                        composable(
                            route = "favorites/detail/{itemId}",
                            arguments = listOf(navArgument("itemId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("itemId") ?: ""
                            DetailScreen(itemId = id)
                        }

                        // Tab 3: settings placeholder
                        composable("settings") {
                            androidx.compose.material3.Text("Экран настроек (заглушка)")
                        }
                    }
                }
            }
        }
    }
}
