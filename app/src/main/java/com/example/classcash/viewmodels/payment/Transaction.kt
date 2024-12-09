package com.example.classcash.viewmodels.payment

data class Transaction(
    val amount: Double = 0.0,
    val purpose: String? = null,
    val source: String? = null,
    val timestamp: Long
)

