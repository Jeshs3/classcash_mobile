package com.example.classcash.viewmodels.collection


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import android.util.Log
import com.example.classcash.viewmodels.addstudent.Student
import com.example.classcash.viewmodels.addstudent.StudentRepository
import com.example.classcash.viewmodels.payment.SharedRepository
import kotlinx.coroutines.tasks.await


class CollectionViewModel(
    private val collectionRepository: CollectionRepository,
    private val studentRepository : StudentRepository
) : ViewModel() {

    private val sharedRepository = SharedRepository()

    private val _collection = MutableLiveData<Collection>()
    val collection: LiveData<Collection> get() = _collection

    private val monthDetailsMap = mutableMapOf<String, List<String>>() // Maps month name to active days
    private val _monthDetails = MutableLiveData<Map<String, List<String>>>()
    val monthDetails: LiveData<Map<String, List<String>>> get() = _monthDetails

    private val _monthlyFund = MutableLiveData<Double?>(0.0)
    val monthlyFund: LiveData<Double?> get() = _monthlyFund

    private val _selectedMonth = MutableLiveData<String>("")
    val selectedMonth: LiveData<String> = _selectedMonth

    private val _students = MutableLiveData<List<Student>>()
    val students: LiveData<List<Student>> get() = _students

    val targetAmt = sharedRepository.targetAmt


    private val _message = MutableLiveData<MessageType>()
    val message: LiveData<MessageType> get() = _message


    sealed class MessageType {
        data class Error(val message: String) : MessageType()
        data class Info(val message: String) : MessageType()
    }


    fun updateMonthlyFund(monthlyFund: Double) {
        _monthlyFund.value = monthlyFund
    }


    fun updateDuration(input: String) {
        Log.d("CollectionViewModel", "Updating duration: $input")
        val durationValue = input.toIntOrNull()
        if (durationValue != null && durationValue > 0) {
            _collection.value = _collection.value?.copy(duration = durationValue)
        } else {
            handleError("Invalid duration")
        }
    }

    fun updateDailyFund(input: String) {
        Log.d("CollectionViewModel", "Updating daily fund: $input")
        val dailyFundValue = input.toDoubleOrNull()
        if (dailyFundValue != null && dailyFundValue > 0) {
            _collection.value = _collection.value?.copy(dailyFund = dailyFundValue)
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

    private fun handleError(message: String) {
        Log.e("CollectionViewModel", "Error: $message")
        _message.value = MessageType.Error(message)
    }

    private suspend fun updateStudentsTarget(monthlyFund: Double) {
        Log.d("CollectionViewModel", "Updating students' targetAmt to $monthlyFund...")

        try {
            val students = studentRepository.getAllStudents() // Ensure this method exists

            val updatedStudents = students.map { student ->
                student.copy(targetAmt = monthlyFund)
            }

            // Step 3: Update students in a batch operation
            val result = studentRepository.updateStudentsBatch(updatedStudents)
            if (result.isSuccess) {
                Log.d("CollectionViewModel", "Updated targetAmt for all students successfully")
            } else {
                handleError("Failed to update students' targetAmt")
            }
        } catch (e: Exception) {
            Log.e("CollectionViewModel", "Error updating students' targetAmt", e)
            handleError("Error updating students' targetAmt")
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
                    updateStudentsTarget(settings.monthlyFund)
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
