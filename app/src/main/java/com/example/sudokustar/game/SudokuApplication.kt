package com.example.sudokustar.game

import android.app.Application

open class SudokuApplication : Application() {

    companion object {
        private var difficulty = 0
        private var gameResult = false
        private var restart = false
    }

    fun setDifficulty(newDifficulty: Int) {
        difficulty = newDifficulty
    }

    fun getDifficulty(): Int {
        return difficulty
    }

    fun setGameResult(newGameResult: Boolean) {
        gameResult = newGameResult
    }

    fun getGameResult(): Boolean {
        return gameResult
    }

    fun setRestart(newRestart: Boolean) {
        restart = newRestart
    }

    fun getRestart(): Boolean {
        return restart
    }

}