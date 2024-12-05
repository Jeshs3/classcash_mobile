package com.example.classcash.viewmodels.collection

data class Collection(
    val collectionId: Int = 0,
    val dailyFund: Double = 0.0,
    val duration: Int = 0,
    val monthName: String = "",
    val activeDays: List<String> = emptyList(),
    val monthlyFund: Double = 0.0
) {
    fun calculateMonthlyFund(): Double {
        return activeDays.size * dailyFund
    }
}





