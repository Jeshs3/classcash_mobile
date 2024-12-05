package com.example.classcash.viewmodels.addstudent

object StudentWarehouse {
    fun createStudent(
        studentId: Int,
        studentName: String,
        targetAmt: Double = 0.0,
        currentBal: Double = 0.0 // Added current balance
    ): Student {
        return Student(
            studentId = studentId,
            studentName = studentName,
            targetAmt = targetAmt,
            currentBal = currentBal, // Set the balance from input
            transactionLogs = mutableListOf() // Empty logs
        )
    }
}

