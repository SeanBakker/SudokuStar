package com.example.sudokustar.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.sudokustar.R
import com.example.sudokustar.game.SudokuApplication
import com.example.sudokustar.game.SudokuGame
import kotlinx.android.synthetic.main.game_result.*
import kotlinx.android.synthetic.main.home_screen.*
import kotlinx.android.synthetic.main.home_screen.EasyButton
import kotlinx.android.synthetic.main.home_screen.HardButton
import kotlinx.android.synthetic.main.home_screen.MediumButton

class GameResultActivity : AppCompatActivity() {

    private lateinit var difficultyButtons: List<Button>
    private var sudokuApplication = SudokuApplication()
    private var sudokuGame = SudokuGame()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_result)

        difficultyButtons = listOf(EasyButton, MediumButton, HardButton)

        if (sudokuApplication.getGameResult()) {
            win_result.setTextColor(ContextCompat.getColor(this, R.color.gold_header))
            lose_result.setTextColor(Color.TRANSPARENT)
        }
        else {
            win_result.setTextColor(Color.TRANSPARENT)
            lose_result.setTextColor(Color.RED)
        }

        //Read Home Screen button input
        difficultyButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                sudokuApplication.setDifficulty(index)
                startNewBoardActivity()
            }
        }

        //Read input from resume game button
        ResumeButton.setOnClickListener {
            finish()
        }

        //Read input from restart game button
        RestartButton.setOnClickListener {
            sudokuGame.recoverCells()
            sudokuApplication.setRestart(true)
            finish()
        }
    }

    private fun startNewBoardActivity() {
        val intent = Intent(this@GameResultActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}