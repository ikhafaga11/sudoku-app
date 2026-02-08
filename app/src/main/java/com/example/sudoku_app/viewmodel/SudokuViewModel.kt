package com.example.sudoku_app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sudoku_app.models.SudokuBoard
import com.example.sudoku_app.models.SudokuGenerator
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.sudoku_app.data.GameStateManager

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
    val isComplete: Boolean = false,
    val notesMode: Boolean = false,
    val hasActiveGame: Boolean = false,
    var lives: Int = 3,
    val flashingIndex: Int?= null,
    val isGameOver: Boolean = false,
    val showCompletionDialog: Boolean = false
    )

class SudokuViewModel(val gameStateManager: GameStateManager) : ViewModel() {


    private val _uiState = MutableStateFlow(GameUIState())
    val uiState: StateFlow<GameUIState> = _uiState.asStateFlow()

    var timerJob: Job? = null

    init {
        viewModelScope.launch {
            gameStateManager.hasSavedGame.collect { hasSaved ->
                _uiState.value = _uiState.value.copy(hasActiveGame = hasSaved)
            }
        }
    }

    fun toggleNotesMode() {
        _uiState.value = _uiState.value.copy(
            notesMode = !_uiState.value.notesMode
        )
    } // toggle for notes mode

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
            isComplete = false,
            lives = 3,
            isGameOver = false
        )
        startTimer()
    } // generate new board with current difficulty setting

    fun startNewGameWithDifficulty(difficulty: Int) {
        setDifficulty(difficulty)
        startNewGame()
    } // shortcut function so we can set a new difficulty immediately and generate a new puzzle

    suspend fun resumeGame() {
        val savedState = gameStateManager.loadGame() ?: return
        val restoredBoard = gameStateManager.restoreBoardFromSavedState(savedState)
        _uiState.value = _uiState.value.copy(
            board = restoredBoard,
            difficulty = savedState.difficulty,
            difficultyLabel = savedState.difficultyLabel,
            clueCount = savedState.clueCount,
            elapsedTime = savedState.elapsedTime,
            notesMode = savedState.notesMode,
            selectedIndex = null,
            columnIndexList = emptyList(),
            rowIndexList = emptyList(),
            squareIndexList = emptyList(),
            isComplete = false,
            hasActiveGame = true
        )
        startTimer()
    }

    fun clearSavedGame() {
        viewModelScope.launch {
            gameStateManager.clearSavedGame()
            _uiState.value = _uiState.value.copy(hasActiveGame = false)
        }
    }

    fun saveGameState() {
        if (_uiState.value.isComplete) {
            clearSavedGame()
            return
        }
        viewModelScope.launch {
            gameStateManager.saveGame(
                board = _uiState.value.board,
                difficulty = _uiState.value.difficulty,
                difficultyLabel = _uiState.value.difficultyLabel,
                clueCount = _uiState.value.clueCount,
                elapsedTime = _uiState.value.elapsedTime,
                notesMode = _uiState.value.notesMode
            )
        }
    }

    fun autoCompletePuzzle() {
        val board = _uiState.value.board
        val solution = board.solution ?: return
        for (row in 0 until 9) {
            for (col in 0 until 9) {
                val cell = board.cells[row][col]
                if (!cell.isFixed) {
                    cell.value = solution[row][col]
                    cell.isCorrect = true
                    cell.notes = emptyList()
                }
            }
        }
        val newBoard = board.copy()
        stopTimer()
        _uiState.value = _uiState.value.copy(
            board = newBoard,
            isComplete = true,
            showCompletionDialog = true
        )
    } // only for debugging, comment out eventually


    fun enterNumber(index: Int, number: Int) {
        val row = index / 9
        val col = index % 9
        val currentBoard = _uiState.value.board
        val cell = currentBoard.cells[row][col]
        if(!cell.isFixed) {
            if (_uiState.value.notesMode) {
                if (cell.notes.contains(number)) {
                    cell.notes -= number
                } else {
                    cell.notes += number
                }
            } else {
                cell.value = number
                cell.notes = emptyList()
                val solution = _uiState.value.board.solution
                if (solution != null) {
                    cell.isCorrect = (number == solution[row][col])
                }
                if (cell.isCorrect == false) {
                    _uiState.value = _uiState.value.copy(lives = _uiState.value.lives - 1)
                    triggerErrorFlash(index)
                    return
                }
            }
            val newBoard = currentBoard.copy()
            val isComplete = checkIfComplete(newBoard)
            if(isComplete){
                stopTimer()
                clearSavedGame()
            }
            _uiState.value = _uiState.value.copy(
                board = newBoard,
                isComplete = isComplete,
                showCompletionDialog = isComplete
            )
            if (!isComplete){
                saveGameState()
            }
        }
    } // enter a number (1-9) in a cell at the specified index if cell is empty OR add a note if toggled on

    fun dismissCompletionDialog(){
        _uiState.value = _uiState.value.copy(showCompletionDialog = false)
    }

    fun triggerFlash(index: Int) {
        val row = index / 9
        val col = index % 9
        val currentBoard = _uiState.value.board
        val cell = currentBoard.cells[row][col]
        viewModelScope.launch{
            cell.isFixed = true
            repeat(2) {
                _uiState.value = _uiState.value.copy(flashingIndex = index)
                delay(200)
                _uiState.value = _uiState.value.copy(flashingIndex = null)
                delay(200)
            }
            cell.isFixed = false
            val newBoard = _uiState.value.board.copy()
            val gameOver = _uiState.value.lives <= 0
            if(gameOver){
                stopTimer()
                clearSavedGame()
            }
            _uiState.value = _uiState.value.copy(
                board = newBoard,
                isGameOver = gameOver
            )
            if(!gameOver) {
                saveGameState()
            }
        }
    }

    fun triggerCorrectFlash(index: Int){
        val row = index / 9
        val col = index % 9
        val currentBoard = _uiState.value.board
        val cell = currentBoard.cells[row][col]
        viewModelScope.launch{
            cell.isFixed = true
            repeat(2) {
                _uiState.value = _uiState.value.copy(flashingIndex = index)
                delay(200)
                _uiState.value = _uiState.value.copy(flashingIndex = null)
                delay(200)
            }
            cell.isFixed = false
        }
    }

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
                    isComplete = false,
                    isGameOver = false
                )
                saveGameState()
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
                if(_uiState.value.elapsedTime % 5 == 0){
                    saveGameState()
                }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
        saveGameState()
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
class SudokuViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SudokuViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SudokuViewModel(GameStateManager(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}