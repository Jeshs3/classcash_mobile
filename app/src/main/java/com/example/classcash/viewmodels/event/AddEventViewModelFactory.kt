package com.example.classcash.viewmodels.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

// AddEventViewModelFactory.kt
class AddEventViewModelFactory(private val eventRepository: EventRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddEventViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddEventViewModel(eventRepository) as T
        }

        if (modelClass.isAssignableFrom(BudgetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BudgetViewModel(eventRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

