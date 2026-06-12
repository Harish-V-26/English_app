package com.example.english_app.ui

data class Word(
    val word: String,
    val pronunciation: String,
    val definition: String,
    val example: String,
    val imageUrl: String = ""
)