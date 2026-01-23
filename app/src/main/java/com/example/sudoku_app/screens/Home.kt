package com.example.sudoku_app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sudoku_app.R
import com.example.sudoku_app.viewmodel.SudokuViewModel


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onGameStart: () -> Unit,
    sudokuViewModel: SudokuViewModel = viewModel()
) {
    val state by sudokuViewModel.uiState.collectAsState()
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row() {
            Text(stringResource(R.string.homepage_heading))
        }
        Spacer(modifier = modifier.height(32.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Difficulty: ${state.difficultyLabel}",
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Slider(
                value = state.difficulty.toFloat(),
                onValueChange = { sudokuViewModel.setDifficulty(it.toInt()) },
                valueRange = 0f..100f,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Clues:  ${state.clueCount}",
                fontSize = 14.sp
            )
        }

        Spacer(modifier = modifier.height(16.dp))

        Row() {
            Button(onClick = {
                sudokuViewModel.startNewGame()
                onGameStart()
            }) {
                Text(stringResource(R.string.start_game_btn))
            }
        }
    }
}
