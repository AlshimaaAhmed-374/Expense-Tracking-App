package com.example.expensetrackerapp.db

import androidx.room.*
import androidx.room.Dao

@Dao
interface ExpenseDao {

    @Insert
    suspend fun insertExpense(expense: Expense)

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("SELECT * FROM Expenses")
    suspend fun getAllExpenses(): List<Expense>
}
