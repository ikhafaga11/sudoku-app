package com.example.sudoku_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sudoku_app.screens.GameScreen
import com.example.sudoku_app.screens.HomeScreen
import com.example.sudoku_app.ui.theme.SudokuappTheme
import com.example.sudoku_app.viewmodel.SudokuViewModel
import androidx.compose.ui.platform.LocalContext
import com.example.sudoku_app.viewmodel.SudokuViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SudokuappTheme {
                App()
            }
        }
    }
}

@Composable
fun App() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sudokuViewModel: SudokuViewModel = viewModel(
        factory = SudokuViewModelFactory(context.applicationContext)
    )
    NavHost(navController = navController, startDestination = "home") {
        composable(route = "home") {
            HomeScreen(onGameStart = {
                navController.navigate("game")
            }, sudokuViewModel = sudokuViewModel)
        }
        composable(route = "game") {
            GameScreen(
                navController = navController,
                sudokuViewModel = sudokuViewModel
            )
        }
    }
}



