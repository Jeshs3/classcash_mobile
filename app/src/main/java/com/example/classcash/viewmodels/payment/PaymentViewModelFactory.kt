package com.example.classcash.viewmodels.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PaymentViewModelFactory(
    private val paymentRepository: PaymentRepository,
    private val sharedRepository: SharedRepository
    ) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PaymentViewModel::class.java)) {
            return PaymentViewModel(paymentRepository, sharedRepository) as T
        }

        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionViewModel(paymentRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
