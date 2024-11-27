package com.example.classcash.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.*

class TopScreenViewModel : ViewModel(){

    private val _currentClassName = MutableStateFlow("")
    val currentClassName: StateFlow<String> = _currentClassName

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved

    private var typingJob: Job? = null

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing

    // Call this whenever the text changes
    fun onClassNameChange(newName: String) {
        _currentClassName.value = newName
        _isSaved.value = false

        // Cancel any existing typing job and start a new one
        typingJob?.cancel()
        typingJob = viewModelScope.launch {
            delay(4000) //
            onTypingStopped()
        }
    }

    private fun onTypingStopped() {
        _isSaved.value = true
        _isEditing.value = false
    }

    fun onEditClassName() {
        _isEditing.value = true // Enable editing mode
    }

    // Optionally: Reset to saved state if you want to discard changes
    fun resetClassName() {
        _currentClassName.value = ""
        _isEditing.value = false
    }

    fun getClassroomName(): String {
        return _currentClassName.value ?: "No Classroom Found"
    }
}