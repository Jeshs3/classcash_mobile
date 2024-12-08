package com.example.classcash.viewmodels.collection


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import android.util.Log
import kotlinx.coroutines.flow.catch


class CollectionViewModel(private val collectionRepository: CollectionRepository) : ViewModel() {

    private val _collection = MutableLiveData<Collection>()
    val collection: LiveData<Collection> get() = _collection

    private val monthDetailsMap = mutableMapOf<String, List<String>>() // Maps month name to active days
    private val _monthDetails = MutableLiveData<Map<String, List<String>>>()
    val monthDetails: LiveData<Map<String, List<String>>> get() = _monthDetails

    private val _selectedMonthName = MutableLiveData<String?>()
    val selectedMonthName: LiveData<String?> get() = _selectedMonthName

    private val _message = MutableLiveData<MessageType>()
    val message: LiveData<MessageType> get() = _message

    sealed class MessageType {
        data class Error(val message: String) : MessageType()
        data class Info(val message: String) : MessageType()
    }


    init {
        Log.d("CollectionViewModel", "Initializing ViewModel...")
        fetchCollectionSettings()
    }


    fun updateDuration(input: String) {
        Log.d("CollectionViewModel", "Updating duration: $input")
        val durationValue = input.toIntOrNull()
        if (durationValue != null && durationValue > 0) {
            _collection.value = _collection.value?.copy(duration = durationValue)
            saveData()
        } else {
            handleError("Invalid duration")
        }
    }

    fun updateDailyFund(input: String) {
        Log.d("CollectionViewModel", "Updating daily fund: $input")
        val dailyFundValue = input.toDoubleOrNull()
        if (dailyFundValue != null && dailyFundValue > 0) {
            _collection.value = _collection.value?.copy(dailyFund = dailyFundValue)
            saveData()
        } else {
            handleError("Invalid daily fund")
        }
    }

    fun updateSelectedMonth(month: String, selectedDays: List<String>) {
        Log.d("CollectionViewModel", "Updating selected month: $month with active days: $selectedDays")

        // Update the main collection state
        _collection.value = _collection.value?.copy(monthName = month) ?: Collection(monthName = month)

        // Safely handle nullable _monthDetails.value
        val newMonthDetails = (_monthDetails.value?.toMutableMap() ?: mutableMapOf()).apply {
            this[month] = selectedDays
        }
        _monthDetails.value = newMonthDetails

        Log.d("CollectionViewModel", "Updated month details: $newMonthDetails")

        // Call editActiveDays if needed
        editActiveDays(month, selectedDays)
        saveData()
    }

    fun editActiveDays(monthName: String, selectedDays: List<String>) {
        Log.d("CollectionViewModel", "Editing active days for $monthName: $selectedDays")

        // Get current active days for the selected month
        val activeDays = monthDetailsMap[monthName] ?: emptyList()

        // Update active days (either add or remove the selected days)
        val updatedActiveDays = activeDays.toMutableList()

        selectedDays.forEach { selectedDay ->
            if (updatedActiveDays.contains(selectedDay)) {
                updatedActiveDays.remove(selectedDay) // Remove the day if it exists
            } else {
                updatedActiveDays.add(selectedDay) // Add the day if it doesn't exist
            }
        }

        // Update the collection with the modified active days
        updateActiveDays(monthName, updatedActiveDays)

        // Also update the monthDetailsMap to ensure consistency
        monthDetailsMap[monthName] = updatedActiveDays

        // Update _monthDetails LiveData to reflect the new state
        _monthDetails.value = monthDetailsMap
    }

    fun updateActiveDays(monthName: String, updatedDays: List<String>) {
        Log.d("CollectionViewModel", "Updating active days for $monthName: $updatedDays")

        // Update the activeDays and monthlyFund in the collection object
        _collection.value = _collection.value?.let { currentCollection ->
            if (currentCollection.monthName == monthName) {
                currentCollection.copy(
                    activeDays = updatedDays,
                    monthlyFund = updatedDays.size * (currentCollection.dailyFund ?: 0.0)
                )
            } else {
                currentCollection
            }
        }
    }

    // Function to get active days for a specific month
    fun getActiveDaysForMonth(month: String): List<String>? {
        val currentMonthDetails = _monthDetails.value ?: return null
        return currentMonthDetails[month]
    }

    fun updateMonthlyFund(selectedMonth: String) {
        val dailyFund = collection.value?.dailyFund ?: 0.0
        val activeDays = monthDetails.value?.get(selectedMonth)?.size ?: 0
        val updatedFund = dailyFund * activeDays
        _collection.value = collection.value?.copy(monthlyFund = updatedFund)
    }

    private fun handleError(message: String) {
        Log.e("CollectionViewModel", "Error: $message")
        _message.value = MessageType.Error(message)
    }

    fun fetchCollectionSettings() {
        Log.d("CollectionViewModel", "Fetching collection settings...")
        viewModelScope.launch {
            collectionRepository.fetchCollectionSettings()
                .catch { e ->
                    // Handle errors in fetching collection settings
                    handleError("Failed to load collection: ${e.message}")
                }
                .collect { collection ->
                    _collection.value = collection // Update LiveData with fetched collection
                    Log.d("CollectionViewModel", "Fetched collection settings: ${_collection.value}")
                }
        }
    }

    private fun saveData() {
        Log.d("CollectionViewModel", "Saving collection data...")
        val collectionData = _collection.value
        if (collectionData != null) {
            viewModelScope.launch {
                try {
                    val isSuccess = collectionRepository.saveCollection(collectionData)
                    if (isSuccess) {
                        Log.d("CollectionViewModel", "Collection data saved successfully to Firestore.")
                    } else {
                        handleError("Failed to save collection data.")
                    }
                } catch (e: Exception) {
                    handleError("Failed to save collection data: ${e.message}")
                }
            }
        } else {
            handleError("Collection data is null, cannot save.")
        }
    }

    fun saveCollection() {
        Log.d("CollectionViewModel", "Saving collection...")
        val settings = _collection.value
        if (settings != null) {
            viewModelScope.launch {
                val isSuccess = collectionRepository.saveCollection(settings)
                if (isSuccess) {
                    Log.d("CollectionViewModel", "Collection saved successfully")
                } else {
                    handleError("Failed to save collection")
                }
            }
        } else {
            handleError("Collection settings are incomplete")
        }
    }

    fun deleteCollection(collectionId: Int) {
        viewModelScope.launch {
            try {
                Log.d("CollectionViewModel", "Initiating deletion for collection ID: $collectionId")

                // Attempt to delete the collection using the repository function
                val result = collectionRepository.deleteCollectionSettings()

                result.onSuccess {
                    // If deletion is successful, reset the local state
                    _collection.value = _collection.value?.copy(
                        monthName = "",  // Clear the month name
                        activeDays = emptyList(),  // Clear the list of active days
                        dailyFund = 0.0,  // Reset the daily fund to zero
                        duration = 0,
                        monthlyFund = 0.0  // Reset the monthly fund to zero
                    )

                    Log.d("CollectionViewModel", "Successfully deleted collection data.")
                }.onFailure { exception ->
                    Log.e("CollectionViewModel", "Failed to delete collection from the repository.", exception)
                    handleError("Failed to delete collection. Please try again.")
                }
            } catch (e: Exception) {
                Log.e("CollectionViewModel", "Error occurred while deleting collection", e)
                handleError("An error occurred while deleting the collection.")
            }
        }
    }



    fun clearMessage() {
        Log.d("CollectionViewModel", "Clearing message")
        _message.value = MessageType.Info("No message")
    }
}
