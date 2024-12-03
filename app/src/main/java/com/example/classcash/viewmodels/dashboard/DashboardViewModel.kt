package com.example.classcash.viewmodels.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classcash.viewmodels.addstudent.Student
import com.example.classcash.viewmodels.addstudent.StudentRepository
import com.example.classcash.viewmodels.payment.PaymentRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
class DashboardViewModel(
    private val studentRepository: StudentRepository,
    private val paymentRepository: PaymentRepository
) : ViewModel() {

    // StateFlow for student objects, representing the UI state
    private val _studentObjects = MutableStateFlow<List<Student>>(emptyList())
    val studentObjects: StateFlow<List<Student>> = _studentObjects.asStateFlow()

    // StateFlow for managing loading and error states
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    init {
        fetchStudentObjects()
    }

    private fun fetchStudentObjects() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            studentRepository.getStudentObjectsFlow()
                .catch { exception ->
                    _uiState.value = UiState.Error("Error fetching students: ${exception.message}")
                }
                .collect { students ->
                    _studentObjects.value = students
                    _uiState.value = UiState.Success("Students loaded successfully")
                }
        }
    }

    // Function to manually trigger a refresh of the student list
    fun refreshStudentObjects() {
        fetchStudentObjects()  // Re-fetching the student data
    }

    fun refreshStudentPayment(studentId: Int, amount: Double) {
        viewModelScope.launch {
            try {
                paymentRepository.recordPayment(studentId, amount)
                refreshStudentObjects() // Refresh after recording payment
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error recording payment: ${e.message}")
            }
        }
    }
    // Helper functions to manage UI state
    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        data class Success(val message: String) : UiState()
        data class Error(val message: String) : UiState()
    }

    // If needed, expose the current list directly
    fun getStudentObjects(): List<Student> = _studentObjects.value
}

