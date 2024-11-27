package com.example.classcash.viewmodels.event

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.*

class EventRepository {
    private val _events = MutableStateFlow<List<Event>>(emptyList())  // MutableStateFlow to hold events
    val events: StateFlow<List<Event>> get() = _events  // Exposed as StateFlow

    // Function to fetch events
    fun getAllEvents(): StateFlow<List<Event>> = events  // Returning StateFlow directly

    fun generateBudget(startDate: LocalDate, endDate: LocalDate): Pair<Double, Map<String, Double>> {
        val totalBudget = 10000.0 // Example total budget
        val breakdown = mapOf(
            "Venue" to 4000.0,
            "Catering" to 3500.0,
            "Miscellaneous" to 2500.0
        )
        return totalBudget to breakdown
    }


    fun generateExpenses(startDate: LocalDate, endDate: LocalDate): Double {
        // Your logic to generate expenses
        return 0.0
    }


    //
    fun generateEventId(): Int {
        // Logic to generate a unique event ID
        return (_events.value.size + 1)
    }

    // Function to add event and update _events list
    fun addEvent(event: Event) {
        _events.value = _events.value + event
    }

    // Function to remove event and update _events list
    fun removeEvent(eventId: Int) {
        _events.value = _events.value.filterIndexed { i, _ -> i != eventId }
    }

    // Function to find an event by date
    fun getEvent(date: LocalDate): Event? {
        return _events.value.find { it.startDate == date || it.endDate == date }
    }
}




