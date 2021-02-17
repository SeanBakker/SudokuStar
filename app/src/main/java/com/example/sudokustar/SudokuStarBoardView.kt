package com.example.sudokustar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class SudokuStarBoardView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private var sqrtSize = 3
    private var size = 9

    private var cellSizePixels = 0F

    private var selectedRow = -1
    private var selectedCol = -1

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
    }

    private fun fillCells(canvas: Canvas) {
        if (selectedRow == -1 || selectedCol == -1) return
        for (row in 0..size) {
            for (col in 0..size) {
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
        selectedRow = (y / cellSizePixels).toInt()
        selectedCol = (x / cellSizePixels).toInt()
        invalidate()
    }


}