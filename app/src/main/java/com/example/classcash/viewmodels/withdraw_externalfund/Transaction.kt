package com.example.classcash.viewmodels.withdraw_externalfund

data class Transaction(
    val id: Int,
    val amount: Double,
    val date: String,
    val purpose: String? = null,
    val fundsource: String? = null
)
