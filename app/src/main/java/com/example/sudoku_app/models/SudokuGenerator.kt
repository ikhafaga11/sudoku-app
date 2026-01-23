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
import com.example.sudoku_app.viewmodel.SudokuViewModel
import kotlin.math.roundToInt

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
    fun singleStep(board: SudokuBoard): Boolean {
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
    } // checks if cell can be solves with 1 step

    fun canSolveWithSingles(board: SudokuBoard): Boolean {
        val temp = board.copy()
        while (true) {
            val progress = singleStep(temp)
            if (!progress) break
        }
        return temp.isComplete()
    }

    fun getCluesForDifficulty(difficulty: Int): Int {
        val clampedDifficulty = difficulty.coerceIn(0,100)
        val maxClues = 45
        val minClues = 22
        return (maxClues - (clampedDifficulty / 100.0) * (maxClues - minClues)).roundToInt()
    } // convert difficulty into number of clues (0 = 45 clues, 100 = 22 clues)

    fun getDifficulty(difficulty: Int): Pair<Int, Int> {
        val clampedDifficulty = difficulty.coerceIn(0, 100)
        val targetClues = getCluesForDifficulty(clampedDifficulty)
        val complexity = (clampedDifficulty * 0.15).roundToInt()
        return targetClues to complexity
    } // calculates difficulty based on a 0-100 scale, 0 (easiest) to 100 (hardest)

    fun getMinPossibilitiesForComplexity(difficulty: Int): Int {
        return when {
            difficulty < 30 -> 2
            difficulty < 70 -> 3
            else -> 4
        }
    } // calculates minimum possibilities for each cell based on complexity (2 for easy, 3 for medium, 4 for hard)

    fun makePuzzle(fullBoard: SudokuBoard, difficulty: Int = 30): Pair<SudokuBoard, Int> {
        val clampedDifficulty = difficulty.coerceIn(0, 100)
        val (targetClues, complexityThreshold) = getDifficulty(clampedDifficulty)
        val minPossibilities = getMinPossibilitiesForComplexity(clampedDifficulty)
        val clueVariance = 2
        val maxAttempts = 20
        var attempts = 0
        var bestPuzzle: SudokuBoard? = null
        var bestScore = Int.MAX_VALUE
        val solution = List(9) { row ->
            List(9) {col ->
                fullBoard.cells[row][col].value!!
            }
        }
        while (attempts < maxAttempts) {
            val clues = (targetClues - clueVariance..targetClues + clueVariance).random()
                .coerceIn(22, 45)
            val puzzle = createPuzzleWithClues(fullBoard, clues)
            puzzle.solution = solution
            val score = evaluatePuzzleDifficulty(puzzle, clampedDifficulty, complexityThreshold, minPossibilities)
            if (score < bestScore) {
                bestScore = score
                bestPuzzle = puzzle
            }
            if (score <= 10) {
                break
            }
            attempts++
        }
        val finalPuzzle = bestPuzzle ?: createPuzzleWithClues(fullBoard, targetClues)
        finalPuzzle.solution = solution
        return finalPuzzle to clampedDifficulty
    } // generates the puzzle with specified difficulty (0-100) - generates multiple attempts and selects the best one

    fun createPuzzleWithClues(fullBoard: SudokuBoard, clues: Int): SudokuBoard {
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
        return puzzle
    } // removes random cells to leave specified number of clues
    fun evaluatePuzzleDifficulty(
        puzzle: SudokuBoard,
        targetDifficulty: Int,
        complexityThreshold: Int,
        minPossibilities: Int
    ): Int {
        val canSolveEasily = canSolveWithSingles(puzzle)
        val complexCells = countComplexCells(puzzle, minPossibilities)
        var score = 0
        if (targetDifficulty < 30) {
            if (!canSolveEasily) score += 50
        }
        else {
            if (canSolveEasily) score += 50
        }
        val complexityDiff = kotlin.math.abs(complexCells - complexityThreshold)
        score += complexityDiff * 2
        return score
    } // scores how well a generated puzzle matches target difficulty
    fun countComplexCells(board: SudokuBoard, minPossibilities: Int): Int {
        var count = 0
        for (row in 0 until 9) {
            for (col in 0 until 9) {
                if (board.cells[row][col].value == null) {
                    val possible = (1..9).filter { board.isMoveValid(row, col, it) }
                    if (possible.size >= minPossibilities) {
                        count++
                    }
                }
            }
        }
        return count
    } // counts how many cells are determined as complex

    fun getDifficultyLabel(difficulty: Int): String {
        return when {
            difficulty < 25 -> "Very Easy"
            difficulty < 40 -> "Easy"
            difficulty < 60 -> "Medium"
            difficulty < 80 -> "Hard"
            else -> "Expert"
        }
    } // descriptor function
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

//@Preview(showBackground = true)
//@Composable
//fun PreviewBoardWithViewModel() {
//    val viewModel = remember { SudokuViewModel() }
//    SudokuBoardPreview(board = viewModel.uiState.value.board)
//}