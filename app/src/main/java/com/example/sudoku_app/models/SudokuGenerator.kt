package com.example.sudoku_app.models

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sudoku_app.viewmodel.GameViewModel

object SudokuGenerator {
    fun fillBoard(board: SudokuBoard): Boolean {
        for (row in 0 until 9) {
            for (col in 0 until 9) {
                if (board.cells[row][col].value == null) {
                    val numbers = (1..9).shuffled()
                    for (num in numbers) {
                        if (board.isMoveValid(row, col, num)) {
                            board.cells[row][col].value = num
                            if (fillBoard(board)) return true
                            board.cells[row][col].value = null
                        }
                    }
                    return false
                }
            }
        }
        return true
    }
    fun nakedSingleStep(board: SudokuBoard): Boolean {
        for (row in 0 until 9) {
            for (col in 0 until 9) {
                val cell = board.cells[row][col]
                if (cell.value == null) {
                    val possible = (1..9).filter { board.isMoveValid(row, col, it) }
                    if (possible.size == 1) {
                        cell.value = possible[0]
                        return true
                    }
                }
            }
        }
        return false
    }

    fun canSolveWithNakedSingles(board: SudokuBoard): Boolean {
        val temp = board.copy()
        while (true) {
            val progress = nakedSingleStep(temp)
            if (!progress) break
        }
        return temp.isComplete()
    }

    fun makePuzzle(fullBoard: SudokuBoard, clues: Int = 30): Pair<SudokuBoard, String> {
        val puzzle = fullBoard.copy()
        val cells = mutableListOf<Pair<Int, Int>>()
        for (r in 0 until 9) for (c in 0 until 9) cells.add(r to c)
        cells.shuffle()
        val removals = 81 - clues
        for (i in 0 until removals) {
            val (r, c) = cells[i]
            puzzle.cells[r][c].value = null
        }

        for (r in 0 until 9) for (c in 0 until 9) {
            puzzle.cells[r][c].isFixed = puzzle.cells[r][c].value != null
        }

        val difficulty = if (canSolveWithNakedSingles(puzzle)) "Easy" else "Harder"
        return puzzle to difficulty
    }
}

@Composable
fun SudokuBoardPreview(board: SudokuBoard) {
    Column {
        board.cells.forEach { row ->
            Row {
                row.forEach { cell ->
                    Text(
                        text = cell.value?.toString() ?: ".",
                        modifier = Modifier.width(20.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBoardWithViewModel() {
    val viewModel = remember { GameViewModel() }
    viewModel.startNewGame()
    SudokuBoardPreview(board = viewModel.board.value)
}