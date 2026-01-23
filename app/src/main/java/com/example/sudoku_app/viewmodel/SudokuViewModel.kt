package com.example.sudoku_app.viewmodel

import androidx.lifecycle.ViewModel
import com.example.sudoku_app.models.SudokuBoard
import com.example.sudoku_app.models.SudokuGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class GameUIState(
    val selectedIndex: Int? = null,
    val columnIndexList: List<Int> = emptyList(),
    val squareIndexList: List<Int> = emptyList(),
    val rowIndexList: List<Int> = emptyList(),
    val board: SudokuBoard = SudokuBoard(),
    val difficulty: Int = 30,
    val difficultyLabel: String = "Easy",
    val clueCount: Int = 33
    )

class SudokuViewModel : ViewModel() {


    private val _uiState = MutableStateFlow(GameUIState())
    val uiState: StateFlow<GameUIState> = _uiState.asStateFlow()

    fun setDifficulty(newDifficulty: Int) {
        val clampedDifficulty = newDifficulty.coerceIn(0, 100)
        _uiState.value = _uiState.value.copy(
            difficulty = clampedDifficulty,
            difficultyLabel = SudokuGenerator.getDifficultyLabel(clampedDifficulty),
            clueCount = SudokuGenerator.getCluesForDifficulty(clampedDifficulty)
        )
    } // updates difficulty level and recalculates the difficulty label and clue count

    fun startNewGame() {
        val fullBoard = SudokuBoard()
        SudokuGenerator.fillBoard(fullBoard)
        val (puzzle, actualDifficulty) = SudokuGenerator.makePuzzle(
            fullBoard,
            _uiState.value.difficulty
        )
        _uiState.value = _uiState.value.copy(
            board = puzzle,
            clueCount = SudokuGenerator.getCluesForDifficulty(actualDifficulty),
            selectedIndex = null,
            columnIndexList = emptyList(),
            rowIndexList = emptyList(),
            squareIndexList = emptyList()
        )
    } // generate new board with current difficulty setting

    fun startNewGameWithDifficulty(difficulty: Int) {
        setDifficulty(difficulty)
        startNewGame()
    } // shortcut function so we can set a new difficulty immediately and generate a new puzzle

    fun enterNumber(index: Int, number: Int) {
        val row = index / 9
        val col = index % 9
        val cell = uiState.value.board.cells[row][col]
        if(!cell.isFixed) {
            cell.value = number
            val solution = _uiState.value.board.solution
            if(solution != null) {
                cell.isCorrect = (number == solution[row][col])
            }
            _uiState.value = _uiState.value.copy(
                board = _uiState.value.board
            )
        }
    } // enter a number (1-9) in a cell at the specified index if cell is empty

    fun clearSelectedCell() {
        val selectedIndex = _uiState.value.selectedIndex
        if (selectedIndex != null) {
            val row = selectedIndex / 9
            val col = selectedIndex % 9
            val cell = _uiState.value.board.cells[row][col]
            if(!cell.isFixed) {
                cell.value = null
                cell.isCorrect = null
                _uiState.value = _uiState.value.copy(
                    board = _uiState.value.board
                )
            }
        }
    } // clear value from currently selected cell

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
        val ySquareIndex = y % 3

        val xPreviousOne =
            (0 until 3).map {  i -> (index - xSquareIndex) - (9 *  ySquareIndex) + i }
        val xPreviousTwo = xPreviousOne.map { value -> (value + 9) }
        val xPreviousThree = xPreviousTwo.map { value -> (value + 9) }

        val results = xPreviousOne + xPreviousTwo + xPreviousThree

        _uiState.value = _uiState.value.copy(
            squareIndexList = results,
        )
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