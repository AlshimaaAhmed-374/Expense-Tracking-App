package com.example.expensetrackerapp.ui.theme

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import com.example.expensetrackerapp.db.AppDatabase
import com.example.expensetrackerapp.db.Expense
import com.example.expensetrackerapp.expenses.ExpenseService
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class HomeUIActivity : ComponentActivity() {

    // refresh key to reload list after add/edit
    private val refreshKey = mutableStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                ExpenseHomeScreenContainer(refreshKey.value)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        refreshKey.value++
    }
}

@Composable
fun ExpenseHomeScreenContainer(refreshKey: Int) {

    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    val dao = db.expenseDao()
    val manager = ExpenseService(dao)

    val scope = rememberCoroutineScope()

    // ✅ Firebase user id
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    var expenses by remember { mutableStateOf(listOf<Expense>()) }

    // Delete dialog
    var showDeleteDialog by remember { mutableStateOf(false) }
    var expenseToDelete by remember { mutableStateOf<Expense?>(null) }

    // ✅ Load ONLY the logged-in user's expenses
    LaunchedEffect(refreshKey, uid) {
        if (uid != null) {
            expenses = manager.getUserExpenses(uid)
        }
    }

    ExpenseHomeScreen(
        expenses = expenses,

        onAddClick = {
            val intent = Intent(context, AddEditExpenseActivity::class.java).apply {
                putExtra("mode", "add")
            }
            context.startActivity(intent)
        },

        onEditClick = { expense ->
            val intent = Intent(context, AddEditExpenseActivity::class.java).apply {
                putExtra("mode", "edit")
                putExtra("expense", expense)
            }
            context.startActivity(intent)
        },

        onDeleteClick = { expense ->
            expenseToDelete = expense
            showDeleteDialog = true
        }
    )

    // Delete dialog
    if (showDeleteDialog && expenseToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete expense") },
            text = { Text("Are you sure you want to delete this expense?") },

            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            manager.deleteExpense(expenseToDelete!!)

                            // ✅ Reload user expenses after delete
                            if (uid != null) {
                                expenses = manager.getUserExpenses(uid)
                            }

                            showDeleteDialog = false
                            expenseToDelete = null
                        }
                    }
                ) {
                    Text("Delete")
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        expenseToDelete = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ExpenseHomeScreen(
    expenses: List<Expense>,
    onAddClick: () -> Unit,
    onEditClick: (Expense) -> Unit,
    onDeleteClick: (Expense) -> Unit
) {

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Text("+")
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            Text(
                text = "Your Expenses",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (expenses.isEmpty()) {

                Text(
                    text = "No expenses yet.\nStart by adding your first expense!",
                    style = MaterialTheme.typography.bodyLarge
                )

            } else {

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
                                Text(text = "Amount: ${expense.Exp_Amount}")
                            }

                            Row {

                                IconButton(onClick = { onEditClick(expense) }) {
                                    Icon(
                                        imageVector = Icons.Filled.Edit,
                                        contentDescription = "Edit"
                                    )
                                }

                                IconButton(onClick = { onDeleteClick(expense) }) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = "Delete"
                                    )
                                }
                            }
                        }

                        Divider()
                    }
                }
            }
        }
    }
}
