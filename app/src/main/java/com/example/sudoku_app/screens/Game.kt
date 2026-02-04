package com.example.sudoku_app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import com.example.sudoku_app.components.NotesToggle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController


@Composable
fun GameScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    sudokuViewModel: SudokuViewModel = viewModel()
) {
    val state by sudokuViewModel.uiState.collectAsState()
    if (state.isComplete) {
        AlertDialog(
            onDismissRequest = {},
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
                Button(onClick = {navController.popBackStack()}) {
                    Text("Back to homepage")
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

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = sudokuViewModel.formatTime(state.elapsedTime),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = { sudokuViewModel.autoCompletePuzzle() },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Auto Complete (debugging)")
                } // button is only for debugging purposes
            }
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
            NotesToggle(
                modifier = Modifier.fillMaxWidth(),
                sudokuViewModel = sudokuViewModel
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGameScreen() {
    GameScreen(navController = rememberNavController())
}
