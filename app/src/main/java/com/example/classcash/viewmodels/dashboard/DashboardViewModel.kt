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
import com.example.classcash.viewmodels.collection.CollectionRepository
import com.example.classcash.viewmodels.collection.Collection
import com.example.classcash.viewmodels.payment.PaymentRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.FileOutputStream

class DashboardViewModel(
    private val studentRepository: StudentRepository,
    private val paymentRepository: PaymentRepository,
    private val collectionRepository: CollectionRepository
) : ViewModel() {

    private val _studentObjects = MutableStateFlow<List<Student>>(emptyList())
    val studentObjects: StateFlow<List<Student>> = _studentObjects.asStateFlow()

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _filteredStudents = MutableStateFlow<List<Student>>(emptyList())
    val filteredStudents: StateFlow<List<Student>> = _filteredStudents

    private val _collection = MutableStateFlow<Collection?>(null)
    val collection: StateFlow<Collection?> = _collection

    private val _progress = MutableStateFlow<Map<Int, Int>>(emptyMap()) // Map<StudentId, Progress>
    val progress: StateFlow<Map<Int, Int>> = _progress.asStateFlow()

    init {
        Log.d("DashboardViewModel", "Initializing ViewModel...")
        fetchStudentObjects()
        _filteredStudents.value = _studentObjects.value
    }

    private fun fetchStudentObjects() {
        Log.d("DashboardViewModel", "Fetching student objects...")
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            studentRepository.getStudentObjectsFlow()
                .catch { exception ->
                    _uiState.value = UiState.Error("Error fetching students: ${exception.message}")
                    Log.e("DashboardViewModel", "Error fetching students: ${exception.message}")
                }
                .collect { students ->
                    _studentObjects.value = students
                    _uiState.value = UiState.Success("Students loaded successfully")
                    Log.d("DashboardViewModel", "Fetched students: ${students.size}")
                }
        }
    }

    private suspend fun fetchStudentData() {
        Log.d("DashboardViewModel", "Fetching student data...")
        studentRepository.getStudentObjectsFlow()
            .collect { students ->
                val updatedStudents = students.map { student ->
                    val progress = if (student.targetAmt > 0) {
                        (student.currentBal / student.targetAmt) * 100
                    } else {
                        0.0
                    }
                    student.copy(progress = progress) // Create a new Student object with updated progress
                }

                _studentObjects.emit(updatedStudents) // Emit updated data with progress
                Log.d("DashboardViewModel", "Student data fetched and emitted: ${updatedStudents.size}")
            }
    }


    fun refreshStudentObjects() {
        Log.d("DashboardViewModel", "Refreshing student objects...")
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                fetchStudentData() // Collect and emit data from the flow
                _uiState.value = UiState.Success("Students refreshed successfully")
                Log.d("DashboardViewModel", "Student objects refreshed successfully")
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error refreshing students: ${e.message}")
                Log.e("DashboardViewModel", "Error refreshing students: ${e.message}")
            }
        }
    }

    fun searchStudents(query: String) {
        Log.d("DashboardViewModel", "Searching for students with query: $query")
        _searchQuery.value = query
        val filteredList = if (query.isBlank()) {
            _studentObjects.value // Show all students if the query is empty
        } else {
            _studentObjects.value.filter {
                it.studentName.contains(query, ignoreCase = true)
            }
        }
        _filteredStudents.value = filteredList
        Log.d("DashboardViewModel", "Filtered students: ${filteredList.size}")
    }

    fun downloadReport(context: Context, studentId: Int, callback: (Boolean, String?) -> Unit) {
        Log.d("DashboardViewModel", "Downloading report for student ID: $studentId")
        val student = getStudentById(studentId)
        if (student == null) {
            callback(false, "Student not found")
            Log.e("DashboardViewModel", "Student not found for ID: $studentId")
            return
        }

        val bitmap = generateReportBitmap(context, student)
        val file = saveBitmapAsPng(context, bitmap, "report_${student.studentName}")

        if (file.exists()) {
            callback(true, file.absolutePath)
            Log.d("DashboardViewModel", "Report saved successfully at: ${file.absolutePath}")
        } else {
            callback(false, "Failed to save report")
            Log.e("DashboardViewModel", "Failed to save report for student: ${student.studentName}")
        }
    }

    private fun generateReportBitmap(context: Context, student: Student): Bitmap {
        Log.d("DashboardViewModel", "Generating report bitmap for student: ${student.studentName}")
        val composeView = ComposeView(context).apply {
            setContent {
                ReceiptComposable(
                    studentName = student.studentName,
                    transactionDetails = "Sample details for ${student.studentName}",
                    totalAmount = "PHP 500" // Replace with actual amount logic
                )
            }
        }

        val width = 1080
        val height = 1920

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        composeView.draw(canvas)

        Log.d("DashboardViewModel", "Report bitmap generated for student: ${student.studentName}")
        return bitmap
    }

    private fun saveBitmapAsPng(context: Context, bitmap: Bitmap, fileName: String): File {
        Log.d("DashboardViewModel", "Saving bitmap as PNG: $fileName")
        val directory = File(context.getExternalFilesDir(null), "Reports")
        if (!directory.exists()) directory.mkdirs()

        val file = File(directory, "$fileName.png")
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        outputStream.close()

        Log.d("DashboardViewModel", "File saved at: ${file.absolutePath}")
        return file
    }



    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        data class Success(val message: String) : UiState()
        data class Error(val message: String) : UiState()
    }

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
