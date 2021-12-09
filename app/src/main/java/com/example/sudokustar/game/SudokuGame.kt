package com.example.sudokustar.game

import android.util.Log
import androidx.lifecycle.MutableLiveData

/*
Application Created By Sean Bakker
*/

class SudokuGame : SudokuApplication() {

    var selectedCellLiveData = MutableLiveData<Pair<Int, Int>>()
    var cellsLiveData = MutableLiveData<List<Cell>>()
    var isTakingNotesLiveData = MutableLiveData<Boolean>()
    var highlightedKeysLiveData = MutableLiveData<Set<String>>()

    private var selectedRow = 0
    private var selectedCol = 0
    private val group1 = 3
    private val group2 = 6
    private val group3 = 9
    private val startPos = 0
    private val endPos = 81
    private val empty = '0'
    private val DEFAULT_CELL = "123456789"
    var isTakingNotes = false
    private var boardComplete = false
    private var boardDifficulty = 0

    private lateinit var board: Board
    private val cells = List(9 * 9) { i -> Cell(i / 9, i % 9, DEFAULT_CELL, 0) }
    private val cellsRecover = List(9 * 9) { i -> Cell(i / 9, i % 9, DEFAULT_CELL, 0) }
    private val cellsAnswer = List(9 * 9) { i -> Cell(i / 9, i % 9, DEFAULT_CELL, 0) }

    init {

        var colNum = 1
        var rowNum = 1
        var groupValue = 1

        //Loop to set group values for each cell
        for (pos in startPos until endPos) {

            //Set cells default to starting cell (not editable)
            cells[pos].isStartingCell = true
            cellsAnswer[pos].isStartingCell = true
            cellsRecover[pos].isStartingCell = true

            //Assign group value to cell
            when {
                colNum <= group1 -> {
                    cells[pos].group = groupValue
                    cellsRecover[pos].group = groupValue
                    cellsAnswer[pos].group = groupValue
                }
                colNum <= group2 -> {
                    cells[pos].group = groupValue + 1
                    cellsRecover[pos].group = groupValue + 1
                    cellsAnswer[pos].group = groupValue + 1
                }
                else -> {
                    cells[pos].group = groupValue + 2
                    cellsRecover[pos].group = groupValue + 2
                    cellsAnswer[pos].group = groupValue + 2
                }
            }

            //Update colNum and rowNum after iterations
            if (colNum == group3) {
                if (rowNum == group1 || rowNum == group2 || rowNum == group3) {
                    groupValue += group1
                }
                rowNum++
                colNum =  1
            }
            else {
                colNum++
            }
        }
        resetBoard()
    }

