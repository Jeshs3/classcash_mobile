package com.example.classcash.viewmodels.event

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.*
import kotlinx.datetime.LocalDate

class EventRepository(private val db: FirebaseFirestore) {

    // Function to save event
    suspend fun saveEvent(event: Event): Boolean {
        return try {
            // Reference to the events collection
            val eventsCollectionRef = db.collection("event")

            // Fetch current list of events
            val snapshot = eventsCollectionRef.get().await()
            val currentEventId = if (!snapshot.isEmpty) {
                // Find the highest eventId in the existing events and increment
                snapshot.documents.mapNotNull { doc ->
                    doc.getLong("eventId")?.toInt()
                }.maxOrNull()?.plus(1) ?: 1
            } else {
                1 // If no events exist, start with ID 1
            }

            // Update the event object with the new eventId
            val updatedEvent = event.copy(eventId = currentEventId)

            // Create event data for Firestore
            val eventData = mapOf(
                "eventId" to updatedEvent.eventId,
                "eventName" to updatedEvent.eventName,
                "eventDescription" to updatedEvent.eventDescription,
                "startDate" to updatedEvent.startDate.toString(),
                "endDate" to updatedEvent.endDate.toString(),
                "budget" to mapOf(
                    "total" to updatedEvent.budget.first,
                    "categories" to updatedEvent.budget.second
                ),
                "expenses" to updatedEvent.expenses
            )

            // Save the updated event to Firestore
            val docRef = eventsCollectionRef.document("event_${updatedEvent.eventId}")
            Log.d("EventRepository", "Saving event data: $eventData")
            docRef.set(eventData).await()
            Log.d("EventRepository", "Successfully saved event with ID: ${updatedEvent.eventId}")
            true
        } catch (e: Exception) {
            Log.e("EventRepository", "Error saving event: ${event.eventId}", e)
            false
        }
    }


    // Function to delete an event by ID
    suspend fun deleteEvent(eventId: Int): Boolean {
        return try {
            Log.d("EventRepository", "Deleting event with ID: $eventId")
            db.collection("event").document("event_$eventId").delete().await()
            Log.d("EventRepository", "Successfully deleted event with ID: $eventId")
            true
        } catch (e: Exception) {
            Log.e("EventRepository", "Error deleting event with ID: $eventId", e)
            false
        }
    }

    // Function to fetch an event by ID
    suspend fun getEventById(eventId: Int): Event? {
        return try {
            Log.d("EventRepository", "Fetching event by ID: $eventId")
            val documentSnapshot = db.collection("event").document("event_$eventId").get().await()
            val eventData = documentSnapshot.data
            eventData?.let {
                Event(
                    eventId = (it["eventId"] as? Number)?.toInt() ?: 0,
                    eventName = it["eventName"] as? String ?: "",
                    eventDescription = it["eventDescription"] as? String ?: "",
                    startDate = (it["startDate"] as? String)?.let { date ->
                        LocalDate.parse(date)
                    } ?: Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
                    endDate = (it["endDate"] as? String)?.let { date ->
                        LocalDate.parse(date)
                    } ?: Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
                    budget = (it["budget"] as? Map<*, *>)?.let { budgetMap ->
                        val totalBudget = (budgetMap["total"] as? Number)?.toDouble() ?: 0.0
                        val categoryBreakdown = (budgetMap["categories"] as? Map<*, *>)?.mapNotNull { entry ->
                            val key = entry.key as? String
                            val value = (entry.value as? Number)?.toDouble()
                            if (key != null && value != null) key to value else null
                        }?.toMap() ?: emptyMap()
                        totalBudget to categoryBreakdown
                    } ?: (0.0 to emptyMap()),
                    expenses = (it["expenses"] as? Map<*, *>)?.mapNotNull { entry ->
                        val key = entry.key as? String
                        val value = entry.value as? Boolean
                        if (key != null && value != null) key to value else null
                    }?.toMap() ?: emptyMap()
                )
            }
        } catch (e: Exception) {
            Log.e("EventRepository", "Error fetching event by ID: $eventId", e)
            null
        }
    }

    // Function to delete saved budget
    suspend fun deleteEventBudget(eventId: Int): Boolean {
        return try {
            Log.d("EventRepository", "Deleting budget for event ID: $eventId")
            val docRef = db.collection("event").document("event_$eventId")
            val updates = mapOf(
                "budget" to mapOf(
                    "total" to 0.0,
                    "categories" to emptyMap<String, Double>()
                )
            )
            docRef.update(updates).await()
            Log.d("EventRepository", "Successfully deleted budget for event ID: $eventId")
            true
        } catch (e: Exception) {
            Log.e("EventRepository", "Error deleting budget for event ID: $eventId", e)
            false
        }
    }
}





