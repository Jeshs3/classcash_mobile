package com.example.classcash.viewmodels.withdraw_externalfund

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.classcash.DateUtil

class ExternalFundViewModel : ViewModel() {

    // Simulated repository for managing class balance and transactions
    private val repository = TransactionRepository()
    val date = DateUtil.getCurrentDate()

    fun addExternalFund(transaction : Transaction): Boolean {
        // Validate the input
        if (transaction.amount <= 0 ||transaction.fundsource.isNullOrBlank()) {
            Log.d("ExternalFundViewModel","Error: Invalid input for external fund.")
            return false
        }

        // Save the external fund details
        saveExternalFund(transaction)

        return true
    }


    fun saveExternalFund(transaction : Transaction) {
        try {
            repository.saveTransaction(transaction)
            repository.updateClassBalance(repository.getClassBalance() + transaction.amount)
            refreshView()

            val message = "You added P${transaction.amount} from ${transaction.fundsource}"
            val log = "Added P${transaction.amount} for ${transaction.fundsource}"
            notify(transaction.date, message, log)

        } catch (e: Exception) {
            Log.d("ExternalFundViewModel","Error occurred while adding external funds: ${e.message}")
        }
    }

    private fun refreshView() {
        Log.d("ExternalFundViewModel", "View refreshed with updated class balance and transactions.")
    }

    private fun notify(date: String, message: String, transactionLog: String) {
        // Add a notification for the UI
        repository.addNotificationLog("Date: $date\n$message")

        // Add the transaction to logs
        repository.addTransactionLog(transactionLog)
    }
}