    //Function to set/reset board after each game
    private fun resetBoard() {

        for (pos in startPos until endPos) {
            cells[pos].value = DEFAULT_CELL
            cellsRecover[pos].value = DEFAULT_CELL
            cellsAnswer[pos].value = DEFAULT_CELL
        }

        //Variables
        val invalid = "000000000"
        val resetTime = 100000000

        var pos = startPos
        var num = 0
        var check = 12
        var posToCheck = group1
        var invalidPos = 0
        var startTimer = true
        var beginTime = System.nanoTime()
        var endTime: Long

        var frequency: Int
        var tempNum: Int
        var startRow: Int

        boardDifficulty = getDifficulty()
        boardComplete = false

        //Loop over cell elements
        while (pos < endPos) {
            tempNum = 0
            startRow = pos

            do {
                //Invalid puzzle needs recovery
                if (cells[pos].value == invalid) {

                    //Start timer
                    if (startTimer) {
                        beginTime = System.nanoTime()
                        startTimer = false

                        invalidPos = if (pos < group1 * group3) {
                            group1 * group3
                        } else if (pos >= group1 * group3 && pos < group2 * group3) {
                            group2 * group3
                        } else {
                            endPos - 1
                        }
                    }

                    if (pos >= invalidPos) {
                        //Reset invalid timer
                        beginTime = System.nanoTime()
                        startTimer = true
                    }

                    //Check time on timer
                    endTime = System.nanoTime()

                    //Reset entire board and generate new board
                    if (endTime - beginTime > resetTime) {
                        resetBoard()
                    }

                    if (!cells[endPos - 1].value.contains(empty)) {
                        pos = endPos - 1
                    }
                    else {
                        //Condition to find the beginning of the group row (pos = 0/27/54)
                        startRow = if (startRow < group1 * group3) {
                            startPos
                        } else if (startRow >= group1 * group3 && startRow < group2 * group3) {
                            group1 * group3
                        } else {
                            group2 * group3
                        }

                        //Reset position to recovery position
                        pos = startRow
                        check = pos + group1
                        posToCheck = group1

                        //Recover all cells after current group row when puzzle is INVALID
                        for (count in startRow until endPos step 1) {
                            cells[count].value = cellsRecover[count].value
                        }
                    }
                }

                //Check for 1 possible number in a cell
                frequency = 0
                if (cells[pos].value.length > 1) {
                    for (count in startPos until group3) {
                        if (cells[pos].value[count] == empty) {
                            frequency++
                        }
                        else {
                            num = cells[pos].value[count].toString().toInt()
                        }
                    }

                    //Select random number for cell if there are more than 1 possible values
                    if (frequency != group3 - 1) {
                        num = (1..9).shuffled().first()
                    }
                    else {
                        tempNum = num
                    }

                    //Optimize value that is chosen randomly by checking options in further cells
                    if (pos >= check && pos <= check + 4) {
                        for (num2 in 1 until group3 step 1) {
                            if (cells[pos].value.contains(num2.toString()) && !cells[pos + posToCheck].value.contains(num2.toString())) {
                                //Add randomizer for temp num that is chosen
                                do {
                                    val randomNum = (1..9).shuffled().first()
                                    tempNum = randomNum
                                } while (!cells[pos].value.contains(randomNum.toString()) || cells[pos + posToCheck].value.contains(randomNum.toString()))
                            }
                        }
                        if (tempNum != 0) {
                            num = tempNum
                        }

                        //Update checking algorithm for optimizing cell numbers
                        if (pos == check + 2) {
                            posToCheck = 1
                        }
                        else if (pos == check + 4) {
                            posToCheck = group1
                            check += 9
                        }
                    }
                }
                else {
                    num = cells[pos].value.toInt()
                    tempNum = num
                }

            } while (!cells[pos].value.contains(num.toString()) && tempNum != num)

            //If the cell contains num as a substring, then we set the value to num
            cells[pos].value = num.toString()

            if (pos != endPos - 1) {
                removeOptions(pos, num, cells)
            }
            else {
                boardComplete = true
            }

            pos++

            //Check for new group row (every 3 rows) and backup cell data
            if (pos == group1 * group3 || pos == group2 * group3 || pos == group3 * group3) {
                for (count in startPos until endPos step 1) {
                    cellsRecover[count].value = cells[count].value
                    cellsAnswer[count].value = cells[count].value
                }
            }
        }

        if (boardComplete) {
            removeCells()

            //Post data to the game board
            board = Board(group3, cells)
            selectedCellLiveData.postValue(Pair(selectedRow, selectedCol))
            cellsLiveData.postValue(board.cells)
            isTakingNotesLiveData.postValue(isTakingNotes)
        }
        else {
            //This case should be an error
            Log.i("RESETTING BOARD ERROR", "----------------------")
            resetBoard()
        }
    }

    //Function to remove options for numbers in cells after adding a number to a cell when creating the board
    private fun removeOptions(pos: Int, num: Int, cells: List<Cell>) {

        var startRow = pos
        var startCol = pos

        //Find starting position in current row (first row is divisible by 9)
        while (startRow % group3 != startPos) {
            startRow--
        }

        //Loop over row cells and remove options for num in the row
        for (count in startPos until group3) {
            if (cells[startRow + count].value.contains(num.toString()) && cells[startRow + count].value.length > 1) {
                cells[startRow + count].value = cells[startRow + count].value.replace(num.toString(), empty.toString())
            }
        }

        //Find the starting position of the current column
        while (startCol - group3 >= startPos) {
            startCol -= group3
        }

        //Loop over column cells and remove options for num in the column
        for (count in startPos until endPos step group3) {
            if (cells[startCol + count].value.contains(num.toString()) && cells[startCol + count].value.length > 1) {
                cells[startCol + count].value = cells[startCol + count].value.replace(num.toString(), empty.toString())
            }
        }

        //Remove option for num in group
        for (pos2 in startPos until endPos) {
            if (cells[pos2].value.toInt() != num && cells[pos2].group == cells[pos].group) {
                cells[pos2].value = cells[pos2].value.replace(num.toString(), empty.toString())
            }
        }
    }

