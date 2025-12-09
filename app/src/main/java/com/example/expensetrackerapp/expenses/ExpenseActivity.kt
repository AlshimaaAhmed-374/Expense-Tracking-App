package com.example.expensetrackerapp.expenses

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.expensetrackerapp.db.AppDatabase
import com.example.expensetrackerapp.db.Expense
import kotlinx.coroutines.launch

class ExpenseActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ExpenseScreen()
        }
    }
}

@Composable
fun ExpenseScreen() {

    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    val dao = db.expenseDao()

    val scope = rememberCoroutineScope()

    var expenses by remember { mutableStateOf(listOf<Expense>()) }

    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    var selectedExpense by remember { mutableStateOf<Expense?>(null) }

    // LOAD ALL EXPENSES
    LaunchedEffect(Unit) {
        expenses = dao.getAllExpenses()
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        // INPUTS
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // BUTTON RO
        Row {

            // ADD OR UPDATE BUTTON
            Button(
                onClick = {
                    scope.launch {

                        if (selectedExpense == null) {
                            // ADD
                            dao.insertExpense(
                                Expense(
                                    Exp_title = title,
                                    Exp_Amount = amount.toInt()
                                )
                            )
                        } else {
                            // EDIT
                            dao.updateExpense(
                                selectedExpense!!.copy(
                                    Exp_title = title,
                                    Exp_Amount = amount.toInt()
                                )
                            )
                            selectedExpense = null
                        }

                        // RESET INPUTS
                        title = ""
                        amount = ""

                        // REFRESH LIST
                        expenses = dao.getAllExpenses()
                    }
                }
            ) {
                Text(if (selectedExpense == null) "Add" else "Update")
            }

            Spacer(modifier = Modifier.width(8.dp))

            // CANCEL BUTTON
            Button(
                onClick = {
                    selectedExpense = null
                    title = ""
                    amount = ""
                }
            ) {
                Text("Cancel")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // EXPENSE LIST
        LazyColumn {
            items(expenses) { expense ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Column {
                        Text(text = expense.Exp_title)
                        Text(text = "$${expense.Exp_Amount}")
                    }

                    Row {

                        // EDIT BUTTON
                        Button(
                            onClick = {
                                selectedExpense = expense
                                title = expense.Exp_title
                                amount = expense.Exp_Amount.toString()
                            }
                        ) {
                            Text("Edit")
                        }

                        Spacer(modifier = Modifier.width(5.dp))

                        // DELETE BUTTON
                        Button(
                            onClick = {
                                scope.launch {
                                    dao.deleteExpense(expense)
                                    expenses = dao.getAllExpenses()
                                }
                            }
                        ) {
                            Text("Delete")
                        }
                    }
                }

                Divider()
            }
        }
    }
}
