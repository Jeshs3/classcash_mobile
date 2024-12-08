package com.example.classcash.viewmodels.dashboard

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.graphics.Canvas
import java.io.File
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classcash.dashboardActivity.ReceiptComposable
import com.example.classcash.viewmodels.addstudent.Student
import com.example.classcash.viewmodels.addstudent.StudentRepository
import com.example.classcash.viewmodels.payment.PaymentRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.FileOutputStream

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

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _filteredStudents = MutableStateFlow<List<Student>>(emptyList())
    val filteredStudents: StateFlow<List<Student>> = _filteredStudents

    init {
        fetchStudentObjects()
        _filteredStudents.value = _studentObjects.value
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

    private suspend fun fetchStudentData() {
        studentRepository.getStudentObjectsFlow()
            .collect { students ->
                _studentObjects.emit(students) // Emit the collected data into the StateFlow
            }
    }


    fun refreshStudentObjects() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                fetchStudentData() // Collect and emit data from the flow
                _uiState.value = UiState.Success("Students refreshed successfully")
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error refreshing students: ${e.message}")
            }
        }
    }

    ///Search students
    fun searchStudents(query: String) {
        _searchQuery.value = query
        val filteredList = if (query.isBlank()) {
            _studentObjects.value // Show all students if the query is empty
        } else {
            _studentObjects.value.filter {
                it.studentName.contains(query, ignoreCase = true) // Adjust `name` to the actual property name
            }
        }
        _filteredStudents.value = filteredList
    }


    //Download report per student as png
    fun downloadReport(context: Context, studentId: Int, callback: (Boolean, String?) -> Unit) {
        val student = getStudentById(studentId)
        if (student == null) {
            callback(false, "Student not found")
            return
        }

        val bitmap = generateReportBitmap(context, student)
        val file = saveBitmapAsPng(context, bitmap, "report_${student.studentName}")

        if (file.exists()) {
            callback(true, file.absolutePath)
        } else {
            callback(false, "Failed to save report")
        }
    }

    private fun generateReportBitmap(context: Context, student: Student): Bitmap {
        // Create a ComposeView and attach it to a temporary FrameLayout
        val composeView = ComposeView(context).apply {
            setContent {
                ReceiptComposable(
                    studentName = student.studentName,
                    transactionDetails = "Sample details for ${student.studentName}",
                    totalAmount = "PHP 500" // Replace with actual amount logic
                )
            }
        }

        // You can remove the manual measure/layout calls here, instead just let Compose handle it
        val width = 1080  // Desired width
        val height = 1920 // Desired height

        // Create the bitmap
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Draw the Compose content onto the canvas (this can be done using a DrawModifier in Compose)
        composeView.draw(canvas)

        return bitmap
    }


    private fun saveBitmapAsPng(context: Context, bitmap: Bitmap, fileName: String): File {
        val directory = File(context.getExternalFilesDir(null), "Reports")
        if (!directory.exists()) directory.mkdirs()

        val file = File(directory, "$fileName.png")
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        outputStream.close()

        return file
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

    fun getStudentById(studentId: Int): Student? {
        Log.d("getStudentById", "Searching for student with ID: $studentId")
        val student = _studentObjects.value.find { it.studentId == studentId }
        if (student != null) {
            Log.d("getStudentById", "Found student: $student")
        } else {
            Log.d("getStudentById", "No student found with ID: $studentId")
        }
        return student
    }


}

