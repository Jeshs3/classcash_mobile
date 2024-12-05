package com.example.classcash.viewmodels.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.classcash.viewmodels.addstudent.Student

class NotificationsViewModelFactory(
    private val students: List<Student>,
    private val withdrawalLogs: List<Student.TransactionLog>,
    private val sinkingFundTarget: Double,
    private val activeDaysOfMonth: Int
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationsViewModel::class.java)) {
            val repository = NotificationsRepository(
                students = students,
                withdrawalLogs = withdrawalLogs,
                sinkingFundTarget = sinkingFundTarget,
                activeDaysOfMonth = activeDaysOfMonth
            )
            return NotificationsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
