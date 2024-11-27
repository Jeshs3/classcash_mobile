package com.example.classcash.viewmodels.collection

data class Collection(
    val collectionId: Int,
    val dailyFund: Double,
    val duration: Int,
    val month: Month,
    val activeDays: List<String>,
    val monthlyFund: Double
) 


data class Month(
    val monthName: String = "",
    val activeDays: List<String> = emptyList(),
    val monthlyFund: Double = 0.0
)

data class CollectionSettings(
    val duration: Int,
    val dailyFund: Double,
    val month: String
)


