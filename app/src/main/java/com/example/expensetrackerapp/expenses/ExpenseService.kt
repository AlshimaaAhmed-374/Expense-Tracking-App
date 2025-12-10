package com.example.expensetrackerapp.expenses

import com.example.expensetrackerapp.db.Expense
import com.example.expensetrackerapp.db.ExpenseDao

class ExpenseService(private val dao: ExpenseDao) {

    suspend fun saveExpense(
        initialExpense: Expense?,
        title: String,
        amountText: String,
        userId: String?
    ): Boolean {

        val amount = amountText.toIntOrNull()
        if (title.isBlank() || amount == null || userId == null) {
            return false
        }

        return if (initialExpense == null) {
            dao.insertExpense(
                Expense(
                    Exp_title = title,
                    Exp_Amount = amount,
                    userId = userId
                )
            )
            true
        } else {
            val updated = initialExpense.copy(
                Exp_title = title,
                Exp_Amount = amount,
                userId = userId
            )
            dao.updateExpense(updated)
            true
        }
    }

    suspend fun deleteExpense(expense: Expense) {
        dao.deleteExpense(expense)
    }

    suspend fun getUserExpenses(userId: String): List<Expense> {
        return dao.getExpensesForUser(userId)
    }
}