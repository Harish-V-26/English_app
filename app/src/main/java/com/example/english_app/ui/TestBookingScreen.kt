package com.example.english_app.ui

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.border

@Composable
fun TestBookingScreen(onBook: (String) -> Unit) {
    var testDate by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Book Your Test")
        BasicTextField(
            value = testDate,
            onValueChange = { testDate = it },
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.border(1.dp, Color.Gray)) {
                    innerTextField()
                }
            }
        )
        Button(onClick = { onBook(testDate) }) {
            Text("Book Test")
        }
    }
}

@Preview
@Composable
fun PreviewTestBookingScreen() {
    TestBookingScreen(onBook = {})
}
