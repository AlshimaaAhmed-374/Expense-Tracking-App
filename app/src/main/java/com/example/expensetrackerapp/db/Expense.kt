package com.example.expensetrackerapp.db


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Expenses")
data class Expense (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val Exp_title: String,
    val Exp_Amount: Int,
    )
