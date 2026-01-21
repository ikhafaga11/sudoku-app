package com.example.sudoku_app.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.sudoku_app.models.SudokuBoard
import com.example.sudoku_app.models.SudokuGenerator
class GameViewModel : ViewModel() {
    private val _board = mutableStateOf(SudokuBoard())
    val board: State<SudokuBoard> = _board
    var difficulty by mutableStateOf("Unknown")
        private set
    var selectedRow by mutableStateOf(-1)
    var selectedCol by mutableStateOf(-1)
    fun startNewGame(clues: Int = 30) {
        val fullBoard = SudokuBoard()
        SudokuGenerator.fillBoard(fullBoard)
        val (puzzle, diff) = SudokuGenerator.makePuzzle(fullBoard, clues)
        _board.value = puzzle
        difficulty = diff
        selectedRow = -1
        selectedCol = -1
    }

    fun selectCell(row: Int, col: Int) {
        val cell = _board.value.cells[row][col]
        if (!cell.isFixed) {
            selectedRow = row
            selectedCol = col
        } else {
            selectedRow = -1
            selectedCol = -1
        }
    }

    fun inputNumber(number: Int) {
        val row = selectedRow
        val col = selectedCol
        if (row != -1 && col != -1) {
            val cell = _board.value.cells[row][col]
            if (!cell.isFixed && _board.value.isMoveValid(row, col, number)) {
                cell.value = number
                _board.value = _board.value
            }
        }
    }

    fun clearCell() {
        val row = selectedRow
        val col = selectedCol
        if (row != -1 && col != -1) {
            val cell = _board.value.cells[row][col]
            if (!cell.isFixed) {
                cell.value = null
                _board.value = _board.value
            }
        }
    }

    fun isSolved(): Boolean {
        return _board.value.isComplete()
    }
}