    //Function to add options for numbers in cells after removing a number in a cell when creating the board
    private fun addOptions(pos: Int, num: Int, cells: List<Cell>) {

        var startRow = pos
        var startCol = pos
        var newValue: String

        //Find starting position in current row (first row is divisible by 9)
        while (startRow % group3 != startPos) {
            startRow--
        }

        //Loop over row cells and add options for num in the row
        for (count in startPos until group3) {
            if (!cells[startRow + count].value.contains(num.toString()) && cells[startRow + count].value.length > 1) {
                newValue = cells[startRow + count].value.substring(0, num - 1).plus(num).plus(cells[startRow + count].value.substring(num))
                cells[startRow + count].value = newValue
            }
        }

        //Find the starting position of the current column
        while (startCol - group3 >= startPos) {
            startCol -= group3
        }

        //Loop over column cells and add options for num in the column
        for (count in startPos until endPos step group3) {
            if (!cells[startCol + count].value.contains(num.toString()) && cells[startCol + count].value.length > 1) {
                newValue = cells[startCol + count].value.substring(0, num - 1).plus(num).plus(cells[startCol + count].value.substring(num))
                cells[startCol + count].value = newValue
            }
        }

        //Add option for num in group
        for (pos2 in startPos until endPos) {
            if (!cells[pos2].value.contains(num.toString()) && cells[pos2].group == cells[pos].group && cells[pos2].value.length > 1) {
                newValue = cells[pos2].value.substring(0, num - 1).plus(num).plus(cells[pos2].value.substring(num))
                cells[pos2].value = newValue
            }
        }
    }

    //Function to remove cells from board after creating the game board
    private fun removeCells() {

        //Variables
        var pos: Int

        //Check difficulty
        var cellsToRemove: Int = when (boardDifficulty) {
            0 -> {
                35
            }
            1 -> {
                (30..33).shuffled().first()
            }
            else -> {
                (26..28).shuffled().first()
                //(79..80).shuffled().first() TESTING - finish a game board to check submit button results
            }
        }

        //Set the number of cells to remove
        cellsToRemove = endPos - cellsToRemove

        for (count in startPos until cellsToRemove) {
            //Randomly remove cells from the board
            do {
                pos = (startPos until endPos).shuffled().first()
            }
            while(cells[pos].value == empty.toString())

            cells[pos].value = empty.toString()
            cells[pos].isStartingCell = false

            //cellsRecover[pos].isStartingCell = false

            cellsAnswer[pos].value = DEFAULT_CELL
            cellsAnswer[pos].isStartingCell = false
        }

        //Remove invalid cell possibilities in cellsAnswer list
        for (count in startPos until endPos) {
            if (cellsAnswer[count].isStartingCell) {
                removeOptions(count, cellsAnswer[count].value.toInt(), cellsAnswer)
            }
        }

        //Solve board after removing cells
        solveBoard()

        for (count in startPos until endPos) {

            //TESTING - FINISH BOARDS AFTER CREATING NEW BOARD
            //cells[count].value = cellsAnswer[count].value
            cells[count].required = cellsAnswer[count].required
            cells[count].removed = cellsAnswer[count].removed

            if (cellsAnswer[count].isStartingCell) {
                cells[count].value = cellsAnswer[count].value
                cells[count].isStartingCell = true
            }
            else {
                cells[count].isStartingCell = false
                cells[count].value = empty.toString()
            }
        }
    }

    fun handleInput(number: String) {
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
            highlightedKeysLiveData.postValue(setOf(cell.value))
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
            else {
                highlightedKeysLiveData.postValue(setOf(cell.value))
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
            setOf(getCell().value)
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
                cell.value = "0"
                highlightedKeysLiveData.postValue(setOf(cell.value))
            }
        }

