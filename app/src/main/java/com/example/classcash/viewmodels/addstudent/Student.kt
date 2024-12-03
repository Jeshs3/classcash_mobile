package com.example.classcash.viewmodels.addstudent

data class Student(
    val studentId: Int = 0,
    val studentName: String = "",
    var targetAmt: Double = 0.0,
    var currentBal: Double = 0.0,
    val transactionLogs: MutableList<TransactionLog> = mutableListOf()
) {
    data class TransactionLog(
        val amount: Double = 0.0,
        val date: String = "",
        val description: String = ""
    )
    fun calculateProgress(): Double = if (targetAmt > 0) (currentBal / targetAmt) * 100 else 0.0
}


