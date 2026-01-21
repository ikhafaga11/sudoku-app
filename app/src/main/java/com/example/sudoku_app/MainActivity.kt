package com.example.sudoku_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sudoku_app.ui.theme.SudokuappTheme

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
    NavHost(navController = navController, startDestination = "home") {
        composable(route = "home") {
            HomeScreen(onGameStart = {
                navController.navigate("game")
            })
        }
        composable(route = "game") {
            GameScreen()
        }
    }

}

@Composable
fun HomeScreen(modifier: Modifier = Modifier, onGameStart: () -> Unit) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row() {
            Text(stringResource(R.string.homepage_heading))
        }
        Spacer(modifier = modifier.height(16.dp))
        Row() {
            Button(onClick = onGameStart) {
                Text(stringResource(R.string.start_game_btn))
            }
        }
    }
}

@Composable
fun GameScreen(modifier: Modifier = Modifier) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Canvas(modifier = modifier.size(360.dp)) {
            val cellSize = size.width / 9
            val canvasWidth = size.width
            val canvasHeight = size.height

            // draw vertical Lines
            for (i in 1..9) {
                val x = i * cellSize
//                val strokeWidth: Float = if(i % 3 == 0) 4.dp. else 1.dp


                drawLine(
                    start = Offset(x = x, y = 0F),
                    end = Offset(x = x, y = canvasHeight),
                    color = Color.Black

                )
            }

            for (i in 1..9) {
                val y = i * cellSize


                drawLine(
                    start = Offset(x = canvasWidth, y = y),
                    end = Offset(x = 0F, y = y),
                    color = Color.Black,

                    )
            }

        }
    }
}

@Preview()
@Composable
fun PreviewGameScreen() {
    GameScreen()
}




