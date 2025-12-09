package com.example.expensetrackerapp.db


import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "Expenses")
data class Expense (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,

    val Exp_title: String,
    val Exp_Amount: Int,
    ) : Serializable
