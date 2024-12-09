package com.example.classcash.viewmodels.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.classcash.viewmodels.event.BudgetViewModel

class PaymentViewModelFactory(private val paymentRepository: PaymentRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PaymentViewModel::class.java)) {
            return PaymentViewModel(paymentRepository) as T
        }

        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionViewModel(paymentRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
