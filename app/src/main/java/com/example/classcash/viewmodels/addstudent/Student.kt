package com.example.classcash.viewmodels.addstudent

data class Student(
    val studentId: Int,
    var name: String,
    var balance: Double,
    var progress: Double = 0.0,
    val transactionLogs: MutableList<TransactionLog> = mutableListOf()
)

data class TransactionLog(
    val date: String,
    val amountPaid: Double
)

