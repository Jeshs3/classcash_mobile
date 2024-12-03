package com.example.classcash.viewmodels.withdraw

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classcash.viewmodels.utilityfunctions.UtilTransactions
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class WithdrawalViewModel(
    private val repository: WithdrawalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WithdrawalUiState())
    val uiState = _uiState.asStateFlow()

    // Function to handle withdrawals
    fun withdraw(amount: Double, purpose: String) {
        viewModelScope.launch {
            if (validatePay(amount)) {
                processWithdraw(amount, purpose)
                updateClassBalance(-amount)
                notify("Withdrawal of $$amount for $purpose was successful.")
                refreshView()
            } else {
                notify("Invalid withdrawal request.")
            }
        }
    }

    fun validatePay(amount: Double): Boolean {
        try {
            // Call the utility function to validate the amount
            if (UtilTransactions.validatePay(amount)) {
                // Additional logic specific to the ViewModel
                return amount <= uiState.value.currentBalance
            }
        } catch (e: IllegalArgumentException) {
            // Handle exception if amount validation fails
            Log.e("validatePay", "Validation error: ${e.message}")
            return false
        }
        return false
    }


    // Process the withdrawal in the repository
    private suspend fun processWithdraw(amount: Double, purpose: String) {
        repository.saveWithdrawal(amount, purpose)
    }

    // Update the class balance
    private fun updateClassBalance(amountChange: Double) {
        val updatedBalance = uiState.value.currentBalance + amountChange
        _uiState.value = uiState.value.copy(currentBalance = updatedBalance)
    }

    // Refresh the UI state
    private fun refreshView() {
        // Logic to refresh the UI if needed
        _uiState.value = uiState.value.copy(refreshTrigger = true)
    }

    // Notify the user with a message
    private fun notify(message: String) {
        _uiState.value = uiState.value.copy(notificationMessage = message)
    }

    // Accepts and updates a value (can be reused for different fields)
    fun updateField(field: String, value: Any) {
        when (field) {
            "currentBalance" -> _uiState.value = uiState.value.copy(currentBalance = value as Double)
            // Add more fields as needed
        }
    }
}

// UI State Data Class
data class WithdrawalUiState(
    val currentBalance: Double = 0.0,
    val refreshTrigger: Boolean = false,
    val notificationMessage: String? = null
)
