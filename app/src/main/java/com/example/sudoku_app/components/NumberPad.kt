package com.example.sudoku_app.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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

    FlowColumn(
        modifier = modifier
            .fillMaxWidth(0.5f)
            .padding(start = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        listOf(
            listOf(1, 2, 3),
            listOf(4, 5, 6),
            listOf(7, 8, 9)
        ).forEach { rowNumbers ->

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                rowNumbers.forEach { number ->
                    IconButton(
                        onClick = {
                            selectedIndex?.let {
                                sudokuViewModel.enterNumber(it, number)
                            }
                        },
                        enabled = selectedIndex != null,
                        modifier = Modifier.size(64.dp)
                    ) {
                        Text(number.toString(), fontSize = 22.sp)
                    }
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = {
                    selectedIndex?.let {
                        sudokuViewModel.enterNumber(it, 0)
                    }
                },
                enabled = selectedIndex != null,
                modifier = Modifier.size(64.dp)
            ) {
                Text("0", fontSize = 22.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NumberPadPreview() {
    NumberPad()
}