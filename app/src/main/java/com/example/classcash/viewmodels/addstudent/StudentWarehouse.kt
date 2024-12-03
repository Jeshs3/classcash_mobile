package com.example.classcash.viewmodels.addstudent

object StudentWarehouse {
    fun createStudent(studentId: Int, studentName: String, targetAmt: Double = 0.0): Student {
        return Student(
            studentId = studentId,
            studentName = studentName,
            targetAmt = targetAmt,          // Default target amount
            currentBal = 0.0,               // Default balance
            transactionLogs = mutableListOf() // Empty logs
        )
    }
}
