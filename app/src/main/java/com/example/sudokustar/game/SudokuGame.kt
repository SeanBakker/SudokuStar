package com.example.sudokustar.game

import androidx.lifecycle.MutableLiveData

class SudokuGame {

    var selectedCellLiveData = MutableLiveData<Pair<Int, Int>>()
    var cellsLiveData = MutableLiveData<List<Cell>>()
    var isTakingNotesLiveData = MutableLiveData<Boolean>()
    var highlightedKeysLiveData = MutableLiveData<Set<Int>>()

    private var selectedRow = -1
    private var selectedCol = -1
    private var isTakingNotes = false

    private val board: Board

    init {
        val cells = List(9 * 9) {i -> Cell(i / 9, i % 9, 0)}

        //Next Step: Add initial starting cell values for cell templates/code to generate random game boards
        
        /*
        Test Code

        cells[0].notes = mutableSetOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
        cells[0].isStartingCell = true
        cells[0].value = 3
        */

        board = Board(9, cells)
        selectedCellLiveData.postValue(Pair(selectedRow, selectedCol))
        cellsLiveData.postValue(board.cells)
        isTakingNotesLiveData.postValue(isTakingNotes)
    }

    fun handleInput(number: Int) {
        if (selectedRow == -1 || selectedCol == -1) return

        val cell = getCell()
        //Extra check to ensure user can not change a starting cell
        if (cell.isStartingCell) return

        if (isTakingNotes) {
            if (cell.notes.contains(number)) {
                cell.notes.remove(number)
            }
            else {
                cell.notes.add(number)
            }
            highlightedKeysLiveData.postValue(cell.notes)
        }
        else {
            cell.value = number
        }

        cellsLiveData.postValue(board.cells)
    }

    fun updateSelectedCell(row: Int, col: Int) {
        val cell = board.getCell(row, col)
        //Starting cells can not be updated, while other cells can take user input
        if (!cell.isStartingCell) {
            selectedRow = row
            selectedCol = col
            selectedCellLiveData.postValue(Pair(row, col))

            //Output notes to cell if taking notes
            if (isTakingNotes) {
                highlightedKeysLiveData.postValue(cell.notes)
            }
        }
    }

    //function for changing note taking state when button is clicked
    fun changeNoteTakingState() {
        isTakingNotes = !isTakingNotes
        isTakingNotesLiveData.postValue(isTakingNotes)

        //Set current notes for the cell
        val curNotes = if (isTakingNotes){
            getCell().notes
        }
        else {
            setOf<Int>()
        }
        highlightedKeysLiveData.postValue(curNotes)
    }

    //function for deleting cell contents
    fun delete() {
        val cell = getCell()

        if (!cell.isStartingCell) {
            if (isTakingNotes) {
                cell.notes.clear()
                highlightedKeysLiveData.postValue(setOf())
            }
            else {
                cell.value = 0
            }
        }

        cellsLiveData.postValue(board.cells)
    }

    fun getCell(): Cell {
        return board.getCell(selectedRow, selectedCol)
    }
}