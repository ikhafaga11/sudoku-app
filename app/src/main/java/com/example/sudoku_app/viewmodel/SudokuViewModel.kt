package com.example.sudoku_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sudoku_app.models.SudokuBoard
import com.example.sudoku_app.models.SudokuGenerator
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class GameUIState(
    val selectedIndex: Int? = null,
    val columnIndexList: List<Int> = emptyList(),
    val squareIndexList: List<Int> = emptyList(),
    val rowIndexList: List<Int> = emptyList(),
    val board: SudokuBoard = SudokuBoard(),
    val difficulty: Int = 30,
    val difficultyLabel: String = "Easy",
    val clueCount: Int = 33,
    val elapsedTime: Int = 0,
    val isComplete: Boolean = false
    )

class SudokuViewModel : ViewModel() {


    private val _uiState = MutableStateFlow(GameUIState())
    val uiState: StateFlow<GameUIState> = _uiState.asStateFlow()

    var timerJob: Job? = null

    fun setDifficulty(newDifficulty: Int) {
        val clampedDifficulty = newDifficulty.coerceIn(0, 100)
        _uiState.value = _uiState.value.copy(
            difficulty = clampedDifficulty,
            difficultyLabel = SudokuGenerator.getDifficultyLabel(clampedDifficulty),
            clueCount = SudokuGenerator.getCluesForDifficulty(clampedDifficulty)
        )
    } // updates difficulty level and recalculates the difficulty label and clue count

    fun startNewGame() {
        stopTimer()
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
            squareIndexList = emptyList(),
            elapsedTime = 0,
            isComplete = false
        )
        startTimer()
    } // generate new board with current difficulty setting

    fun startNewGameWithDifficulty(difficulty: Int) {
        setDifficulty(difficulty)
        startNewGame()
    } // shortcut function so we can set a new difficulty immediately and generate a new puzzle

    fun autoCompletePuzzle() {
        val board = _uiState.value.board
        val solution = board.solution ?: return
        for (row in 0 until 9) {
            for (col in 0 until 9) {
                val cell = board.cells[row][col]
                if (!cell.isFixed) {
                    cell.value = solution[row][col]
                    cell.isCorrect = true
                }
            }
        }
        val newBoard = board.copy()
        stopTimer()
        _uiState.value = _uiState.value.copy(
            board = newBoard,
            isComplete = true
        )
    }


    fun enterNumber(index: Int, number: Int) {
        val row = index / 9
        val col = index % 9
        val currentBoard = _uiState.value.board
        val cell = currentBoard.cells[row][col]
        if(!cell.isFixed) {
            cell.value = number
            val solution = _uiState.value.board.solution
            if(solution != null) {
                cell.isCorrect = (number == solution[row][col])
            }
            val newBoard = currentBoard.copy()
            val isComplete = checkIfComplete(newBoard)
            if(isComplete){
                stopTimer()
            }
            _uiState.value = _uiState.value.copy(
                board = newBoard,
                isComplete = isComplete
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
                    board = _uiState.value.board,
                    isComplete = false
                )
            }
        }
    } // clear value from currently selected cell

    fun checkIfComplete(board: SudokuBoard): Boolean {
        val allFilled = board.cells.all { row ->
            row.all { it.value != null }
        }
        if(!allFilled) return false
        val solution = board.solution ?: return false

        for (row in 0 until 9) {
            for (col in 0 until 9) {
                val cellValue = board.cells[row][col].value
                if (cellValue != solution[row][col]) {
                    return false
                }
            }
        }

        return true
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _uiState.value = _uiState.value.copy(
                    elapsedTime = _uiState.value.elapsedTime + 1
                )
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return "%02d:%02d".format(minutes, remainingSeconds)
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }

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