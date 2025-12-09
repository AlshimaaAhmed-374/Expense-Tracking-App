package com.example.expensetrackerapp.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

object FirebaseAuthHelper {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    fun loginUser(
        email: String,
        password: String,
        onResult: (success: Boolean, user: FirebaseUser?, error: String?) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, auth.currentUser, null)
                } else {
                    onResult(false, null, task.exception?.localizedMessage)
                }
            }
    }
    fun registerUser(
        email: String,
        password: String,
        onResult: (success: Boolean, user: FirebaseUser?, error: String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, auth.currentUser, null)
                } else {
                    onResult(false, null, task.exception?.localizedMessage)
                }
            }
    }

}
