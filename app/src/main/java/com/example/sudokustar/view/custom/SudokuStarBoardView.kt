package com.example.sudokustar.view.custom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.example.sudokustar.game.Cell
import kotlin.math.min

/*
Application Created By Sean Bakker
*/

class SudokuStarBoardView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private var sqrtSize = 3
    private var size = 9
    private var sizeFactor = 1.5F

    //Set in onDraw
    private var cellSizePixels = 0F
    private var noteSizePixels = 0F

    private var selectedRow = -1
    private var selectedCol = -1
    private var listener: OnTouchListener? = null
    private var cells: List<Cell>? = null

    private val thickLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 6F
    }

    private val thinLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 2F
    }

    private val selectedCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.parseColor("#fbdb64")
    }

    private val conflictingCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.parseColor("#efedef")
    }

    private val textPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.BLACK
    }

    private val startingCellTextPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.BLACK
        typeface = Typeface.DEFAULT_BOLD
    }

    private val startingCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.WHITE
    }

    private val noteTextPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.BLACK
    }

    //TESTING --------------------
    private val removedCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.parseColor("#ef3838")
    }

    private val addedCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.parseColor("#a5fc9d")
    }

    //Set display of screen to be bounded by the minimum value of the width/height
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val sizePixels = min(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(sizePixels, sizePixels)
    }

    override fun onDraw(canvas: Canvas) {
        updateMeasurements(width)
        fillCells(canvas)
        drawLines(canvas)
        drawText(canvas)
    }

    private fun updateMeasurements(width: Int) {
        cellSizePixels = (width / size).toFloat()
        noteSizePixels = cellSizePixels / sqrtSize.toFloat()
        noteTextPaint.textSize = cellSizePixels / sqrtSize.toFloat()
        textPaint.textSize = cellSizePixels / sizeFactor
        startingCellTextPaint.textSize = cellSizePixels / sizeFactor
    }

    private fun fillCells(canvas: Canvas) {
        cells?.forEach {
            val row = it.row
            val col = it.col

            //Cell has a starting value
            if (it.isStartingCell){
                fillCell(canvas, row, col, startingCellPaint)
            }
            //Cell is selected
            else if (row == selectedRow && col == selectedCol) {
                fillCell(canvas, row, col, selectedCellPaint)
            }
            //Cell is in same row or column as selected cell
            else if (row == selectedRow || col == selectedCol) {
                fillCell(canvas, row, col, conflictingCellPaint)
            }
            //Cell is in the same cell group as the selected cell
            else if (row / sqrtSize == selectedRow / sqrtSize && col / sqrtSize == selectedCol / sqrtSize) {
                fillCell(canvas, row, col, conflictingCellPaint)
            }
/*
            //TESTING
            if (it.required) {
                fillCell(canvas, row, col, addedCellPaint)
            }
            if (it.removed) {
                fillCell(canvas, row, col, removedCellPaint)
            }
 */
        }
    }

    //Draw rectangle from start of the selected cell to the next cell
    private fun fillCell(canvas: Canvas, row: Int, col: Int, paint: Paint) {
        canvas.drawRect(col * cellSizePixels, row * cellSizePixels, (col + 1) * cellSizePixels, (row + 1) * cellSizePixels, paint)
    }

    private fun drawLines(canvas: Canvas) {
        canvas.drawRect(0F, 0F, width.toFloat(), height.toFloat(), thickLinePaint)

        for (i in 1 until size){
            val paintToUse = when(i % sqrtSize) {
                0 -> thickLinePaint
                else -> thinLinePaint
            }

            canvas.drawLine(i * cellSizePixels, 0F, i * cellSizePixels, height.toFloat(), paintToUse)
            canvas.drawLine(0F, i * cellSizePixels, width.toFloat(), i * cellSizePixels, paintToUse)
        }
    }

    //Draw numbers to the screen
    private fun drawText(canvas: Canvas) {
        cells?.forEach { cell ->

            val value = cell.value
            val textBounds = Rect()

            if (value.toInt() == 0) {
                //draw notes
                cell.notes.forEach { note ->
                    val rowInCell = (note.toInt() - 1) / sqrtSize
                    val colInCell = (note.toInt() - 1) % sqrtSize

                    noteTextPaint.getTextBounds(note, 0, note.length, textBounds)
                    val textWidth = noteTextPaint.measureText(note)
                    val textHeight = textBounds.height()

                    canvas.drawText(note,
                            (cell.col * cellSizePixels) + (colInCell * noteSizePixels) + noteSizePixels / 2 - textWidth / 2f,
                            (cell.row * cellSizePixels) + (rowInCell * noteSizePixels) + noteSizePixels / 2 + textHeight / 2f,
                            noteTextPaint)
                }
            }
            else {
                val row = cell.row
                val col = cell.col
                val valueString = cell.value

                val paintToUse = if (cell.isStartingCell) startingCellTextPaint else textPaint
                paintToUse.getTextBounds(valueString, 0, valueString.length, textBounds)
                val textWidth = paintToUse.measureText(valueString)
                val textHeight = textBounds.height()

                canvas.drawText(valueString,
                        (col * cellSizePixels) + cellSizePixels / 2 - textWidth / 2,
                        (row * cellSizePixels) + cellSizePixels / 2 + textHeight / 2,
                        paintToUse)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                handleTouchEvent(event.x, event.y)
                true
            }
            else -> false
        }
    }

    private fun handleTouchEvent(x: Float, y: Float) {
        val possibleSelectedRow = (y / cellSizePixels).toInt()
        val possibleSelectedCol = (x / cellSizePixels).toInt()
        listener?.onCellTouched(possibleSelectedRow, possibleSelectedCol)
    }

    fun updateSelectedCellUI(row: Int, col: Int) {
        selectedRow = row
        selectedCol = col
        invalidate()
    }

    fun updateCells(cells: List<Cell>) {
        this.cells = cells
        invalidate()
    }

    fun registerListener(listener: OnTouchListener) {
        this.listener = listener
    }

    interface OnTouchListener {
        fun onCellTouched(row: Int, col: Int)
    }

}