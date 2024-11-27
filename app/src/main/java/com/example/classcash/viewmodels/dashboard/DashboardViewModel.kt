package com.example.classcash.viewmodels.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classcash.viewmodels.addstudent.Student
import com.example.classcash.viewmodels.addstudent.StudentRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val studentRepository: StudentRepository
) : ViewModel() {

    // StateFlow for student objects
    private val _studentObjects = MutableStateFlow<List<Student>>(emptyList())
    val studentObjects: StateFlow<List<Student>> = _studentObjects.asStateFlow()

    init {
        fetchStudentObjects()
    }

    private fun fetchStudentObjects() {
        viewModelScope.launch {
            studentRepository.getStudentObjectsFlow()
                .collect { students ->
                    _studentObjects.value = students
                }
        }
    }

    // In DashboardViewModel
    fun refreshStudentObjects() {
        fetchStudentObjects()  // Triggering a re-fetch manually
    }

    // Example transformation logic
    fun getStudentObjects(): List<Student> = studentObjects.value
}