        cellsLiveData.postValue(board.cells)
    }

    private fun getCell(): Cell {
        return board.getCell(selectedRow, selectedCol)
    }

    fun getCells(): List<Cell> {
        return cells
    }

    fun recoverCells() {

        //Reset all cell values to only starting cells & remove notes from cells
        for (pos in startPos until endPos) {
            cells[pos].notes.clear()

            if (!cells[pos].isStartingCell) {
                cells[pos].value = empty.toString()
            }
        }

        //Post values to board
        cellsLiveData.postValue(board.cells)
    }

    //Solve board and confirm board is solvable using Sudoku techniques
    private fun solveBoard() {

        var complete = false
        var win = false
        var changedCells = false
        var full: Boolean
        var tempNum = "0"
        var tempPos: Int
        var noMoreMoves: Boolean
        var numList = List(group3) { Cell(startPos, startPos, empty.toString(), startPos) }

        //Check for full and complete board
        full = checkFull(cellsAnswer)

        if (full) {
            win = checkWin(cellsAnswer)
        }

        if (win) {
            complete = true
        }

        while (!complete) {
            noMoreMoves = true

            for (pos in startPos until endPos) {
                //Check for any cells with only one possible option and input that number
                if (!cellsAnswer[pos].isStartingCell && checkChar(pos, cellsAnswer)) {
                    for (count in startPos until group3) {
                        if (cellsAnswer[pos].value[count] != empty) {
                            tempNum = cellsAnswer[pos].value[count].toString()
                        }
                    }
                    cellsAnswer[pos].value = tempNum
                    noMoreMoves = false

                    //Remove options from other cells after inputting that value
                    removeOptions(pos, tempNum.toInt(), cellsAnswer)
                }

                //Check groups for 1 possible number - Loop over all groups on the board
                for (group in startPos + 1..group3) {
                    //Loop over cells in each group
                    for (pos2 in startPos until endPos) {
                        if (cellsAnswer[pos2].group == group) {
                            numList = recordCellChars(pos2, numList)
                        }
                    }

                    //If any frequencies are 1 after all cells in the group have been checked, then that number can be inputted
                    for (count in startPos until group3) {
                        if (numList[count].value == "1") {
                            for (pos2 in startPos until endPos) {
                                if (cellsAnswer[pos].group == group) {
                                    noMoreMoves = checkFrequencyAndInsertNumber(pos2, count, noMoreMoves)
                                }
                            }
                        }
                    }
                    //Reset numList
                    numList = resetList(numList)
                }

                //Check rows for 1 possible number - Loop over all 9 rows on the board
                var startRow = 0
                var pos2: Int

                for (row in startPos until group3) {
                    //Loop over cells in each row
                    for (col in startPos until group3) {
                        pos2 = startRow + col
                        numList = recordCellChars(pos2, numList)
                    }

                    //If any frequencies are 1 after all cells in the row have been checked, then that number can be inputted
                    for (count in startPos until group3) {
                        if (numList[count].value == "1") {
                            //Loop over cells in row
                            for (col in startPos until group3) {
                                pos2 = startRow + col
                                noMoreMoves = checkFrequencyAndInsertNumber(pos2, count, noMoreMoves)
                            }
                        }
                    }
                    //Reset numList
                    numList = resetList(numList)
                    startRow += group3
                }

                //Check columns for 1 possible number - Loop over all 9 columns on the board
                for (startCol in startPos until group3) {
                    //Loop over cells in each column
                    for (row in startPos until endPos step group3) {
                        pos2 = startCol + row
                        numList = recordCellChars(pos2, numList)
                    }

                    //If any frequencies are 1 after all cells in the column have been checked, then that number can be inputted
                    for (count in startPos until group3) {
                        if (numList[count].value == "1") {
                            //Loop over cells in column
                            for (row in startPos until endPos step group3) {
                                pos2 = startCol + row
                                noMoreMoves = checkFrequencyAndInsertNumber(pos2, count, noMoreMoves)
                            }
                        }
                    }
                    //Reset numList
                    numList = resetList(numList)
                }

                //Add advanced checking technique if the board is unfinished but there are no single numbers left
                //Other harder techniques may be added later (x-wing, y-wing, etc)
                //Also add check for when notes restrict certain numbers to a row or col (within a group) this can clear notes in other cells
            }

            //Check for full and complete board
            full = checkFull(cellsAnswer)

            if (full) {
                win = checkWin(cellsAnswer)
            }

            if (win) {
                complete = true
            }
            else if (noMoreMoves){
                //When there are no more cells with one possibility we exit the while loop
                complete = true
            }
        }
        var pos = startPos

        while (!win) {
            //Check remaining board for unsolved cells
            while (cellsAnswer[pos].value.length > 1) {

                do {
                    val randomNum = (1..9).shuffled().first()
                    tempNum = randomNum.toString()
                } while (!cellsAnswer[pos].value.contains(randomNum.toString()) || cellsAnswer[pos].removed)

                cellsAnswer[pos].value = tempNum
                cellsAnswer[pos].isStartingCell = true
                cellsAnswer[pos].required = true

                //Remove options from other cells after inputting that value
                removeOptions(pos, cellsAnswer[pos].value.toInt(), cellsAnswer)

                //Remove another cell on the board to accommodate for the newly added one
                do {
                    val randomNum = (startPos until endPos).shuffled().first()
                    tempPos = randomNum
                } while (!cellsAnswer[randomNum].isStartingCell || cellsAnswer[randomNum].required)

                //Remove cell from starting cells
                tempNum = cellsAnswer[tempPos].value
                cellsAnswer[tempPos].value = DEFAULT_CELL
                cellsAnswer[tempPos].isStartingCell = false
                cellsAnswer[tempPos].removed = true

                //Add options for num on board
                addOptions(tempPos, tempNum.toInt(), cellsAnswer)

                //After adding options - loop over entire board and remove options to confirm no conflicts
                for (pos2 in startPos until endPos) {
                    if (cellsAnswer[pos2].value.length == 1) {
                        removeOptions(pos2, cellsAnswer[pos2].value.toInt(), cellsAnswer)
                    }
                }

                //Remove options from new default cell
                var startRow = tempPos
                var startCol = tempPos

                //Find starting position in current row (first row is divisible by 9)
                while (startRow % group3 != startPos) {
                    startRow--
                }

                //Loop over the row and remove options
                for (count in startPos until group3) {
                    if (cellsAnswer[startRow + count].value.length == 1) {
                        removeOptions(startRow + count, cellsAnswer[startRow + count].value.toInt(), cellsAnswer)
                    }
                }

                //Find the starting position of the current column
                while (startCol - group3 >= startPos) {
                    startCol -= group3
                }

                //Loop over the column and remove options
                for (count in startPos until endPos step group3) {
                    if (cellsAnswer[startCol + count].value.length == 1) {
                        removeOptions(startCol + count, cellsAnswer[startCol + count].value.toInt(), cellsAnswer)
                    }
                }

                //Loop over group cells and remove options
                for (pos2 in startPos until endPos) {
                    if (cellsAnswer[pos2].group == cellsAnswer[tempPos].group && cellsAnswer[pos2].value.length == 1) {
                        removeOptions(pos2, cellsAnswer[pos2].value.toInt(), cellsAnswer)
                    }
                }
                changedCells = true
            }

            if (changedCells) {
                //Solve board again - needs to return win=true to move forward
                solveBoard()
                win = true //used for recursive cases to exit the while loop
            }
            //Eventually we may need a manual reset as the board may not be possible to solve
            else if (pos >= endPos - 1 && !win) {
                //resetBoard()
                win = true
            }
            else {
                pos++
            }
        }
    }

    //Function to check for a full game board
    fun checkFull(cells: List<Cell>): Boolean {
        //Loop over all board cells and check for cells that are not full (have more than one character)
        for (pos in startPos until endPos) {
            if (cells[pos].value.length > 1 || cells[pos].value.contains(empty)) {
                return false
            }
        }
        return true
    }

    //Function to check for winning game board
    fun checkWin(cells: List<Cell>): Boolean {
        val checkCells = List(group3 * group3) { i -> Cell(i / group3, i % group3, DEFAULT_CELL, startPos) }

        //Loop over all board cells and assign group values
        for (pos in startPos until endPos) {
            checkCells[pos].group = cells[pos].group
        }

        //Loop over all board cells and check for winning board
        for (pos in startPos until endPos) {
            //Check for any duplicates and return false when duplicate is found
            if (checkCells[pos].value.contains(cells[pos].value)) {
                checkCells[pos].value = cells[pos].value
                removeOptions(pos, checkCells[pos].value.toInt(), checkCells)
            }
            else {
                return false
            }
        }
        return true
    }

    //Checks cell characters and returns true when only one possible option is available
    private fun checkChar(pos: Int, cells: List<Cell>): Boolean {
        var frequency = 0

        return if (cells[pos].value.length > 1) {
            for (count in startPos until group3) {
                if (cells[pos].value[count] != empty) {
                    frequency++
                }
            }
            frequency <= 1
        }
        else {
            //Returns false when frequency=0, cell already has a value in it
            false
        }
    }

    //Loops over cell characters and records the frequency of each character into numList
    private fun recordCellChars(pos: Int, numList: List<Cell> ): List<Cell> {
        if (cellsAnswer[pos].value.length > 1) {
            //Loop over cell value characters
            for (count in startPos until group3) {
                if (cellsAnswer[pos].value[count] != empty) {
                    numList[(cellsAnswer[pos].value[count]).toString().toInt() - 1].value = (numList[(cellsAnswer[pos].value[count]).toString().toInt() - 1].value.toInt() + 1).toString()
                }
            }
        }
        return numList
    }

    //If any frequencies are 1 after all cells in the group/row/col have been checked, then that number can be inputted
    private fun checkFrequencyAndInsertNumber(pos: Int, count: Int, noMoreMoves: Boolean): Boolean {
        var noMoves = noMoreMoves

        if (cellsAnswer[pos].value.length > 1) {
            if (cellsAnswer[pos].value.contains((count + 1).toString())) {

                cellsAnswer[pos].value = (count + 1).toString()
                noMoves = false

                //Remove options from other cells after inputting that value
                removeOptions(pos, cellsAnswer[pos].value.toInt(), cellsAnswer)
            }
        }
        return noMoves
    }

    private fun resetList(list: List<Cell>): List<Cell> {
        for (count in startPos until group3) {
            list[count].value = empty.toString()
        }
        return list
    }

}
