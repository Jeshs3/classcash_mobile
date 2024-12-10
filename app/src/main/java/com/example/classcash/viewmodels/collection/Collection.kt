package com.example.classcash.viewmodels.collection

import com.google.firebase.Timestamp

data class Collection(
    val collectionId: Int = 0,
    val dailyFund: Double = 0.0,
    val duration: Int = 0,
    val monthName: String = "",
    val activeDays: List<String> = emptyList(),
    val monthlyFund: Double = 0.0,
    val updatedAt: Timestamp = Timestamp.now()
) {
    fun calculateMonthlyFund(): Double {
        return activeDays.size * dailyFund
    }
}





