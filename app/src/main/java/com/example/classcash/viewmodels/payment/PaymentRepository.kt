package com.example.classcash.viewmodels.payment

import android.util.Log
import com.example.classcash.viewmodels.addstudent.Student
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PaymentRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val studentsCollection = firestore.collection("students")
    private val notificationsCollection = firestore.collection("notifications")
    private val classBalanceDocument = firestore.collection("classBalance").document("currentBalance")


    suspend fun recordPayment(studentId: Int, amount: Double): Boolean {
        return try {
            val updateResult = firestore.runTransaction { transaction ->
                val docRef = studentsCollection.document("student_$studentId")
                val snapshot = transaction.get(docRef)

                if (!snapshot.exists()) {
                    Log.e("recordPayment", "Student not found for ID: $studentId")
                    throw Exception("Student not found")
                }

                val student = snapshot.toObject(Student::class.java)
                    ?: throw Exception("Error parsing student data")

                val updatedBalance = student.currentBal + amount
                transaction.update(docRef, mapOf(
                    "currentBal" to updatedBalance
                ))

                // Return student data for logging
                student
            }.await()

            // Add log entry after transaction
            if (updateResult != null) {
                addTransactionLog(studentId, amount)
            }

            true
        } catch (e: Exception) {
            Log.e("recordPayment", "Error recording payment transaction", e)
            false
        }
    }

    private suspend fun addTransactionLog(studentId: Int, amount: Double): Boolean {
        return try {
            val logEntry = Student.TransactionLog(amount, getCurrentDate())
            studentsCollection.document("student_$studentId").update(mapOf(
                "transactionLogs" to (getStudentById(studentId)?.transactionLogs ?: emptyList()) + logEntry
            ))
            true
        } catch (e: Exception) {
            Log.e("addTransactionLog", "Error adding transaction log", e)
            false
        }
    }

    // Helper function to get student by ID
   suspend fun getStudentById(studentId: Int): Student? {
        return try {
            val studentDocId = "student_$studentId"
            val document = studentsCollection.document(studentDocId).get().await()

            if (!document.exists()) {
                Log.w("PaymentRepository", "Student with ID $studentId (Doc ID: $studentDocId) not found")
                null
            }

            document.toObject(Student::class.java)?.copy(studentId = studentId) ?: run {
                Log.e("PaymentRepository", "Error mapping document for student ID $studentId (Doc ID: $studentId)")
                null
            }
        } catch (e: Exception) {
            Log.e("PaymentRepository", "Error fetching student by ID $studentId", e)
            null
        }
    }

    // Add a notification log
    suspend fun addNotification(message: String): Boolean {
        val notification = mapOf(
            "message" to message,
            "timestamp" to System.currentTimeMillis()
        )
        notificationsCollection.add(notification).await()
        return true
    }

    // Update the class balance
    suspend fun updateClassBalance(amount: Double): Boolean {
        val balanceSnapshot = classBalanceDocument.get().await()
        val currentBalance = balanceSnapshot.getDouble("amount") ?: 0.0
        classBalanceDocument.set(mapOf("amount" to currentBalance + amount)).await()
        return true
    }

    // Update a student's object
    suspend fun updateStudent(student: Student): Boolean {
        return try {
            // Only update mutable fields
            studentsCollection.document(student.studentId.toString()).update(
                mapOf(
                    "currentBal" to student.currentBal,
                    "transactionLogs" to student.transactionLogs
                )
            ).await()
            true
        } catch (e: Exception) {
            Log.e("updateStudent", "Error updating student object", e)
            false
        }
    }

    // Fetch the current class balance
    suspend fun getClassBalance(): Double? {
        return try {
            val balanceSnapshot = classBalanceDocument.get().await()
            balanceSnapshot.getDouble("amount") // Return the current balance or null if not set
        } catch (e: Exception) {
            Log.e("getClassBalance", "Error fetching class balance", e)
            null
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
        return dateFormat.format(java.util.Date())
    }
}

