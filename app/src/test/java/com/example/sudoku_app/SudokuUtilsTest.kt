
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*
import com.example.sudoku_app.viewmodel.SudokuViewModel
import kotlin.math.exp

class SudokuUtilsTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun testReturnedSquareIndices() {
        val viewModel = SudokuViewModel()
        viewModel.generateSquare(47)
        val expected = listOf(48, 47, 46, 39, 38,37, 30, 29, 28)
        assertEquals(expected, viewModel.uiState.value.squareIndexList)

    }
    @Test
    fun testReturnedColumnIndices(){
        val viewModel = SudokuViewModel()
        viewModel.generateRow(38)
        val expected = listOf(36, 37, 38, 39, 40, 41, 42, 43, 44)
        assertEquals(expected, viewModel.uiState.value.rowIndexList)
    }


}