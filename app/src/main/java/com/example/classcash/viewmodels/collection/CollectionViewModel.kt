package com.example.classcash.viewmodels.collection

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class CollectionViewModel(private val repository: CollectionRepository) : ViewModel() {

    // Attributes to hold current collection settings
    private val _collectionSettings = MutableLiveData<CollectionSettings>()
    val collectionSettings: LiveData<CollectionSettings> get() = _collectionSettings

    // Month details mapping and LiveData
    private val monthDetailsMap = mutableMapOf<String, Month>()
    private val _monthDetailsLiveData = MutableLiveData<List<Month>>()
    val monthDetailsLiveData: LiveData<List<Month>> get() = _monthDetailsLiveData

    private val _selectedMonthDetails = MutableLiveData<Month?>()
    val selectedMonthDetails: LiveData<Month?> get() = _selectedMonthDetails

    // Extracted data properties for the selected month
    val selectedMonthName: LiveData<String> = MediatorLiveData<String>().apply {
        addSource(_selectedMonthDetails) { month ->
            value = month?.monthName ?: ""
        }
    }

    val selectedMonthlyFund: LiveData<Double> = MediatorLiveData<Double>().apply {
        addSource(_selectedMonthDetails) { month ->
            value = month?.monthlyFund ?: 0.0
        }
    }

    val selectedActiveDays: LiveData<List<String>> = MediatorLiveData<List<String>>().apply {
        addSource(_selectedMonthDetails) { month ->
            value = month?.activeDays ?: emptyList()
        }
    }

    // Error handling
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _infoMessage = MutableLiveData<String>()
    val infoMessage: LiveData<String> get() = _infoMessage

    init {
        // Initialize default settings
        _collectionSettings.value = CollectionSettings(0, 0.0, "")
        selectedMonthName.value?.let { fetchMonthDetails(it) }
    }

    // Save the collection
    fun saveCollection() {
        val settings = _collectionSettings.value
        val selectedMonth = _selectedMonthDetails.value
        if (settings != null && selectedMonth != null) {
            val collection = Collection(
                collectionId = generateCollectionId(),
                dailyFund = settings.dailyFund,
                duration = settings.duration,
                month = selectedMonth,
                activeDays = selectedMonth.activeDays,
                monthlyFund = selectedMonth.monthlyFund
            )
            viewModelScope.launch {
                val isSuccess = repository.saveCollection(collection)
                if (!isSuccess) {
                    _errorMessage.value = "Failed to save collection"
                }
            }
        } else {
            _errorMessage.value = "Collection settings or selected month are incomplete"
        }
    }

    // Update duration
    fun updateDuration(input: String) {
        // Check if the input contains any non-digit characters except for an optional leading minus sign
        if (input.isEmpty()) {
            _errorMessage.value = "Duration cannot be empty"
            return
        }

        // Validate if input contains only digits
        val filteredInput = input.filter { it.isDigit() }
        if (filteredInput.isEmpty()) {
            _errorMessage.value = "Duration must be a valid number"
            return
        }

        // Convert the filtered input to an integer
        val durationValue = filteredInput.toIntOrNull()

        // If the duration value is null or less than or equal to zero, it's invalid
        if (durationValue == null || durationValue <= 0) {
            _errorMessage.value = "Invalid duration"
        } else {
            // Valid duration, update collection settings
            _collectionSettings.value = _collectionSettings.value?.copy(duration = durationValue)
            _errorMessage.value = "" // Clear any error message
        }
    }


    // Update daily fund
    fun updateDailyFund(input: String) {
        val filteredInput = input.filter { it.isDigit() || it == '.' }
        val dailyFundValue = filteredInput.toDoubleOrNull() ?: 0.0
        if (dailyFundValue > 0) {
            _collectionSettings.value = _collectionSettings.value?.copy(dailyFund = dailyFundValue)
        } else {
            _errorMessage.value = "Invalid daily fund"
        }
    }

    // Update the active days for a selected month
    fun updateActiveDays(monthName: String, updatedDays: List<String>) {
        val month = monthDetailsMap[monthName]
        if (month != null) {
            val updatedMonth = month.copy(activeDays = updatedDays)
            monthDetailsMap[monthName] = updatedMonth
            updateMonthDetailsLiveData()
            Log.d("ViewModel", "Updated active days for $monthName: ${updatedDays.size}")
        }
    }

    // Select a month and initialize active days
    private fun updateMonthDetailsLiveData() {
        _monthDetailsLiveData.value = monthDetailsMap.values.toList()
    }

    fun fetchMonthDetails(monthName: String) {
        viewModelScope.launch {
            val monthFromRepo = repository.fetchMonthObject(monthName)
            if (monthFromRepo != null) {
                val mappedMonth = Month(
                    monthName = monthName,
                    activeDays = monthFromRepo.activeDays,
                    monthlyFund = monthFromRepo.monthlyFund
                )
                monthDetailsMap[monthName] = mappedMonth
                updateMonthDetailsLiveData()
            }
        }
    }

    fun selectMonth(monthName: String) {
        val existingMonth = monthDetailsMap[monthName]
        if (existingMonth != null) {
            _selectedMonthDetails.value = existingMonth
            updateMonthDetailsLiveData()
        } else {
            val newMonth = Month(monthName, emptyList(), 0.0)
            monthDetailsMap[monthName] = newMonth
            _selectedMonthDetails.value = newMonth
            updateMonthDetailsLiveData()
        }
    }

    // Initialize months
    fun initializeMonths(monthNames: List<String>) {
        monthDetailsMap.clear()
        monthNames.forEach { monthName ->
            monthDetailsMap[monthName] = Month(monthName, emptyList(), 0.0)
        }
        _monthDetailsLiveData.value = monthDetailsMap.values.toList()
    }

    fun deleteCollection() {
        // Reset the collection settings to default values
        _collectionSettings.value = CollectionSettings(0, 0.0, "")

        // Optionally, clear other state values related to the collection
        _selectedMonthDetails.value = null
        _errorMessage.value = ""

        // Inform observers that the collection has been deleted
        _infoMessage.value = "Collection has been successfully deleted"
    }


    // Generate collection ID
    private fun generateCollectionId(): Int = (1..1000).random()

    fun clearMessage(){
        _errorMessage.value = ""
    }
}

