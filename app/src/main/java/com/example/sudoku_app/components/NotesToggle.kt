package com.example.sudoku_app.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sudoku_app.viewmodel.SudokuViewModel

@Composable
fun NotesToggle(
    modifier: Modifier = Modifier,
    sudokuViewModel: SudokuViewModel = viewModel()
) {
    val state by sudokuViewModel.uiState.collectAsState()
    Column(
        modifier = modifier
            .padding(end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Notes",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        Switch(
            checked = state.notesMode,
            onCheckedChange = { sudokuViewModel.toggleNotesMode() },
            modifier = Modifier.size(width = 60.dp, height = 40.dp),
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF2196F3),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.Gray
            )
        )
        Text(
            text = if (state.notesMode) "ON" else "OFF",
            fontSize = 14.sp,
            fontWeight = if (state.notesMode) FontWeight.Bold else FontWeight.Normal,
            color = if (state.notesMode) Color(0xFF2196F3) else Color.Gray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NotesTogglePreview() {
    NotesToggle()
}