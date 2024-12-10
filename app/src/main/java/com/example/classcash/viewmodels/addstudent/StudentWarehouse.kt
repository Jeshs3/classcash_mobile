package com.example.classcash.viewmodels.addstudent

import com.example.classcash.viewmodels.collection.Collection

object StudentWarehouse {
    fun createStudent(
        studentId: Int,
        studentName: String,
        collection: Collection, // Ensure correct type
        currentBal: Double = 0.0,
        progress : Double = 0.0
    ): Student {
        val monthlyFund = collection.calculateMonthlyFund()
        return Student(
            studentId = studentId,
            studentName = studentName,
            targetAmt = monthlyFund, // Uses calculated monthly fund
            currentBal = currentBal,
            transactionLogs = mutableListOf() // Empty transaction logs
        )
    }
}



