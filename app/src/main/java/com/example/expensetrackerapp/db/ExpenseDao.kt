package com.example.expensetrackerapp.db

import androidx.room.*
import androidx.room.Dao

@Dao
interface ExpenseDao {

    @Query("SELECT * FROM Expenses WHERE id = :id")
    suspend fun getExpenseById(id: Int): Expense

    @Insert
    suspend fun insertExpense(expense: Expense)

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("SELECT * FROM Expenses")
    suspend fun getAllExpenses(): List<Expense>
}
