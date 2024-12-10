package com.example.classcash.viewmodels.collection

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.classcash.viewmodels.addstudent.StudentRepository

class CollectionViewModelFactory(
    private val collectionRepository: CollectionRepository,
    private val studentRepository: StudentRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        Log.d("FundSetupViewModelFactory", "Creating ViewModel: $modelClass")

        if (modelClass.isAssignableFrom(CollectionViewModel::class.java)) {
            Log.d("FundSetupViewModelFactory", "ViewModel created successfully")
            return CollectionViewModel(collectionRepository, studentRepository) as T
        }

        Log.e("FundSetupViewModelFactory", "Unknown ViewModel class")
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

