package com.example.sudokustar.view

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.example.sudokustar.R
import com.example.sudokustar.game.Cell
import com.example.sudokustar.game.SudokuApplication
import com.example.sudokustar.view.custom.SudokuStarBoardView
import com.example.sudokustar.viewmodel.SudokuStarViewModel
import kotlinx.android.synthetic.main.activity_main.*

/*
Application Created By Sean Bakker
*/

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), SudokuStarBoardView.OnTouchListener {

    private lateinit var viewModel: SudokuStarViewModel
    private lateinit var numberButtons: List<Button>
    private var sudokuApplication = SudokuApplication()

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sudokuStarBoardView.registerListener(this)

        viewModel = ViewModelProviders.of(this).get(SudokuStarViewModel::class.java)
        viewModel.sudokuGame.selectedCellLiveData.observe(this, { updateSelectedCellUI(it) })
        viewModel.sudokuGame.cellsLiveData.observe(this, { updateCells(it) })
        viewModel.sudokuGame.isTakingNotesLiveData.observe(this, { updateNoteTakingUI(it) })
        viewModel.sudokuGame.highlightedKeysLiveData.observe(this, { updateHighlightedKeys(it) })

        numberButtons = listOf(oneButton, twoButton, threeButton, fourButton, fiveButton, sixButton, sevenButton, eightButton, nineButton)

        //Read button input
        numberButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                viewModel.sudokuGame.handleInput((index + 1).toString())
            }
        }

        //Call changeNoteTakingState function when notesButton is clicked
        notesButton.setOnClickListener {
            viewModel.sudokuGame.changeNoteTakingState()
        }

        //Call delete function when deleteButton is clicked
        deleteButton.setOnClickListener { viewModel.sudokuGame.delete() }

        //Listener for Reset Game button
        leaveBoardButton.setOnClickListener {
            val intent = Intent(this@MainActivity, HomeScreenActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            startActivity(intent)
        }

        submit_game.setOnClickListener {

            //Check for finished game board
            val fullBoard = viewModel.sudokuGame.checkFull(viewModel.sudokuGame.getCells())

            //When fullBoard is true, we check for a winning game board
            if (fullBoard) {
                val result = viewModel.sudokuGame.checkWin(viewModel.sudokuGame.getCells())

                //Post game result screen based on winning/losing board
                sudokuApplication.setGameResult(result)

                //Call GameResultActivity()
                val intent = Intent(this@MainActivity, GameResultActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                startActivity(intent)
            }
            else {
                //Board is not complete
                boardNotComplete.setTextColor(Color.WHITE)
            }
        }

//        removeLoadingButton()
    }

    //Perform actions on resuming MainActivity
    override fun onResume() {
        super.onResume()

        //Check for restart requested
        if (sudokuApplication.getRestart()) {
            viewModel.sudokuGame.recoverCells()
            sudokuApplication.setRestart(false)
        }

        //Reset notes mode and button colour
        viewModel.sudokuGame.isTakingNotes = false
        viewModel.sudokuGame.isTakingNotesLiveData.postValue(false)

        //Update button UI
        for (button in numberButtons) {
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.design_default_color_background))
        }
    }

    private fun updateCells(cells: List<Cell>?) = cells?.let {
        sudokuStarBoardView.updateCells(cells)
    }

    private fun updateSelectedCellUI(cell: Pair<Int, Int>?) = cell?.let {
        sudokuStarBoardView.updateSelectedCellUI(cell.first, cell.second)

        //Reset text on screen
        boardNotComplete.setTextColor(Color.TRANSPARENT)
    }

    @SuppressLint("ResourceAsColor")
    private fun updateNoteTakingUI(isNoteTaking: Boolean?) = isNoteTaking?.let {

        val color : Int = if (isNoteTaking) {
            ContextCompat.getColor(this, R.color.darker_gold_header)
        } else {
            Color.LTGRAY
        }
        notesButton.background.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
    }

    private fun updateHighlightedKeys(set: Set<String>?) = set?.let {
        numberButtons.forEachIndexed { index, button ->
            val color = if (set.contains((index + 1).toString())) ContextCompat.getColor(this, R.color.gold_buttons) else Color.LTGRAY
            button.setBackgroundColor(color)
        }
    }

    private fun removeLoadingButton() {
        loadingText.setTextColor(Color.TRANSPARENT)
    }

    override fun onCellTouched(row: Int, col: Int) {
        viewModel.sudokuGame.updateSelectedCell(row, col)
    }
}