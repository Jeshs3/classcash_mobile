package com.example.classcash.viewmodels.payment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classcash.viewmodels.addstudent.Student
import kotlinx.coroutines.launch


class PaymentViewModel(private val paymentRepository: PaymentRepository) : ViewModel() {

    // LiveData for observing the current student's balance
    private val _studentBalance = MutableLiveData<Double>()
    val studentBalance: LiveData<Double> get() = _studentBalance

    // LiveData for observing the total class balance
    private val _classBalance = MutableLiveData<Double>()
    val classBalance: LiveData<Double> get() = _classBalance

    private val _studentData = MutableLiveData<Student?>()
    val studentData: LiveData<Student?> get() = _studentData

    private var currentStudent: Student? = null

    private val studentCache = mutableMapOf<Int, Student>()

    // Initialize the balances when the ViewModel is created
    init {
        fetchClassBalance()
    }

    // Fetch the current class balance from the repository
    private fun fetchClassBalance() {
        viewModelScope.launch {
            try {
                val balance = paymentRepository.getClassBalance() ?: 0.0
                _classBalance.value = balance
                Log.d("PaymentViewModel", "Fetched class balance: $balance")
            } catch (e: Exception) {
                Log.e("PaymentViewModel", "Error fetching class balance", e)
                _classBalance.value = 0.0
            }
        }
    }

    // Fetch student data from the database
    fun fetchStudentData(studentId: Int, onResult: (Student?) -> Unit) {
        Log.d("fetchStudentData", "Fetching student data for studentId=$studentId")
        if (studentId <= 0) {
            Log.e("fetchStudentData", "Invalid student ID: $studentId")
            onResult(null)
            return
        }

        viewModelScope.launch {
            try {
                val student = paymentRepository.getStudentById(studentId)
                Log.d("fetchStudentData", "Fetched student: ${student?.studentName ?: "No student found"}")
                _studentData.postValue(student)
                _studentBalance.postValue(student?.currentBal ?: 0.0)
                currentStudent = student
                onResult(student)
            } catch (e: Exception) {
                Log.e("fetchStudentData", "Error fetching student", e)
                _studentData.postValue(null)
                onResult(null)
            }
        }
    }

    // 2. Record a payment for the student
    fun recordPay(student: Student, amount: Double, onResult: (Boolean) -> Unit) {
        Log.d("recordPay", "Called with studentId=${student.studentId}, amount=$amount")

        // Validate student object and payment amount
        if (amount <= 0.0 || !validatePay(amount)) {
            Log.e("recordPay", "Invalid payment amount: $amount")
            onResult(false)
            return
        }

        // Proceed with payment if validations pass
        viewModelScope.launch {
            try {
                Log.d("recordPay", "Attempting to record payment for studentId=${student.studentId}, amount=$amount")
                val result = paymentRepository.recordPayment(student.studentId, amount)
                Log.d("recordPay", "Record payment result: $result")
                if (result) {
                    val updatedTransaction = Student.TransactionLog(amount, getCurrentDate())
                    student.currentBal += amount
                    Log.d("recordPay", "New balance for student ${student.studentId}: ${student.currentBal}")
                    _studentBalance.value = student.currentBal
                    student.transactionLogs += updatedTransaction
                    updateStudentObject { success ->
                        Log.d("recordPay", "Update student result: $success")
                    }
                    updateClassBalance(amount)
                }
                onResult(result)
            } catch (e: Exception) {
                Log.e("recordPay", "Error recording payment for studentId=${student.studentId}", e)
                onResult(false)
            }
        }
    }

    // Process the payment for the student
    fun processPayment(studentId: Int, amount: String, description: String = "Payment", onResult: (Boolean) -> Unit) {
        Log.d("processPayment", "processPayment called with studentId=$studentId, amount=$amount")

        // Trim and validate the amount
        val sanitizedAmount = amount.trim()
        val amountDouble = sanitizedAmount.toDoubleOrNull()

        if (sanitizedAmount.isEmpty() || amountDouble == null || amountDouble <= 0.0 || !validatePay(amountDouble)) {
            Log.d("processPayment", "Invalid amount: $sanitizedAmount")
            onResult(false)
            return
        }

        // Proceed if valid amount
        fetchStudentData(studentId) { student ->
            if (student != null) {
                Log.d("processPayment", "Student found: ${student.studentName}")
                // Proceed only when student is successfully fetched
                recordPay(student, amountDouble) { success ->
                    onResult(success)
                }
            } else {
                Log.d("processPayment", "Student not found for ID: $studentId")
                onResult(false)
            }
        }
    }

    // 3. Update class balance using the repository
    fun updateClassBalance(amount: Double) {
        viewModelScope.launch {
            try {
                val success = paymentRepository.updateClassBalance(amount)
                if (success) {
                    _classBalance.value = _classBalance.value?.plus(amount) ?: amount
                    Log.d("updateClassBalance", "Class balance updated successfully. New balance: ${_classBalance.value}")
                } else {
                    Log.e("updateClassBalance", "Failed to update class balance")
                }
            } catch (e: Exception) {
                Log.e("updateClassBalance", "Error updating class balance", e)
            }
        }
    }

    // Update the stored student data
    fun updateStudentObject(onResult: (Boolean) -> Unit) {
        currentStudent?.let { student ->
            Log.d("updateStudentObject", "Updating student ${student.studentId}")
            viewModelScope.launch {
                val result = paymentRepository.updateStudent(student)
                Log.d("updateStudentObject", "Student update result: $result")
                onResult(result)
            }
        } ?: onResult(false)
    }

    // Helper Function 1: Validate payment amount
    fun validatePay(amount: Double): Boolean {
        return amount > 0
    }


    // Utility function to get the current date
    private fun getCurrentDate(): String {
        val dateFormat = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
        return dateFormat.format(java.util.Date())
    }
}
