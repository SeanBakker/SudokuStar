package com.example.sudokustar.view.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.sudokustar.game.Cell

class SudokuStarBoardView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private var sqrtSize = 3
    private var size = 9
    private var cellSizePixels = 0F
    private var selectedRow = -1
    private var selectedCol = -1
    private var listener: SudokuStarBoardView.OnTouchListener? = null
    private var cells: List<Cell>? = null

    private val thickLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 4F
    }

    private val thinLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 2F
    }

    private val selectedCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.parseColor("#6ead3a")
    }

    private val conflictingCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.parseColor("#efedef")
    }

    private val textPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.BLACK
        textSize = 24F
    }

    //Set display of screen to be bounded by the minimum value of the width/height
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val sizePixels = Math.min(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(sizePixels, sizePixels)
    }

    override fun onDraw(canvas: Canvas) {
        cellSizePixels = (width / size).toFloat()
        fillCells(canvas)
        drawLines(canvas)
        drawText(canvas)
    }

    private fun fillCells(canvas: Canvas) {
        cells?.forEach {
            val row = it.row
            val col = it.col

            //Cell is selected
            if (row == selectedRow && col == selectedCol) {
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
        cells?.forEach {

            val row = it.row
            val col = it.col
            val valueString = it.value.toString()

            val textBounds = Rect()
            textPaint.getTextBounds(valueString, 0, valueString.length, textBounds)
            val textWidth = textPaint.measureText(valueString)
            val textHeight = textBounds.height()

            canvas.drawText(valueString, (col * cellSizePixels) + cellSizePixels / 2 - textWidth / 2, (row * cellSizePixels) + cellSizePixels / 2 - textHeight / 2, textPaint)

        }
    }

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

    fun registerListener(listener: SudokuStarBoardView.OnTouchListener) {
        this.listener = listener
    }

    interface OnTouchListener {
        fun onCellTouched(row: Int, col: Int)
    }

}