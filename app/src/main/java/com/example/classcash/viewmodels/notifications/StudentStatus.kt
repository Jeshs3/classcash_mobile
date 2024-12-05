package com.example.classcash.viewmodels.notifications

data class StudentStatus(
    val studentName: String,
    val isTargetCompleted: Boolean,
    val isMonthlyCompleted: Boolean,
    val monthName: String
)

