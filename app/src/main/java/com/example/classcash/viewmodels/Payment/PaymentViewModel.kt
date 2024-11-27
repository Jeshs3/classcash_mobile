package com.example.classcash.viewmodels.Payment

import android.icu.text.SimpleDateFormat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Locale


class PaymentViewModel : ViewModel() {
    private val studentsData = mutableMapOf<String, StudentData>()

    // Student data class
    data class StudentData(
        val studentName: String,
        val targetAmt: Double,
        val payment: MutableList<PaymentRecord>
    ) {
        var currentBal: Double = 0.0
            private set

        // Method to update balance internally
        fun updateBalance(amount: Double) {
            currentBal += amount
        }
    }

    data class PaymentRecord(val studentId: Int, val amount: Double, val date: String)

    // Retrieves the student's name
    fun getStudent(name: String): String? {
        return studentsData[name]?.studentName
    }

    // Retrieves the current balance for a student
    fun getCurrentBal(name: String): Double? {
        return studentsData[name]?.currentBal
    }

    // Records a payment for a student if validation passes
    suspend fun recordPayment(name: String, amount: Double): Boolean {
        val student = studentsData[name]
        return if (student != null && validatePayment(amount)) {
            student.updateBalance(amount)  // Safely update balance
            student.payment.add(PaymentRecord(studentId = student.hashCode(), amount = amount, date = getCurrentDate()))
            true
        } else {
            false
        }
    }

    // Validates a payment amount for format and positive value
    fun validatePayment(amount: Double): Boolean {
        return amount > 0 && isValidAmount(amount.toString())
    }

    // Checks if the amount contains only valid digits and one optional decimal point
    private fun isValidAmount(input: String): Boolean {
        val regex = Regex("^[0-9]+(\\.[0-9]{1,2})?$")
        return regex.matches(input)
    }

    // Calculates the balance remaining to reach the target amount
    fun calculateStudentBalance(currentBal: Double, amount: Double): Double {
        return currentBal - amount
    }

    // Calculates progress towards the target as a percentage
    fun calculateProgress(currentBal: Double, targetAmt: Double): Double {
        return if (targetAmt > 0) {
            (currentBal / targetAmt) * 100
        } else {
            0.0
        }
    }

    // Deletes all payment records for a student by studentId
    fun delPayment(studentId: Int): Boolean {
        val student = studentsData.values.find { it.hashCode() == studentId }
        return if (student != null) {
            viewModelScope.launch {
                student.payment.clear()
                student.updateBalance(-student.currentBal)  // Reset balance to 0
            }
            true
        } else {
            false
        }
    }

    // Utility function to get the current date
    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }

    // Updates a student's payment
    fun updatePay(name: String, amount: Double, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            if (validatePayment(amount)) {
                val result = recordPayment(name, amount)
                onResult(result)
            } else {
                onResult(false)
            }
        }
    }

    //Function that get the target amount for student
    fun getTargetAmount(name: String): Double? {
        return studentsData[name]?.targetAmt
    }
}