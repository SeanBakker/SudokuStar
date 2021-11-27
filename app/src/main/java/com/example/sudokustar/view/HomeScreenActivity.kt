package com.example.sudokustar.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.sudokustar.R
import com.example.sudokustar.game.SudokuApplication
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.home_screen.*

/*
Created By Sean Bakker
*/

class HomeScreenActivity : AppCompatActivity() {

    private lateinit var difficultyButtons: List<Button>
    private var sudokuApplication = SudokuApplication()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_screen)

        difficultyButtons = listOf(EasyButton, MediumButton, HardButton)

        //Read Home Screen button input
        difficultyButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
//                updateLoadingButton()
                sudokuApplication.setDifficulty(index)

                val intent = Intent(this@HomeScreenActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }
    }

    private fun updateLoadingButton() {
        loadingText.setTextColor(Color.WHITE)
    }

}