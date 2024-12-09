package com.example.classcash.viewmodels.addstudent

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.classcash.viewmodels.payment.PaymentRepository

class AddStudentViewModelFactory(
    private val studentRepository: StudentRepository,
    private val paymentRepository: PaymentRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        Log.d("AddStudentViewModelFactory", "Creating ViewModel: $modelClass")
        if (modelClass.isAssignableFrom(AddStudentViewModel::class.java)) {
            Log.d("AddStudentViewModelFactory", "ViewModel created successfully")
            return AddStudentViewModel(studentRepository, paymentRepository) as T
        }
        Log.e("AddStudentViewModelFactory", "Unknown ViewModel class")
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
