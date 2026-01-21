package com.example.sudoku_app.models

data class SudokuCell(
    val row: Int,
    val col: Int,
    var value: Int? = null,
    var isFixed: Boolean = false
)