package com.example.classcash.viewmodels.event

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.datetime.*

class AddEventViewModel(private val eventRepository: EventRepository) : ViewModel() {

    private val _event = MutableLiveData<Event?>()
    val event: LiveData<Event?> get() = _event

    private val _startDate = MutableLiveData<LocalDate>()
    val startDate: LiveData<LocalDate> get() = _startDate

    private val _endDate = MutableLiveData<LocalDate>()
    val endDate: LiveData<LocalDate> get() = _endDate

    private val _message = MutableLiveData<EventMessage>()
    val message: LiveData<EventMessage> get() = _message

    private val _eventSaveStatus = MutableLiveData<EventSaveStatus>()
    val eventSaveStatus: LiveData<EventSaveStatus> = _eventSaveStatus

    sealed class EventMessage {
        data class Error(val message: String) : EventMessage()
    }

    fun updateEventDetails(input: Map<String, Any?>) {
        try {
            // Update eventName if provided
            input["eventName"]?.let { name ->
                if (name is String && name.isNotBlank()) {
                    _event.value = _event.value?.copy(eventName = name)
                    Log.d("EventUpdate", "Event name updated to: $name")
                }
            }

            // Update eventDescription if provided (optional)
            input["eventDescription"]?.let { description ->
                if (description is String) {
                    _event.value = _event.value?.copy(eventDescription = description)
                    Log.d("EventUpdate", "Event description updated to: $description")
                }
            }

            // Update startDate and endDate if provided
            input["startDate"]?.let { start ->
                when (start) {
                    is String -> { // Parse if it's a String
                        try {
                            val parsedStartDate = LocalDate.parse(start)
                            _event.value = _event.value?.copy(startDate = parsedStartDate)
                            Log.d("EventUpdate", "Start date updated to: $parsedStartDate")
                        } catch (e: Exception) {
                            Log.e("EventUpdate", "Invalid start date format: $start", e)
                        }
                    }
                    is LocalDate -> { // Use directly if it's already a LocalDate
                        _event.value = _event.value?.copy(startDate = start)
                        Log.d("EventUpdate", "Start date updated to: $start")
                    }
                    else -> {
                        Log.e("EventUpdate", "Unsupported start date type: ${start::class}")
                    }
                }
            }

            input["endDate"]?.let { end ->
                when (end) {
                    is String -> {
                        try {
                            val parsedEndDate = LocalDate.parse(end)
                            _event.value = _event.value?.copy(endDate = parsedEndDate)
                            Log.d("EventUpdate", "End date updated to: $parsedEndDate")
                        } catch (e: Exception) {
                            Log.e("EventUpdate", "Invalid end date format: $end", e)
                        }
                    }
                    is LocalDate -> {
                        _event.value = _event.value?.copy(endDate = end)
                        Log.d("EventUpdate", "End date updated to: $end")
                    }
                    else -> {
                        Log.e("EventUpdate", "Unsupported end date type: ${end::class}")
                    }
                }
            }

        } catch (e: Exception) {
            Log.e("EventUpdate", "Error updating event details", e)
        }
    }

    fun addEvent() {
        val eventDetails = _event.value
        if (eventDetails != null) {
            _eventSaveStatus.value = EventSaveStatus.Loading
            viewModelScope.launch {
                try {
                    val isSuccess = eventRepository.saveEvent(eventDetails)
                    if (isSuccess) {
                        _eventSaveStatus.value = EventSaveStatus.Success
                    } else {
                        _eventSaveStatus.value = EventSaveStatus.Error("Failed to save event.")
                    }
                } catch (e: Exception) {
                    Log.e("AddEventViewModel", "Error saving event", e)
                    _eventSaveStatus.value = EventSaveStatus.Error("An error occurred: ${e.message}")
                }
            }
        } else {
            _eventSaveStatus.value = EventSaveStatus.Error("Event details are incomplete.")
        }
    }

    fun removeEvent(eventId: Int) {
        viewModelScope.launch {
            try {
                Log.d("AddEventViewModel", "Initiating deletion for event ID: $eventId")

                // Attempt to delete the collection from the repository
                val success = eventRepository.deleteEvent(eventId)

                if (success) {
                    // If deletion is successful, reset the local state
                    _event.value = null

                    Log.d("AddEventViewModel", "Successfully deleted event details.")
                } else {
                    Log.e("AddEventViewModel", "Failed to delete event from the repository.")
                    handleError("Failed to delete event. Please try again.")
                }
            } catch (e: Exception) {
                Log.e("AddEventViewModel", "Error occurred while deleting event", e)
                handleError("An error occurred while deleting the event.")
            }
        }
    }

    // Function to fetch event by ID
    fun fetchEventDetails(eventId: Int) {
        Log.d("EventViewModel", "Fetching event by ID: $eventId")
        viewModelScope.launch {
            try {
                // Fetch the event from the repository
                val fetchedEvent = eventRepository.getEventById(eventId)
                if (fetchedEvent != null) {
                    _event.value = fetchedEvent // Update LiveData
                    Log.d("EventViewModel", "Fetched event: $fetchedEvent")
                } else {
                    Log.d("EventViewModel", "No event found with ID: $eventId")
                }
            } catch (e: Exception) {
                Log.e("EventViewModel", "Error fetching event by ID: $eventId", e)
            }
        }
    }

    // Calendar-related functions from CalendarViewModel
    fun getDates(startDate: LocalDate?, endDate: LocalDate?): List<LocalDate> {
        if (startDate == null || endDate == null) return emptyList()
        return if (startDate == endDate) {
            listOf(startDate) // Single day event
        } else {
            generateSequence(startDate) { it.plus(DatePeriod(days = 1)) }
                .takeWhile { it <= endDate }
                .toList()
        }
    }

    fun getDaysForMonth(year: Int, month: Int): List<LocalDate?> {
        val firstDayOfMonth = LocalDate(year, month, 1)
        val daysInMonth = when (month) {
            2 -> if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) 29 else 28
            4, 6, 9, 11 -> 30
            else -> 31
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

    private fun handleError(message: String) {
        Log.e("AddEventViewModel", "Error: $message")
        _message.value = EventMessage.Error(message)
    }

    // Helper function to check if the date is in the range of startDate and endDate
    fun isDateInRange(date: LocalDate): Boolean {
        val startDate = _startDate.value
        val endDate = _endDate.value

        // Check if startDate and endDate are not null
        if (startDate == null || endDate == null) {
            Log.e("AddEventViewModel", "Start date or end date is not set.")
            return false // Return false instead of throwing an exception
        }

        // Return true if the date is within the range
        return date >= startDate && date <= endDate
    }
}

sealed class EventSaveStatus {
    object Loading : EventSaveStatus()
    object Success : EventSaveStatus()
    data class Error(val message: String) : EventSaveStatus()
}
