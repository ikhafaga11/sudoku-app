package com.example.sudoku_app.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sudoku_app.components.Grid
import com.example.sudoku_app.viewmodel.GameViewModel

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = viewModel()
) {
    val board by viewModel.board
    LaunchedEffect(Unit) {
        viewModel.startNewGame()
    }

    Grid(
        board = board,
        selectedRow = viewModel.selectedRow,
        selectedCol = viewModel.selectedCol,
        onCellClick = viewModel::selectCell,
        modifier = modifier
    )
}

@Preview(showSystemUi = true)
@Composable
fun PreviewGameScreen() {
    val viewModel = GameViewModel().apply {
        startNewGame()
    }
    Grid(
        board = viewModel.board.value,
        selectedRow = viewModel.selectedRow,
        selectedCol = viewModel.selectedCol,
        onCellClick = viewModel::selectCell
    )
}
