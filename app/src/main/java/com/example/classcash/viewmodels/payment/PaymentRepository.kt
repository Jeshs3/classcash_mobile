package com.example.classcash.viewmodels.payment

import android.util.Log
import com.example.classcash.viewmodels.addstudent.Student
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PaymentRepository(private val db: FirebaseFirestore){

    private val firestore = FirebaseFirestore.getInstance()
    private val studentsCollection = firestore.collection("students")
    private val notificationsCollection = firestore.collection("notifications")
    private val transactionsCollection = firestore.collection("transactions")
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

    // Process the withdrawal of a specified amount from the class balance
    suspend fun processWithdrawal(purpose: String, amount: Double): Boolean {
        return try {
            // Run a transaction to ensure atomicity
            firestore.runTransaction { transaction ->
                // Fetch the current class balance
                val balanceSnapshot = transaction.get(classBalanceDocument)
                val currentBalance = balanceSnapshot.getDouble("amount") ?: 0.0

                // Check if there is enough balance for the withdrawal
                if (currentBalance >= amount) {
                    // Update the class balance
                    val updatedBalance = currentBalance - amount
                    transaction.update(classBalanceDocument, "amount", updatedBalance)

                    // Record the withdrawal transaction
                    val transactionData = mapOf(
                        "purpose" to purpose,
                        "amount" to amount,
                        "timestamp" to FieldValue.serverTimestamp()
                    )
                    transactionsCollection.add(transactionData)

                    // Transaction completed successfully
                    updatedBalance
                } else {
                    throw Exception("Insufficient balance for withdrawal")
                }
            }.await()

            Log.d("PaymentRepository", "Withdrawal successfully processed")
            true // Return true if the transaction succeeded
        } catch (e: Exception) {
            Log.e("PaymentRepository", "Error processing withdrawal", e)
            false // Return false if the transaction failed
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


    // Reset the class balance to 0
    suspend fun deleteClassBalance(): Boolean {
        return try {
            // Update the Firestore document to set the balance to zero
            classBalanceDocument.set(mapOf("amount" to 0.0)).await()
            Log.d("resetClassBalance", "Class balance reset successfully")
            true
        } catch (e: Exception) {
            Log.e("resetClassBalance", "Error resetting class balance", e)
            false
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
        return dateFormat.format(java.util.Date())
    }
}

