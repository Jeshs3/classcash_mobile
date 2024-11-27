package com.example.classcash.viewmodels.addstudent

import androidx.lifecycle.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AddStudentViewModel(private val repository: StudentRepository) : ViewModel() {

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        data class Success(val message: String) : UiState()
        data class Error(val message: String) : UiState()
    }
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    private val _studentNames = MutableStateFlow<List<String>>(listOf())
    val studentNames: StateFlow<List<String>> = _studentNames

    private val _inputState = MutableStateFlow("")
    val inputState: StateFlow<String> = _inputState

    private val _classSize = MutableStateFlow(0)
    val classSize: StateFlow<Int> = _classSize

    init {
        fetchStudentNames()
    }

    fun updateStudentName(studentId: Int, newName: String) {
        _studentNames.value = _studentNames.value.mapIndexed { i, name ->
            if (i == studentId) newName else name
        }
    }

    fun addStudent(name: String) {
        if (name.isBlank()) return
        if (_studentNames.value.contains(name)) {
            _uiState.value = UiState.Error("Duplicate student name")
            return
        }

        performActionWithUiState {
            val newStudentId = generateNewStudentId() // Example function to generate IDs
            val result = repository.saveStudentName(newStudentId, name)
            if (result.isSuccess) {
                _studentNames.value = _studentNames.value + name
                updateClassSize()
                UiState.Success("$name added successfully")
            } else {
                UiState.Error("Failed to add student: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun updateClassSize() {
        _classSize.value = _studentNames.value.distinct().count { it.isNotBlank() }
    }

    fun clearStudentName() {
        _studentNames.value = listOf() // Reset the list of student names
        updateClassSize()  // Reset class size as well
    }

    fun clearInputFields() {
        _studentNames.value = listOf("") // Reset to a single blank field
        _inputState.value = ""           // Clear the input state
        updateClassSize()                // Update the class size to reflect changes
    }


    fun updateInput(input: String) {
        _inputState.value = input
    }


    fun saveAll() {
        if (_studentNames.value.isEmpty()) return

        performActionWithUiState {
            val students = _studentNames.value
                .filter { it.isNotBlank() }
                .distinct()
                .mapIndexed { index, name ->
                    index + 1 to name // Generate student ID
                }

            val result = repository.saveStudentsBatch(students)
            if (result.isSuccess) {
                UiState.Success("All students added successfully")
            } else {
                UiState.Error("Failed to add students: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun removeStudent(index: Int) {
        _studentNames.value = _studentNames.value.filterIndexed { i, _ -> i != index }
        updateClassSize()
    }

    fun addNewStudent() {
        _studentNames.value = _studentNames.value + "" // Add an empty string (a new student entry)
    }

    fun updateInputState(newValue: String) {
        _inputState.value = newValue
    }

    fun deleteClass(clearFields: Boolean = true) {
        performActionWithUiState {
            val result = repository.deleteAllStudents()
            if (result.isSuccess) {
                if (clearFields) _studentNames.value = listOf()
                updateClassSize()
                UiState.Success("All students deleted successfully")
            } else {
                UiState.Error("Failed to delete students: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    private fun fetchStudentNames() {
        viewModelScope.launch {
            repository.fetchStudentNames()
                .catch { exception ->
                    _uiState.value = UiState.Error("Failed to fetch student names: ${exception.message}")
                }
                .collect { studentMap ->
                    _studentNames.value = studentMap.values.toList()
                    updateClassSize()
                }
        }
    }

    private fun performActionWithUiState(action: suspend () -> UiState) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _uiState.value = action()
        }
    }
    private fun generateNewStudentId(): Int {
        return (_studentNames.value.size + 1) // Example: Incremental ID
    }
}
