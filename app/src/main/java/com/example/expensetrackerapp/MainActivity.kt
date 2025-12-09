package com.example.expensetrackerapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.material3.*
import com.example.expensetrackerapp.auth.LoginScreen
import com.example.expensetrackerapp.auth.RegisterScreen
import com.example.expensetrackerapp.expenses.ExpenseActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AuthApp(
                    onOpenExpenseActivity = {
                        val intent = Intent(this, ExpenseActivity::class.java)
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun AuthApp(
    onOpenExpenseActivity: () -> Unit
) {
    var currentScreen by remember { mutableStateOf("login") }

    when (currentScreen) {
        "login" -> LoginScreen(
            onLoginClick = { email, password ->
                onOpenExpenseActivity()
            },
            onGoToRegister = { currentScreen = "register" }
        )

        "register" -> RegisterScreen(
            onRegisterClick = { email, password ->
                onOpenExpenseActivity()
            },
            onGoToLogin = { currentScreen = "login" }
        )
    }
}
