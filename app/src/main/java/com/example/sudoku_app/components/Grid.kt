package com.example.sudoku_app.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Shadow
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
    val flashingIndex = state.flashingIndex
    val completionHighlight = state.completionHighlight
    val matchingIndices = state.matchingNumberIndices
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
            Column(
                modifier = modifier.matchParentSize(),
            ) {
                for (row in 0 until 9) {
                    Row(
                        modifier = Modifier.weight(1f),
                    ) {
                        for (col in 0 until 9) {
                            val index = row * 9 + col
                            val cell = board.cells[row][col]
                            val isSelectedCell = index == selectedIndex
                            val isInColumn = index in columnIndices
                            val isInRow = index in rowIndices
                            val isInSquare = index in squareIndices
                            val isFlashing = index == flashingIndex
                            val isMatchingNumber = index in matchingIndices
                            val isInCompletion = completionHighlight?.indices?.contains(index) == true
                            val completionAlpha = if (isInCompletion && completionHighlight != null) {
                                val sourceRow = completionHighlight.sourceIndex / 9
                                val sourceCol = completionHighlight.sourceIndex % 9
                                val cellRow = index / 9
                                val cellCol = index % 9
                                val distance = kotlin.math.abs(cellRow - sourceRow) + kotlin.math.abs(cellCol - sourceCol)
                                val maxDistance = completionHighlight.indices.maxOf { cellIndex ->
                                    val r = cellIndex / 9
                                    val c = cellIndex % 9
                                    kotlin.math.abs(r - sourceRow) + kotlin.math.abs(c - sourceCol)
                                }.toFloat()
                                val normalizedDistance = if (maxDistance > 0) distance.toFloat() / maxDistance else 0f
                                val rippleStart = normalizedDistance * 0.7f
                                val rippleWidth = 0.25f
                                val rippleEnd = rippleStart + rippleWidth
                                when {
                                    completionHighlight.progress < rippleStart -> 0f
                                    completionHighlight.progress > rippleEnd -> {
                                        val fadeProgress = (completionHighlight.progress - rippleEnd) / (1.0f - rippleEnd)
                                        val fadeAlpha = 0.3f * (1f - fadeProgress)
                                        fadeAlpha.coerceAtLeast(0f)
                                    }
                                    else -> {
                                        val localProgress = (completionHighlight.progress - rippleStart) / rippleWidth
                                        val wave = kotlin.math.sin(localProgress * Math.PI).toFloat()
                                        0.4f + (wave * 0.5f)
                                    }
                                }
                            } else 0f

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .background(
                                        when {
                                            isFlashing -> Color(0xFFF44336)
                                            isInCompletion -> Color(0xFF4CAF50).copy(alpha = completionAlpha)
                                            isSelectedCell -> Color.Black
                                            isMatchingNumber -> Color(0xFFBBDEFB)
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
                                            isInCompletion && !cell.isFixed -> Color(0xFF4CAF50)
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
                                    Text(
                                        text = cell.notes.sorted().joinToString(" "),
                                        color = when {
                                            isSelectedCell -> Color.White
                                            else -> Color(0xFF303030)
                                        },
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
                // draw horizontal lines
                for (i in 0..9) {
                    val y = i * cellSize
                    val strokeWidth = if (i % 3 == 0) 4.dp.toPx() else 1.dp.toPx()
                    drawLine(
                        start = Offset(x = 0F, y = y),
                        end = Offset(x = canvasWidth, y = y),
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