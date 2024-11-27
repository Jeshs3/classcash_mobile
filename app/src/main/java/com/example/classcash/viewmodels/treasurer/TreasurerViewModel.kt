package com.example.classcash.viewmodels.treasurer

import android.util.Patterns
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.State
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel : ViewModel() {

    var name = mutableStateOf("")
    var email = mutableStateOf("")
    var password = mutableStateOf("")

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> get() = _errorMessage

    private val _successMessage = mutableStateOf<String?>(null)
    val successMessage: State<String?> get() = _successMessage

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Utility functions for validation
    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 6
    }

    private fun validateRegister(): String? {
        return when {
            name.value.isBlank() -> "Name cannot be empty."
            email.value.isBlank() || !isEmailValid(email.value) -> "Invalid email format."
            password.value.isBlank() || !isPasswordValid(password.value) -> "Password must be at least 6 characters long."
            else -> null
        }
    }

    // Registration logic
    fun register(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val validationError = validateRegister()
        if (validationError != null) {
            _errorMessage.value = validationError
            return
        }

        auth.createUserWithEmailAndPassword(email.value, password.value)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        FirebaseFirestore.getInstance().collection("users").document(userId)
                            .set(mapOf("name" to name.value, "email" to email.value))
                            .addOnSuccessListener {
                                _successMessage.value = "Registration successful. Welcome!"
                                login(onSuccess, onFailure) // Automatically log in after registration
                            }
                            .addOnFailureListener { e ->
                                _errorMessage.value = e.message ?: "Failed to store user information."
                            }
                    } else {
                        _errorMessage.value = "Failed to get user ID."
                    }
                } else {
                    _errorMessage.value = when (task.exception) {
                        is FirebaseAuthUserCollisionException -> "Email already exists. Please login instead."
                        else -> task.exception?.message ?: "Registration failed!"
                    }
                }
            }
    }

    // Login logic
    fun login(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        auth.signInWithEmailAndPassword(email.value, password.value)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _successMessage.value = "Login successful!"
                    onSuccess()
                } else {
                    val error = when (val exception = task.exception) {
                        is FirebaseAuthInvalidUserException -> "User does not exist. Please register."
                        is FirebaseAuthInvalidCredentialsException -> "Invalid password. Please try again."
                        else -> exception?.message ?: "Login failed due to an unknown error."
                    }
                    _errorMessage.value = error
                    onFailure(error)
                }
            }
    }

    // Logout logic
    fun logout() {
        auth.signOut()
        clearInputFields()
        clearMessages()
    }

    // Input field change handlers
    fun onNameChange(newName: String) {
        name.value = newName
    }

    fun onEmailChange(newEmail: String) {
        email.value = newEmail.trim()
    }

    fun onPasswordChange(newPassword: String) {
        password.value = newPassword
    }

    // Utility to retrieve treasurer's name
    fun getTreasurerName(): String {
        return if (name.value.isBlank()) "No Name Found" else name.value
    }

    // Clear both error and success messages
    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }

    // Clear all input fields
    private fun clearInputFields() {
        name.value = ""
        email.value = ""
        password.value = ""
    }
}
