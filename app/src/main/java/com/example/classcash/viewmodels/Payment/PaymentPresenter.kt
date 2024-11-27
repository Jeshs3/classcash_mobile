package com.example.classcash.viewmodels.Payment

import androidx.compose.runtime.mutableStateOf

// Presenter
class PaymentPresenter(private val paymentViewModel: PaymentViewModel) {

    var amountInput = mutableStateOf("")
    var currentBalance = mutableStateOf(0.0)
    var progressPercentage = mutableStateOf(0.0)

    fun handlePay() {
        amountInput.value = ""
    }

    fun onPayButtonClicked(name: String, amount: Double) {
        val amountValue = amountInput.value.toDoubleOrNull()
        if (amountValue != null && amountValue > 0) {
            paymentViewModel.updatePay(name, amountValue) { result ->
                if (result) {
                    refreshView(name)
                } else {
                    println("Invalid payment amount or payment update failed.")
                }
            }
        } else {
            println("Please enter a valid amount.")
        }
    }

    fun updateProgress(name: String) {
        val currentBal = paymentViewModel.getCurrentBal(name) ?: 0.0
        val targetAmount = paymentViewModel.getTargetAmount(name) ?: 100.0
        progressPercentage.value = paymentViewModel.calculateProgress(currentBal, targetAmount)
    }

    fun refreshView(name: String) {
        currentBalance.value = paymentViewModel.getCurrentBal(name) ?: 0.0
        updateProgress(name)
    }
}
