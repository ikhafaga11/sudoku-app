package com.example.sudoku_app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sudoku_app.components.Grid
import com.example.sudoku_app.components.NumberPad
import com.example.sudoku_app.viewmodel.SudokuViewModel
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.TextButton


@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    sudokuViewModel: SudokuViewModel = viewModel()
) {
    val state by sudokuViewModel.uiState.collectAsState()
    if (state.isComplete) {
        AlertDialog(
            onDismissRequest = { /* Block dismiss if you want */ },
            title = {
                Text(text = "🎉 Puzzle Complete!")
            },
            text = {
                Text(
                    text = "You solved the Sudoku in ${
                        sudokuViewModel.formatTime(state.elapsedTime)
                    }"
                )
            },
            confirmButton = {
                Button(onClick = { sudokuViewModel.startNewGame() }) {
                    Text("New Game")
                }
            }
        )
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Difficulty: ${state.difficultyLabel}",
                    fontSize = 14.sp
                )
                Text(
                    text = "Clues: ${state.clueCount}",
                    fontSize = 14.sp
                )
            }

            // Timer
            Text(
                text = sudokuViewModel.formatTime(state.elapsedTime),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(10.dp)
                .weight(2f),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Grid(sudokuViewModel = sudokuViewModel)
        }

        HorizontalDivider(thickness = 2.dp)

        Row(modifier = modifier
            .weight(1.5f)
            .padding(top = 16.dp)) {
            NumberPad(sudokuViewModel = sudokuViewModel)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGameScreen() {
    GameScreen()
}
