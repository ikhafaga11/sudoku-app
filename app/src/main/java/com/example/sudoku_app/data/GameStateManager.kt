package com.example.sudoku_app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.sudoku_app.models.SudokuBoard
import com.example.sudoku_app.models.SudokuCell
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "sudoku_prefs")


@Serializable
data class SavedGameState(
    val boardCells: List<List<SudokuCell>>,
    val solution: List<List<Int>>?,
    val difficulty: Int,
    val difficultyLabel: String,
    val clueCount: Int,
    val elapsedTime: Int,
    val notesMode: Boolean,
    val lives: Int
)

class GameStateManager(private val context: Context) {
    companion object {
        private val HAS_SAVED_GAME = booleanPreferencesKey("has_saved_game")
        private val SAVED_GAME_DATA = stringPreferencesKey("saved_game_data")
    }
    val hasSavedGame: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[HAS_SAVED_GAME] ?: false
    }

    suspend fun saveGame(
        board: SudokuBoard,
        difficulty: Int,
        difficultyLabel: String,
        clueCount: Int,
        elapsedTime: Int,
        notesMode: Boolean,
        lives: Int
    ) {
        val savedState = SavedGameState(
            boardCells = board.cells,
            solution = board.solution,
            difficulty = difficulty,
            difficultyLabel = difficultyLabel,
            clueCount = clueCount,
            elapsedTime = elapsedTime,
            notesMode = notesMode,
            lives = lives
        )
        val jsonString = Json.encodeToString(savedState)
        context.dataStore.edit { preferences ->
            preferences[HAS_SAVED_GAME] = true
            preferences[SAVED_GAME_DATA] = jsonString
        }
    }

    suspend fun loadGame(): SavedGameState? {
        return try {
            val preferences = context.dataStore.data.first()
            val jsonString = preferences[SAVED_GAME_DATA] ?: return null
            Json.decodeFromString<SavedGameState>(jsonString)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun clearSavedGame() {
        context.dataStore.edit { preferences ->
            preferences[HAS_SAVED_GAME] = false
            preferences.remove(SAVED_GAME_DATA)
        }
    }

    fun restoreBoardFromSavedState(savedState: SavedGameState): SudokuBoard {
        val newBoard = SudokuBoard(size = 9)

        for (row in 0 until 9) {
            for (col in 0 until 9) {
                val cellData = savedState.boardCells[row][col]
                val cell = newBoard.cells[row][col]

                cell.value = cellData.value
                cell.isFixed = cellData.isFixed
                cell.isCorrect = cellData.isCorrect
                cell.notes = cellData.notes
            }
        }
        newBoard.solution = savedState.solution
        return newBoard
    }
}