package com.example.expensetrackerapp.ui.theme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.expensetrackerapp.db.AppDatabase
import com.example.expensetrackerapp.db.Expense
import kotlinx.coroutines.launch

class AddEditExpenseActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mode = intent.getStringExtra("mode") ?: "add"
        val expense = intent.getSerializableExtra("expense") as? Expense

        setContent {
            MaterialTheme {
                AddEditExpenseScreen(
                    initialExpense = expense,
                    isEdit = (mode == "edit"),
                    onFinish = {
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun AddEditExpenseScreen(
    initialExpense: Expense?,
    isEdit: Boolean,
    onFinish: () -> Unit
) {
    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    val dao = db.expenseDao()
    val scope = rememberCoroutineScope()

    var title by remember { mutableStateOf(initialExpense?.Exp_title ?: "") }
    var amount by remember { mutableStateOf(initialExpense?.Exp_Amount?.toString() ?: "") }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            Text(
                text = if (isEdit) "Edit Expense" else "Add Expense",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row {
                Button(
                    onClick = {
                        scope.launch {
                            val amountInt = amount.toIntOrNull()
                            if (title.isBlank() || amountInt == null) {
                                // ممكن تحطي Snackbar / Toast
                                return@launch
                            }

                            if (isEdit && initialExpense != null) {
                                val updated = initialExpense.copy(
                                    Exp_title = title,
                                    Exp_Amount = amountInt
                                )
                                dao.updateExpense(updated)
                            } else {
                                dao.insertExpense(
                                    Expense(
                                        Exp_title = title,
                                        Exp_Amount = amountInt
                                    )
                                )
                            }

                            onFinish()
                        }
                    }
                ) {
                    Text(if (isEdit) "Save changes" else "Add expense")
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedButton(onClick = { onFinish() }) {
                    Text("Cancel")
                }
            }
        }
    }
}
