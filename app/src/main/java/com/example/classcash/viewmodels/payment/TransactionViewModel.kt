package com.example.classcash.viewmodels.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import android.util.Log


class TransactionViewModel(private val paymentRepository: PaymentRepository) : ViewModel() {

    private val _transactionResult = MutableLiveData<Result<Boolean>>()
    val transactionResult: LiveData<Result<Boolean>> = _transactionResult

    private val TAG = "TransactionViewModel"

    fun withdrawBalance(amount: Double, purpose: String, onResult: (Boolean) -> Unit) {
        Log.d(TAG, "Attempting to withdraw. Amount: $amount, Purpose: $purpose")

        viewModelScope.launch {
            try {
                if (amount <= 0) {
                    Log.e(TAG, "Error: Amount is not greater than zero.")
                    _transactionResult.value = Result.failure(Exception("Amount must be greater than zero"))
                    return@launch
                }

                Log.d(TAG, "Fetching current balance...")
                val currentBalance = paymentRepository.getClassBalance()
                Log.d(TAG, "Current Balance: $currentBalance")

                if (currentBalance == null || amount > currentBalance) {
                    Log.e(TAG, "Error: Insufficient balance. Requested amount: $amount, Current balance: $currentBalance")
                    _transactionResult.value = Result.failure(Exception("Insufficient balance"))
                    return@launch
                }

                Log.d(TAG, "Proceeding with withdrawal...")
                val success = paymentRepository.processWithdrawal(purpose, amount)
                if (success) {
                    Log.d(TAG, "Withdrawal successful.")
                    _transactionResult.value = Result.success(true)
                } else {
                    Log.e(TAG, "Error: Failed to process withdrawal.")
                    _transactionResult.value = Result.failure(Exception("Failed to process withdrawal"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error during withdrawal: ${e.message}", e)
                _transactionResult.value = Result.failure(e)
            }
        }
    }

    fun addExternalFund(amount: Double, source: String, onResult: (Boolean) -> Unit) {
        Log.d(TAG, "Attempting to add external fund. Amount: $amount, Source: $source")

        viewModelScope.launch {
            try {
                if (amount <= 0) {
                    Log.e(TAG, "Error: Amount is not greater than zero.")
                    _transactionResult.value = Result.failure(Exception("Amount must be greater than zero"))
                    return@launch
                }

                Log.d(TAG, "Fetching current balance before adding external fund...")
                val currentBalance = paymentRepository.getClassBalance()
                Log.d(TAG, "Current Balance: $currentBalance")

                if (currentBalance == null) {
                    Log.e(TAG, "Error: Unable to fetch current balance.")
                    _transactionResult.value = Result.failure(Exception("Failed to fetch current balance"))
                    return@launch
                }

                Log.d(TAG, "Proceeding with adding external fund...")
                val success = paymentRepository.updateClassBalance(amount) // Add the fund to the class balance
                if (success) {
                    Log.d(TAG, "External fund added successfully.")
                    _transactionResult.value = Result.success(true)
                } else {
                    Log.e(TAG, "Error: Failed to add external fund.")
                    _transactionResult.value = Result.failure(Exception("Failed to process adding funds"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error during adding external fund: ${e.message}", e)
                _transactionResult.value = Result.failure(e)
            }
        }
    }
}

