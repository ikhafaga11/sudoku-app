package com.example.sudoku_app.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sudoku_app.viewmodel.SudokuViewModel

@Composable
fun Grid(modifier: Modifier = Modifier, sudokuViewModel: SudokuViewModel = viewModel()) {

    val state by sudokuViewModel.uiState.collectAsState()
    val board = state.board
    val selectedIndex = state.selectedIndex
    val columnIndices = state.columnIndexList
    val rowIndices = state.rowIndexList
    val squareIndices = state.squareIndexList
    val flattenedBoard = buildList {
        for (row in 0 until 9) {
            for (col in 0 until 9) {
                add(board.cells[row][col])
            }
        }
    }

    Column(modifier = modifier.background(Color.White)) {
        Box(
            Modifier
                .aspectRatio(1f)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            LazyVerticalGrid(
                modifier = modifier.matchParentSize(),
                verticalArrangement = Arrangement.Center,
                columns = GridCells.Fixed(9)
            ) {
                flattenedBoard.forEachIndexed { index, cell ->
                    val isSelectedCell = index == selectedIndex
                    val isInColumn = index in columnIndices
                    val isInRow = index in rowIndices
                    val isInSquare = index in squareIndices

                    items(1) {
                        Box(
                            modifier = modifier
                                .aspectRatio(1f)
                                .background(
                                    when {
                                        isSelectedCell -> Color.Black
                                        isInColumn -> Color.LightGray
                                        isInRow -> Color.LightGray
                                        isInSquare -> Color.LightGray
                                        else -> Color.Transparent

                                    }
                                )
                                .clickable {
                                    sudokuViewModel.onSelectedIndex(index)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (cell.value != null) {
                                Text(
                                    text = cell.value.toString(),
                                    color = when {
                                        isSelectedCell -> Color.White
                                        cell.isFixed -> Color.Black
                                        cell.isCorrect == true -> Color(0xFF4CAF50)
                                        cell.isCorrect == false -> Color(0xFFF44336)
                                        else -> Color(0xFF1976D2)
                                    },
                                    fontSize = 20.sp,
                                    fontWeight = if (cell.isFixed) FontWeight.Bold else FontWeight.Normal
                                )
                            } else if (cell.notes.isNotEmpty()) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Top,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = cell.notes.sorted().joinToString(" "),
                                        color = Color(0xFFBDBDBD), // Light grey
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Canvas(
                modifier = modifier
                    .matchParentSize()
            ) {
                val cellSize = size.width / 9
                val canvasWidth = size.width
                val canvasHeight = size.height

                // draw vertical Lines
                for (i in 0..9) {
                    val x = i * cellSize
                    val strokeWidth: Float = if (i % 3 == 0) 4.dp.toPx() else 1.dp.toPx()


                    drawLine(
                        start = Offset(x = x, y = 0F),
                        end = Offset(x = x, y = canvasHeight),
                        color = Color.Black,
                        strokeWidth = strokeWidth

                    )
                }

                for (i in 0..9) {
                    val y = i * cellSize
                    val strokeWidth = if (i % 3 == 0) 4.dp.toPx() else 1.dp.toPx()


                    drawLine(
                        start = Offset(x = canvasWidth, y = y),
                        end = Offset(x = 0F, y = y),
                        color = Color.Black,
                        strokeWidth = strokeWidth

                    )
                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGrid() {
    Grid()
}