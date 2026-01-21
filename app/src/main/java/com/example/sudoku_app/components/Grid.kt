package com.example.sudoku_app.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun Grid(modifier: Modifier = Modifier) {

    // Just for example
    val numbers: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
    val board = buildList {
        repeat(9)
        {
            addAll(numbers)
        }
    }


    Column(modifier = modifier.fillMaxSize()
        , verticalArrangement = Arrangement.Center) {
        Box(
            Modifier
                .aspectRatio(1f)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = modifier.matchParentSize()) {
                val cellSize = size.width / 9
                val canvasWidth = size.width
                val canvasHeight = size.height

                // draw vertical Lines
                for (i in 1..9) {
                    val x = i * cellSize
                    val strokeWidth: Float = if (i % 3 == 0 && i != 9) 4.dp.toPx() else 1.dp.toPx()


                    drawLine(
                        start = Offset(x = x, y = 0F),
                        end = Offset(x = x, y = canvasHeight),
                        color = Color.Black,
                        strokeWidth = strokeWidth

                    )
                }

                for (i in 1..9) {
                    val y = i * cellSize
                    val strokeWidth = if (i % 3 == 0 && i != 9) 4.dp.toPx() else 1.dp.toPx()


                    drawLine(
                        start = Offset(x = canvasWidth, y = y),
                        end = Offset(x = 0F, y = y),
                        color = Color.Black,
                        strokeWidth = strokeWidth

                    )
                }

            }
            LazyVerticalGrid(
                modifier = modifier.matchParentSize(),
                verticalArrangement = Arrangement.Center,
                columns = GridCells.Fixed(9)
            ) {
                board.forEachIndexed { index, int ->
                    items(1) {
                        Box(
                            modifier = modifier.aspectRatio(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("${index + 1}")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewGrid() {
    Grid()
}