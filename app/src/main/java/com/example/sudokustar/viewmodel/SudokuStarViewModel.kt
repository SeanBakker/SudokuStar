package com.example.sudokustar.viewmodel

import androidx.lifecycle.ViewModel
import com.example.sudokustar.game.SudokuGame

class SudokuStarViewModel : ViewModel(){
    val sudokuGame = SudokuGame()
}