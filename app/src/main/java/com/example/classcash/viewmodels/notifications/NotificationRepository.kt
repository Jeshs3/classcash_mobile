package com.example.classcash.viewmodels.notifications

import com.example.classcash.viewmodels.addstudent.Student
import com.example.classcash.viewmodels.addstudent.Student.TransactionLog
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

class NotificationsRepository(
    private val students: List<Student>,
    private val withdrawalLogs: List<TransactionLog>,
    private val sinkingFundTarget: Double,
    private val activeDaysOfMonth: Int
) {

    // 1. Get list of students who missed contributions
    fun getMissedContributions(): List<Student> {
        return students.filter { student ->
            student.transactionLogs.none { log ->
                val date = LocalDate.parse(log.date) // Parse the date string to LocalDate
                date.dayOfMonth == activeDaysOfMonth // Checks if the last active day of the month has a log
            }
        }
    }

    // 2. Calculate the overall progress of contributions
    fun getProgressPercentage(): Double {
        val totalCollected = students.sumOf { it.currentBal }
        return if (sinkingFundTarget > 0) {
            (totalCollected / sinkingFundTarget) * 100
        } else {
            0.0
        }
    }

    // 3. Get withdrawal logs
    fun getWithdrawalLogs(): List<TransactionLog> {
        return withdrawalLogs
    }

    // 4. Get student status for contributions
    fun getStudentStatus(): List<StudentStatus> {
        val completedStudents = students.filter { student ->
            student.currentBal >= student.targetAmt
        }.sortedBy { student ->
            student.transactionLogs.minOfOrNull { it.date ?: "" } // Sort by the earliest contribution completion date, handle nulls
        }

        return completedStudents.mapIndexed { index, student ->
            StudentStatus(
                studentName = student.studentName,
                isTargetCompleted = student.currentBal >= student.targetAmt,
                isMonthlyCompleted = student.transactionLogs.any { log ->
                    val date = LocalDate.parse(log.date) // Parse the date string to LocalDate
                    date.month.number == activeDaysOfMonth  // Checks if the log corresponds to the correct month
                },
                monthName = "January" // Placeholder, you can customize this to reflect the actual month
            )
        }
    }
}

