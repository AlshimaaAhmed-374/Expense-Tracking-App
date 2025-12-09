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
                        val intent = Intent(this, HomeUIActivity::class.java)
                        startActivity(intent)
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
        "login" -> LoginScreen(
            onLoginClick = { email, password ->
                // TODO: Validation / Auth
                onOpenHome()
            },
            onGoToRegister = { currentScreen = "register" }
        )

        "register" -> RegisterScreen(
            onRegisterClick = { email, password ->
                // TODO: Register logic
                onOpenHome()
            },
            onGoToLogin = { currentScreen = "login" }
        )
    }
}
