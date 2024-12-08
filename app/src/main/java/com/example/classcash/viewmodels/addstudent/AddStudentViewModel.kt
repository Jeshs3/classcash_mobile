package com.example.classcash.viewmodels.addstudent

import android.util.Log
import androidx.lifecycle.*
import com.example.classcash.viewmodels.payment.PaymentRepository
import com.opencsv.CSVReader
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.FileReader

class AddStudentViewModel(
    private val repository: StudentRepository,
    private val paymentRepository: PaymentRepository
) : ViewModel() {

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

    private val _classBalance = MutableStateFlow(0.0)
    val classBalance: StateFlow<Double> = _classBalance

    init {
        fetchStudentNames()
    }

    fun updateStudentName(studentId: Int, newName: String) {
        _studentNames.value = _studentNames.value.mapIndexed { i, name ->
            if (i == studentId) newName else name
        }
    }

    fun addStudent(studentName: String) {
        if (studentName.isBlank()) return
        if (_studentNames.value.contains(studentName)) {
            _uiState.value = UiState.Error("Duplicate student name")
            return
        }

        performActionWithUiState {
            val newStudentId = generateNewStudentId()
            val newStudent = StudentWarehouse.createStudent(
                studentId = newStudentId,
                studentName = studentName
            )
            val result = repository.saveStudent(newStudent)
            if (result.isSuccess) {
                _studentNames.value = (_studentNames.value + studentName).distinct()
                updateClassSize()
                UiState.Success("$studentName added successfully")
            } else {
                UiState.Error("Failed to add student: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun updateClassSize() {
        _classSize.value = _studentNames.value.distinct().count { it.isNotBlank() }
        Log.d("AddStudentViewModel", "Class size updated: ${_classSize.value}")
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
                    Student(
                        studentId = index + 1,// Generate student ID as a string
                        studentName = name
                    )
                }
            Log.d("AddStudentViewModel", "Saving all students: $students")

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
                resetClassBalance()
                UiState.Success("All students deleted successfully")
            } else {
                UiState.Error("Failed to delete students: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun import(filePath: String) {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading

                // Read the CSV file
                val reader = CSVReader(FileReader(filePath))
                val records = reader.readAll()

                // Process each row and add student names
                val importedStudents = records.drop(1) // Skip the header
                    .mapNotNull { row ->
                        if (row.isNotEmpty() && row[0].isNotBlank()) row[0].trim() else null
                    }

                // Update the student names, avoiding duplicates
                val uniqueStudents = (importedStudents + _studentNames.value).distinct()
                _studentNames.value = uniqueStudents
                updateClassSize()

                _uiState.value = UiState.Success("Students imported successfully")
                Log.d("AddStudentViewModel", "Imported students: $importedStudents")

            } catch (e: Exception) {
                _uiState.value = UiState.Error("Failed to import students: ${e.message}")
                Log.e("AddStudentViewModel", "Error importing students", e)
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

    // Assuming PaymentRepository has a method to fetch the balance
    fun fetchClassBalance() {
        viewModelScope.launch {
            val balance = paymentRepository.getClassBalance() ?: 0.0
            _classBalance.value = balance
            Log.d("AddStudentViewModel", "Fetched class balance: $balance")
        }
    }

    private fun resetClassBalance() {
        viewModelScope.launch {
            val success = paymentRepository.deleteClassBalance()
            if (success) {
                _classBalance.value = 0.0
                Log.d("resetClassBalanceAfterDelete", "Class balance reset successfully after student deletion")
            } else {
                Log.e("resetClassBalanceAfterDelete", "Failed to reset class balance after student deletion")
            }
        }
    }
}
