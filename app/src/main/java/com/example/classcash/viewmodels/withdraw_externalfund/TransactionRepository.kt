package com.example.classcash.viewmodels.withdraw_externalfund

import android.util.Log
import com.example.classcash.DateUtil

class TransactionRepository {

    private var classBalance: Double = 0.00
    private val transactionLogs = mutableListOf<String>()
    private val notificationLogs = mutableListOf<String>()
    val date = DateUtil.getCurrentDate()

    fun getClassBalance(): Double {
        return classBalance
    }

    fun updateClassBalance(newBalance: Double) {
        classBalance = newBalance
    }

    fun saveTransaction(transaction: Transaction) {
        transactionLogs.add(
            "Transaction - Amount: $${transaction.amount}, Purpose: ${transaction.purpose}, Date: ${transaction.date}"
        )
        Log.d("TransactionRepository","Transaction saved: $transaction")
    }


    fun addTransactionLog(log: String) {
        transactionLogs.add(log)
    }

    fun addNotificationLog(notification: String) {
        notificationLogs.add(notification)
    }

    fun getTransactionLogs(): List<String> = transactionLogs.toList()

    fun getNotificationLogs(): List<String> = notificationLogs.toList()

    fun clearTransactionLogs() {
        transactionLogs.clear()
    }

    fun clearNotificationLogs() {
        notificationLogs.clear()
    }

    private fun formatTransactionLog(amount: Double, purpose: String): String {
        return "Transaction - Amount: $$amount, Purpose: $purpose, Date: ${date}"
    }

}