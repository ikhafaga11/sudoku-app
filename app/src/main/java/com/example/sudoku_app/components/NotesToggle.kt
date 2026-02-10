package com.example.sudoku_app.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sudoku_app.R
import com.example.sudoku_app.viewmodel.SudokuViewModel

@Composable
fun NotesToggle(
    modifier: Modifier = Modifier,
    sudokuViewModel: SudokuViewModel = viewModel()
) {
    val state by sudokuViewModel.uiState.collectAsState()
    val notesMode = state.notesMode

    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(horizontal = 8.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = { sudokuViewModel.toggleNotesMode() }
            ) {
                Icon(
                    painter = painterResource(R.drawable.pencil),
                    contentDescription = if (notesMode) "Notes mode on" else "Notes mode off",
                    modifier = Modifier.padding(2.dp),
                    tint = if (notesMode) Color(0xFF1976D2) else Color.Gray
                )
            }
            Text(
                text = if (notesMode) "On" else "Off",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (notesMode) Color(0xFF1976D2) else Color.Gray,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 0.dp, end = 0.dp)
            )
        }
        Text(
            text = "Notes",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NotesTogglePreview() {
    NotesToggle()
}