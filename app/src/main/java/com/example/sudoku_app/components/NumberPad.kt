package com.example.sudoku_app.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sudoku_app.viewmodel.SudokuViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NumberPad(
    modifier: Modifier = Modifier,
    sudokuViewModel: SudokuViewModel = viewModel()
) {
    val state by sudokuViewModel.uiState.collectAsState()
    val selectedIndex = state.selectedIndex
    FlowColumn {
        FlowRow(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
        ) {
            (1..9).forEach { number ->
                IconButton(
                    onClick = {
                        if (selectedIndex != null) {
                        sudokuViewModel.enterNumber(selectedIndex, number)
                    }},
                    modifier = modifier.size(40.dp)
                ) {
                    Text("$number", fontSize = 40.sp)
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun NumberPadPreview() {
    NumberPad()
}

