package com.example.classcash.viewmodels.collection

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CollectionViewModelFactory(
    private val collectionRepository: CollectionRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        Log.d("FundSetupViewModelFactory", "Creating ViewModel: $modelClass")

        if (modelClass.isAssignableFrom(CollectionViewModel::class.java)) {
            Log.d("FundSetupViewModelFactory", "ViewModel created successfully")
            return CollectionViewModel(collectionRepository) as T
        }

        Log.e("FundSetupViewModelFactory", "Unknown ViewModel class")
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

