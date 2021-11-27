package com.example.sudokustar.game

/*
Created By Sean Bakker
*/

class Cell(val row: Int,
    val col: Int,
    var value: String,
    var group: Int,
    var isStartingCell: Boolean = false,
    var required: Boolean = false,
    var removed: Boolean = false,
    var notes: MutableSet<String> = mutableSetOf()
)