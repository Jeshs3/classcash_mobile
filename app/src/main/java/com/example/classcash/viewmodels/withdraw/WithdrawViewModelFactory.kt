package com.example.classcash.viewmodels.withdraw

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class WithdrawalViewModelFactory(
    private val repository: WithdrawalRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WithdrawalViewModel::class.java)) {
            return WithdrawalViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
