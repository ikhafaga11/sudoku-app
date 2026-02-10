package com.example.sudoku_app.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
fun EraseButton(
    modifier: Modifier = Modifier,
    sudokuViewModel: SudokuViewModel = viewModel()
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(horizontal = 8.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = { sudokuViewModel.clearSelectedCell() }
        ) {
            Icon(
                painter = painterResource(R.drawable.eraser),
                contentDescription = "Erase selected cell",
                modifier = Modifier.padding(2.dp),
            )
        }
        Text(
            text = "Erase",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEraseButton() {
    EraseButton()
}