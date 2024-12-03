package com.example.classcash.viewmodels.utilityfunctions

import java.text.SimpleDateFormat
import java.util.Date

object UtilTransactions {

    // Format the current date for notifications
    private fun getCurrentDate(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return formatter.format(Date())
    }

    // 1. updateClassBalance: Handle payments, withdrawals, and external funds
    fun updateClassBalance(
        currentBalance: Double,
        amount: Double,
        operation: String
    ): Double {
        return when (operation.lowercase()) {
            "payment" -> currentBalance + amount
            "withdrawal" -> {
                if (currentBalance >= amount) currentBalance - amount
                else throw IllegalArgumentException("Insufficient balance for withdrawal.")
            }
            "external" -> currentBalance + amount
            else -> throw IllegalArgumentException("Invalid operation type. Use 'payment', 'withdrawal', or 'external'.")
        }
    }

    // 2. validatePay: Ensure the amount is valid (positive and within a range)
    fun validatePay(amount: Double): Boolean {
        if (amount <= 0) {
            throw IllegalArgumentException("Amount must be greater than 0.")
        }
        return true
    }

    // 3. notify: Generate notification messages for various transactions
    fun notify(transactionType: String, details: Map<String, String>): String {
        val date = getCurrentDate()
        return when (transactionType.lowercase()) {
            "payment" -> "$date: ${details["name"]} paid ${details["amount"]} on account."
            "withdrawal" -> "$date: You withdrew ${details["amount"]} for ${details["purpose"]}."
            "external" -> "$date: You added ${details["amount"]} from ${details["source"]}."
            else -> throw IllegalArgumentException("Invalid transaction type for notification.")
        }
    }
}
