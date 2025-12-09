package com.example.expensetrackerapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.expensetrackerapp.auth.LoginScreen
import com.example.expensetrackerapp.auth.RegisterScreen
//import com.example.expensetrackerapp.auth.RegisterScreen
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

    // Center container
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (currentScreen) {
            "login" -> LoginScreen(
                onLoginSuccess = { onOpenExpenseActivity() },
                onGoToRegister = { currentScreen = "register" }
            )
            "register" -> RegisterScreen(
                onGoToLogin = { currentScreen = "login" }
            )
        }
    }
}
