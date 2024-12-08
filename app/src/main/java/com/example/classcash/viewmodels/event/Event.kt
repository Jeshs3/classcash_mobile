package com.example.classcash.viewmodels.event

import kotlinx.datetime.*

data class Event(
    val eventId: Int = 0,
    val eventName: String = "",
    val eventDescription: String = "",
    val startDate: LocalDate,
    val endDate: LocalDate,
    val budget: Pair<Double, Map<String, Double>> = Pair(0.0, emptyMap()),
    val expenses: Map<String, Boolean> = emptyMap()
) {
    val startDateString: String get() = startDate.toString()
    val endDateString: String get() = endDate.toString()

    fun totalExpenses(): Double {
        return budget.second.values.sum()
    }

    fun isExpensePredefined(expenseName: String): Boolean {
        return expenses[expenseName] ?: false
    }
}




