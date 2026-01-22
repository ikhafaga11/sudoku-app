package com.example.sudoku_app.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class GameUIState(
    val selectedIndex: Int? = null,
    var columnIndexList: List<Int> = emptyList(),
    val squareIndexList: List<Int> = emptyList(),
    val rowIndexList: List<Int> = emptyList(),

    )

class SudokuViewModel : ViewModel() {


    private val _uiState = MutableStateFlow(GameUIState())

    val uiState: StateFlow<GameUIState> = _uiState.asStateFlow()

    fun generateColumn(index: Int) {
        val remainder = index % 9

        val columnIndices = (0 until 9).map { i -> (9 * i) + remainder }

        _uiState.value = _uiState.value.copy(
            columnIndexList = columnIndices
        )
    }

    fun generateSquare(index: Int) {
        val xSquareIndex = index % 3
        val y = index / 9
        val ySquareIndex = y % 3  // 48 = 0

        //45 - 0 = 45
        // 45 - 9 = 36
        // 36 - 9 = 27

        //46 - 1 = 45

        // 47 - 2 == 45

        val xPreviousOne =
            (0 until 3).map {  i -> (index - xSquareIndex) - (9 *  ySquareIndex) + i }
        val xPreviousTwo = xPreviousOne.map { value -> (value + 9) }
        val xPreviousThree = xPreviousTwo.map { value -> (value + 9) }

        val results = xPreviousOne + xPreviousTwo + xPreviousThree

        _uiState.value = _uiState.value.copy(
            squareIndexList = results,
        )


        // indexCopy = index

        // if y == 0 && x == 0
        // xPreviousTwo = (0 until 3).map{i -> index - i}
        // yPreviousTwo = xPreviousTwo.forEach( value -> (0 until 3).map{i -> value-(i*10) })

//        48, 47, 46

//        if
//        (0,0)(0,1)(0,2)(0,3)
//        (1,0)(1,1)(1,2)(1,3)
//        (2,0)(2,1)(2,2)(2,3)
//        (3,0)(3,1)(3,2)(3,3)
        // index - 3
        // (48 - 9) - 3
        // (48 - 18) - 3

//        .     .   .
//        .     .   .
//        .     .   48 (0,0)

    }

    fun generateRow(index: Int) {
        val remainder = index % 9
        val rowStart = index - (remainder)

        val rowIndices = (0 until 9).map { i -> rowStart + i }

        _uiState.value = _uiState.value.copy(
            rowIndexList = rowIndices
        )
    }

    fun onSelectedIndex(index: Int) {
        if (_uiState.value.selectedIndex == null) {
            _uiState.value = _uiState.value.copy(
                selectedIndex = index
            )
            generateColumn(index)
            generateRow(index)
            generateSquare(index)
        } else if (_uiState.value.selectedIndex != index) {

            _uiState.value = _uiState.value.copy(
                selectedIndex = index
            )
            generateColumn(index)
            generateRow(index)
            generateSquare(index)
        } else {
            _uiState.value = _uiState.value.copy(
                selectedIndex = null,
                columnIndexList = emptyList(),
                rowIndexList = emptyList(),
                squareIndexList = emptyList()

            )
        }
    }

}