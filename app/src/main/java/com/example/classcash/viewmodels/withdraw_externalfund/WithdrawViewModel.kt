package com.example.classcash.viewmodels.withdraw_externalfund

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.classcash.DateUtil


class WithdrawViewModel : ViewModel() {

    private val repository = TransactionRepository()
    val date = DateUtil.getCurrentDate()

    fun withdraw(transaction: Transaction): Boolean {
        val totalBal = repository.getClassBalance()
        Log.d("WithdrawalViewModel", "Attempting withdrawal: ID=$transaction.id, Amount=$transaction.amount, Purpose=$transaction.purpose, TotalBalance=$totalBal")
        return if (transaction.amount > 0 && transaction.amount <= totalBal) {
            saveWithdrawal(transaction)
            Log.d("WithdrawViewModel","Withdrawal successful.")
            true
        } else {
            Log.d("WithdrawViewModel","Withdrawal failed: Insufficient balance or invalid amount.")
            false
        }
    }

    fun saveWithdrawal(transaction: Transaction) {
        try {

            repository.saveTransaction(transaction)
            repository.updateClassBalance(repository.getClassBalance() - transaction.amount)
            refreshView()

            notify(transaction.date, "You withdrew P${transaction.amount} for ${transaction.purpose}")
            logTransaction("Withdrew P${transaction.amount} for ${transaction.purpose}")
        } catch (e: Exception) {
            Log.d("WithdrawViewModel","Error occurred while saving withdrawal: ${e.message}")
        }
    }

    private fun refreshView() {
        Log.d("WithdrawViewModel","View refreshed with the latest class balance and transactions.")
    }

    private fun notify(date: String, message: String) {
        repository.addNotificationLog("Date: $date\n$message")
    }

    private fun logTransaction(transactionLog: String) {
        repository.addTransactionLog(transactionLog)
    }

}

