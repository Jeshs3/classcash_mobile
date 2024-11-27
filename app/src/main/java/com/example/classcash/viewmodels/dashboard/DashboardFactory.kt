package com.example.classcash.viewmodels.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.classcash.viewmodels.addstudent.StudentRepository

class DashboardViewModelFactory(
    private val studentRepository: StudentRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(studentRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
