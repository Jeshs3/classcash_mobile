package com.example.classcash.viewmodels.addstudent

data class Student(
    val studentId: Int = 0,
    val studentName: String = "",
    var targetAmt: Double = 0.0,
    var currentBal: Double = 0.0,
    var progress: Double = 0.0,
    val transactionLogs: MutableList<TransactionLog> = mutableListOf()
) {
    data class TransactionLog(
        val amount: Double = 0.0,
        val date: String = "",
        val description: String = ""
    )


    // Dynamically calculate targetAmt using fund settings values (dailyFund * activeDays)
    fun calculateTargetAmt(dailyFund: Double, activeDays: List<String>): Double {
        return dailyFund * activeDays.size // Calculate targetAmt
    }

    fun calculateProgress(): Double = if (targetAmt > 0) (currentBal / targetAmt) * 100 else 0.0
}


