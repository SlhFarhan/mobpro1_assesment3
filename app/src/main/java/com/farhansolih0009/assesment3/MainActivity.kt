package com.farhansolih0009.assesment3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.farhansolih0009.assesment3.ui.screen.*
import com.farhansolih0009.assesment3.ui.theme.Assesment3Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Assesment3Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    // ViewModel dibuat satu kali di sini
                    val mainViewModel: MainViewModel = viewModel()

                    NavHost(
                        navController = navController,
                        startDestination = "splash_screen"
                    ) {
                        composable("splash_screen") { SplashScreen(navController) }
                        composable("home_screen") { HomeScreen(navController) }

                        // --- PERUBAHAN DI SINI ---
                        // Teruskan instance ViewModel yang sama ke MainScreen
                        composable("main_screen") {
                            MainScreen(navController = navController, viewModel = mainViewModel)
                        }

                        composable(
                            route = "detail_screen/{filmId}",
                            arguments = listOf(navArgument("filmId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val filmId = backStackEntry.arguments?.getInt("filmId")
                            // Gunakan instance ViewModel yang sama untuk mencari film
                            val film = filmId?.let { mainViewModel.getFilmById(it) }

                            if (film != null) {
                                DetailScreen(navController = navController, film = film)
                            } else {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("Film tidak ditemukan.")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
