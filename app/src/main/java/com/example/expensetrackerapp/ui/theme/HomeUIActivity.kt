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
import kotlinx.coroutines.launch

class HomeUIActivity : ComponentActivity() {

    // علشان نعمل refresh لما نرجع من Add/Edit
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
        // كل مرة نرجع للأكتيفيتي دي نزود key → يعيد تحميل البيانات
        refreshKey.value++
    }
}

@Composable
fun ExpenseHomeScreenContainer(refreshKey: Int) {
    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    val dao = db.expenseDao()
    val scope = rememberCoroutineScope()

    var expenses by remember { mutableStateOf(listOf<Expense>()) }

    // delete dialog state
    var showDeleteDialog by remember { mutableStateOf(false) }
    var expenseToDelete by remember { mutableStateOf<Expense?>(null) }

    // كل ما refreshKey يتغير نعيد تحميل البيانات
    LaunchedEffect(refreshKey) {
        expenses = dao.getAllExpenses()
    }

    ExpenseHomeScreen(
        expenses = expenses,
        onAddClick = {
            // فتح Activity الإضافة
            val intent = Intent(context, AddEditExpenseActivity::class.java).apply {
                putExtra("mode", "add")
            }
            context.startActivity(intent)
        },
        onEditClick = { expense ->
            // فتح Activity التعديل وإرسال الـ Expense
            val intent = Intent(context, AddEditExpenseActivity::class.java).apply {
                putExtra("mode", "edit")
                putExtra("expense", expense) // Serializable
            }
            context.startActivity(intent)
        },
        onDeleteClick = { expense ->
            expenseToDelete = expense
            showDeleteDialog = true
        }
    )

    // AlertDialog للحذف
    if (showDeleteDialog && expenseToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete expense") },
            text = { Text("Are you sure you want to delete this expense?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            dao.deleteExpense(expenseToDelete!!)
                            expenses = dao.getAllExpenses()
                            showDeleteDialog = false
                            expenseToDelete = null
                        }
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    expenseToDelete = null
                }) {
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
                // عبارة ترحيبية لو مفيش بيانات
                Text(
                    text = "No expenses yet.\nStart by adding your first expense!",
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                // لست بالمصاريف
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
