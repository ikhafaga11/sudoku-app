package com.example.sudoku_app.models

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class SudokuBoard(val size: Int = 9) {
    val cells: List<List<SudokuCell>> = List(size) { row ->
        List(size) { col -> SudokuCell(row, col) }
    }
    fun isMoveValid(row: Int, col: Int, number: Int): Boolean {
        if (cells[row].any { it.value == number }) return false
        if (cells.any { it[col].value == number }) return false
        val boxRow = (row / 3) * 3
        val boxCol = (col / 3) * 3
        for (r in boxRow until boxRow + 3) {
            for (c in boxCol until boxCol + 3) {
                if (cells[r][c].value == number) return false
            }
        }
        return true
    }

    fun isComplete(): Boolean {
        return cells.all { row -> row.all { it.value != null } }
    }

    fun copy(): SudokuBoard {
        val newBoard = SudokuBoard(size)
        for (r in 0 until size) {
            for (c in 0 until size) {
                newBoard.cells[r][c].value = this.cells[r][c].value
                newBoard.cells[r][c].isFixed = this.cells[r][c].isFixed
            }
        }
        return newBoard
    }
}
@Preview(showBackground = true)
@Composable
fun SudokuBoardPreview() {
    val board = SudokuBoard().apply {
        for (r in 0 until 9) {
            for (c in 0 until 9) {
                cells[r][c].value = ((r * 3 + c) % 9 + 1)
            }
        }
    }
    PreviewSudokuBoard(board = board)
}
@Composable
fun PreviewSudokuBoard(board: SudokuBoard) {
    Column(modifier = androidx.compose.ui.Modifier.padding(16.dp)) {
        board.cells.forEach { row ->
            Row {
                row.forEach { cell ->
                    Text(
                        text = cell.value?.toString() ?: ".",
                        modifier = androidx.compose.ui.Modifier.width(20.dp)
                    )
                }
            }
        }
    }
}