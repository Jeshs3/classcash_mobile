package com.example.classcash.viewmodels.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PaymentViewModelFactory(private val repository: PaymentRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PaymentViewModel::class.java)) {
            return PaymentViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
