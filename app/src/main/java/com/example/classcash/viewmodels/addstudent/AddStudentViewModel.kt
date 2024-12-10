package com.example.classcash.viewmodels.addstudent

import android.util.Log
import androidx.lifecycle.*
import com.example.classcash.viewmodels.collection.CollectionRepository
import com.example.classcash.viewmodels.collection.Collection
import com.example.classcash.viewmodels.dashboard.DashboardViewModel
import com.example.classcash.viewmodels.payment.PaymentRepository
import com.opencsv.CSVReader
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.FileReader

class AddStudentViewModel(
    private val studentRepository: StudentRepository,
    private val paymentRepository: PaymentRepository,
    private val collectionRepository: CollectionRepository,
    private val dashboardViewModel: DashboardViewModel
) : ViewModel() {

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        data class Success(val message: String) : UiState()
        data class Error(val message: String) : UiState()
    }
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    private val _collection = MutableLiveData<Collection>()
    val collection: LiveData<Collection> get() = _collection

    private val _studentObjects = MutableStateFlow<List<Student>>(emptyList())
    val studentObjects: StateFlow<List<Student>> = _studentObjects.asStateFlow()

    private val _studentNames = MutableStateFlow<List<String>>(listOf())
    val studentNames: StateFlow<List<String>> = _studentNames

    private val _inputState = MutableStateFlow("")
    val inputState: StateFlow<String> = _inputState

    private val _classSize = MutableStateFlow(0)
    val classSize: StateFlow<Int> = _classSize

    private val _classBalance = MutableStateFlow(0.0)
    val classBalance: StateFlow<Double> = _classBalance

    val monthlyFund: MediatorLiveData<Double> = MediatorLiveData()
    private var isMonthlyFundObserverAdded = false

    init {
        fetchStudentNames()
    }

    fun addStudent(studentName: String) {
        if (studentName.isBlank()) {
            _uiState.value = UiState.Error("Student name cannot be blank")
            return
        }
        if (_studentObjects.value.any { it.studentName == studentName }) {
            _uiState.value = UiState.Error("Duplicate student name")
            return
        }

        _uiState.value = UiState.Loading

        val newStudentId = generateNewStudentId()
        val newStudent = Student(
            studentId = newStudentId,
            studentName = studentName,
            currentBal = 0.0,
            targetAmt = 0.0 // Temporary value, will be updated later
        )

        viewModelScope.launch {
            try {
                studentRepository.saveStudent(newStudent) // Save the new student
                _studentObjects.value = _studentObjects.value + newStudent


                _uiState.value = UiState.Success("$studentName added successfully")
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error adding student: ${e.message}")
            }
        }
    }




    fun updateClassSize() {
        _classSize.value = _studentNames.value.distinct().count { it.isNotBlank() }
        Log.d("AddStudentViewModel", "Class size updated: ${_classSize.value}")
    }

    fun clearInputFields() {
        _studentNames.value = listOf("") // Reset to a single blank field
        _inputState.value = ""           // Clear the input state
        updateClassSize()                // Update the class size to reflect changes
    }


    fun updateInput(input: String) {
        _inputState.value = input
    }

    fun saveAll(selectedMonth: String) {
        if (_studentNames.value.isEmpty()) {
            _uiState.value = UiState.Error("No students to save")
            return
        }

        viewModelScope.launch {
            _uiState.value = UiState.Loading

            try {
                val monthlyFundValue = collectionRepository.getMonthlyFund(selectedMonth)
                if (monthlyFundValue <= 0) {
                    _uiState.value = UiState.Error("Invalid monthly fund value")
                    return@launch
                }

                // Prepare student objects
                val students = _studentNames.value
                    .filter { it.isNotBlank() }
                    .distinct()
                    .mapIndexed { index, name ->
                        Student(
                            studentId = index + 1,
                            studentName = name,
                            targetAmt = monthlyFundValue
                        )
                    }

                // Save students in batch
                val result = studentRepository.saveStudentsBatch(students)
                if (result.isSuccess) {
                    _uiState.value = UiState.Success("All students added and updated successfully")
                    Log.d("AddStudentViewModel", "Students successfully saved")
                } else {
                    throw result.exceptionOrNull() ?: IllegalArgumentException("Unknown error occurred")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Failed to save students: ${e.message}")
                Log.e("AddStudentViewModel", "Error saving students", e)
            }
        }
    }


    fun removeStudent(index: Int) {
        _studentNames.value = _studentNames.value.filterIndexed { i, _ -> i != index }
        updateClassSize()
    }

    fun deleteClass(clearFields: Boolean = true) {
        performActionWithUiState {
            val result = studentRepository.deleteAllStudents()
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
            studentRepository.fetchStudentNames()
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
