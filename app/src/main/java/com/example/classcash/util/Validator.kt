package com.example.classcash.util

import android.util.Patterns

object Validator {
    fun validateRegister(name: String, email: String, password: String): String? {
        return when {
            name.isBlank() -> "Name cannot be empty."
            !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() -> "Invalid email format."
            password.length < 6 -> "Password must be at least 6 characters long."
            else -> null
        }
    }
}