package com.example.expensetrackerapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.example.expensetrackerapp.auth.LoginScreen
import com.example.expensetrackerapp.auth.RegisterScreen
import com.example.expensetrackerapp.ui.theme.HomeUIActivity

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                AuthApp(
                    onOpenHome = {
                        startActivity(
                            Intent(this, HomeUIActivity::class.java)
                        )
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun AuthApp(
    onOpenHome: () -> Unit
) {
    var currentScreen by remember { mutableStateOf("login") }

    when (currentScreen) {

        // ✅ LOGIN SCREEN
        "login" -> LoginScreen(
            onLoginSuccess = {
                onOpenHome()
            },
            onGoToRegister = {
                currentScreen = "register"
            }
        )

        // ✅ REGISTER SCREEN
        "register" -> RegisterScreen(
            onGoToLogin = {
                // RegisterScreen handles Firebase logic
                // After success snackbar → go back to login
                currentScreen = "login"
            }
        )
    }
}
