package com.example.expensetrackerapp

import com.example.expensetrackerapp.db.Expense
import com.example.expensetrackerapp.db.ExpenseDao
import com.example.expensetrackerapp.expenses.ExpenseService
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertSame
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ExpenseServiceTest {

    @Mock
    private lateinit var dao: ExpenseDao

    private lateinit var expenseService: ExpenseService

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        expenseService = ExpenseService(dao)
    }

    @Test
    fun saveExpense_invalidInputs_returnsFalse() = runBlocking {
        val result = expenseService.saveExpense(
            initialExpense = null,
            title = "",
            amountText = "abc",
            userId = null
        )

        assertFalse(result)
        verify(dao, never()).insertExpense(any())
    }
    @Test
    fun saveExpense_validInsert_callsInsertAndReturnsTrue() = runBlocking {
        val result = expenseService.saveExpense(
            initialExpense = null,
            title = "Food",
            amountText = "20",
            userId = "user123"
        )

        assertTrue(result)
        verify(dao).insertExpense(any())
    }

    @Test
    fun saveExpense_validUpdate_callsUpdateAndReturnsTrue() = runBlocking {

        val existing = Expense(
            id = 1,
            Exp_title = "Old",
            Exp_Amount = 10,
            userId = "user123"
        )

        val result = expenseService.saveExpense(
            initialExpense = existing,
            title = "New Title",
            amountText = "50",
            userId = "user123"
        )

        assertTrue(result)
        verify(dao).updateExpense(any())
    }

    @Test
    fun deleteExpense_callsDaoDelete() = runBlocking {
        val e = Expense(
            id = 0,
            userId = "user123",
            Exp_title = "Test",
            Exp_Amount = 10
        )

        expenseService.deleteExpense(e)

        verify(dao).deleteExpense(e)
    }
    @Test
    fun getUserExpenses_returnsDaoData() {
        runBlocking {

            val expectedList = listOf(
                Expense(id = 0, userId = "user123", Exp_title = "Food", Exp_Amount = 20),
                Expense(id = 0, userId = "user123", Exp_title = "Snacks", Exp_Amount = 5)
            )

            whenever(dao.getExpensesForUser("user123")).thenReturn(expectedList)

            val actual = expenseService.getUserExpenses("user123")

            assertEquals(expectedList, actual)
            assertSame(expectedList, actual)
            verify(dao).getExpensesForUser("user123")
        }
    }
}