package com.example.classcash.viewmodels.addstudent

object StudentWarehouse {
    fun createStudent(studentId: Int, name: String): Student {
        return Student(
            studentId = studentId,
            name = name,
            balance = 0.0,              // Default balance
            progress = 0.0,             // Default progress
            transactionLogs = mutableListOf() // Empty logs
        )
    }
}