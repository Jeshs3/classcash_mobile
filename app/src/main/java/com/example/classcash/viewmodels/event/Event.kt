package com.example.classcash.viewmodels.event

import kotlinx.datetime.*

data class Event(
    val eventId: Int,
    val eventName: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val budget: Pair<Double, Map<String, Double>>,
    val expenses: Double
)



