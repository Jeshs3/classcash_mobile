package com.example.classcash.viewmodels.event


import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.*

class AddEventViewModel(private val eventRepository: EventRepository) : ViewModel() {

    val selectedEvent = MutableStateFlow<Event?>(null)
    val validationError = MutableStateFlow<String?>(null)
    val events: StateFlow<List<Event>> = eventRepository.getAllEvents()

    fun addEvent(eventName: String, startDate: LocalDate, endDate: LocalDate) {
        if (eventName.isBlank()) {
            validationError.value = "Event name cannot be blank."
            return
        }
        if (startDate > endDate) {
            validationError.value = "Start date cannot be after the end date."
            return
        }

        try {
            val budget = eventRepository.generateBudget(startDate, endDate)
            val expenses = eventRepository.generateExpenses(startDate, endDate)
            val event = Event(
                eventId = eventRepository.generateEventId(),
                eventName = eventName,
                startDate = startDate,
                endDate = endDate,
                budget = budget,
                expenses = expenses
            )
            eventRepository.addEvent(event)
            validationError.value = null // Clear previous errors
        } catch (e: Exception) {
            validationError.value = "Failed to add event: ${e.message}"
        }
    }

    fun selectEvent(date: LocalDate) {
        selectedEvent.value = eventRepository.getEvent(date)
    }

    fun removeEvent(eventId: Int) {
        try {
            eventRepository.removeEvent(eventId)
        } catch (e: Exception) {
            validationError.value = "Failed to remove event: ${e.message}"
        }
    }

    fun calculateCustomAmount(budget: Double, percentage: Double): Double {
        require(percentage in 0.0..100.0) { "Percentage must be between 0 and 100" }
        return budget * (percentage / 100)
    }
}




class CalendarViewModel : ViewModel() {

    fun getDaysForMonth(year: Int, month: Int): List<LocalDate?> {
        val firstDayOfMonth = LocalDate(year, month, 1)
        val daysInMonth = when (month) {
            2 -> if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) 29 else 28 // Leap year calculation
            4, 6, 9, 11 -> 30 // April, June, September, November
            else -> 31 // All other months
        }
        val firstDayOffset = firstDayOfMonth.dayOfWeek.isoDayNumber % 7

        // Generate list of days with offsets for empty slots
        return buildList {
            repeat(firstDayOffset) { add(null) } // Empty slots for alignment
            addAll((1..daysInMonth).map { day -> LocalDate(year, month, day) })
        }
    }

    fun getCurrentMonth(): Pair<Int, Int> {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        return now.year to now.monthNumber
    }
}
