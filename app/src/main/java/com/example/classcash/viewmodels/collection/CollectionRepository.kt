package com.example.classcash.viewmodels.collection

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class CollectionRepository(private val db: FirebaseFirestore) {

    suspend fun saveCollection(collection: Collection): Boolean {
        return try {
            // Fetch the highest current collectionId
            val lastDocument = db.collection("fundsetting")
                .orderBy("collectionId", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .await()
                .documents.firstOrNull()

            val nextId = (lastDocument?.getLong("collectionId")?.toInt() ?: 0) + 1

            val docRef = db.collection("fundsetting").document("fund_$nextId")
            val calculatedMonthlyFund = collection.calculateMonthlyFund()

            val collectionData = mapOf(
                "collectionId" to nextId,
                "dailyFund" to collection.dailyFund,
                "duration" to collection.duration,
                "monthName" to collection.monthName,
                "activeDays" to collection.activeDays,
                "monthlyFund" to calculatedMonthlyFund
            )

            Log.d("CollectionRepository", "Saving collection: $collectionData")
            docRef.set(collectionData).await()
            Log.d("CollectionRepository", "Successfully saved collection with ID: $nextId")
            true
        } catch (e: Exception) {
            Log.e("CollectionRepository", "Error saving collection", e)
            false
        }
    }

    fun fetchCollectionSettings(): Flow<Collection> = callbackFlow {
        val listener = db.collection("fundsetting")
            .orderBy(
                "updatedAt",
                Query.Direction.DESCENDING
            ) // Sort by updatedAt in descending order
            .limit(1) // Limit to the most recent document
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    close(exception) // Close the flow if an error occurs
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    // Assuming the settings correspond to a single document in "fundsetting"
                    val document = snapshot.documents.firstOrNull() // Fetch the first document
                    if (document != null) {
                        val collectionId = document.getLong("collectionId")?.toInt() ?: 0
                        val dailyFund = document.getDouble("dailyFund") ?: 0.0
                        val duration = document.getLong("duration")?.toInt() ?: 0
                        val monthName = document.getString("monthName") ?: ""
                        val activeDays = document.get("activeDays") as? List<String> ?: emptyList()
                        val monthlyFund = document.getDouble("monthlyFund") ?: 0.0

                        val collection = Collection(
                            collectionId = collectionId,
                            dailyFund = dailyFund,
                            duration = duration,
                            monthName = monthName,
                            activeDays = activeDays,
                            monthlyFund = monthlyFund
                        )
                        trySend(collection).isSuccess
                    } else {
                        trySend(Collection()).isSuccess
                    }
                } else {
                    // Handle the case where no document exists
                    close(IllegalStateException("No collection settings found"))
                }
            }

        awaitClose { listener.remove() } // Cleanup listener when flow is canceled
    }

    fun deleteCollectionSettings(): Result<Unit> {
        return try {
            val collection = db.collection("fundsetting")
            collection.get().addOnSuccessListener { querySnapshot ->
                querySnapshot.forEach { document ->
                    collection.document(document.id).delete()
                }
            }.addOnFailureListener { exception ->
                throw exception
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getActiveDays(monthName: String): List<String> {
        return try {
            Log.d("CollectionRepository", "Fetching active days for monthName: $monthName")
            val querySnapshot =
                db.collection("fundsetting").whereEqualTo("monthName", monthName).get().await()

            if (querySnapshot.isEmpty) {
                Log.d("CollectionRepository", "No active days found for monthName: $monthName")
                return emptyList()
            }

            val document = querySnapshot.documents.firstOrNull()
            document?.get("activeDays") as? List<String> ?: emptyList()
        } catch (e: Exception) {
            Log.e("CollectionRepository", "Error fetching active days for monthName: $monthName", e)
            emptyList()
        }
    }

    suspend fun getMonthlyFund(selectedMonth: String): Double {
        return try {
            val querySnapshot = db.collection("fundsetting")
                .whereEqualTo("monthName", selectedMonth)
                .limit(1)
                .get()
                .await()

            if (querySnapshot.isEmpty) {
                0.0
            } else {
                val document = querySnapshot.documents.first()
                val dailyFund = document.getDouble("dailyFund") ?: 0.0
                val activeDays = (document.get("activeDays") as? List<*>)?.size ?: 0

                Log.d("getMonthlyFund", "Daily Fund: $dailyFund, Active Days: $activeDays")
                dailyFund * activeDays
            }
        } catch (e: Exception) {
            Log.e("CollectionRepository", "Error fetching monthly fund", e)
            0.0 // Default value in case of failure
        }
    }

    suspend fun getSelectedMonth(): String {
        return try {
            val snapshot = db.collection("fundsetting")
                .document("monthName")
                .get()
                .await()

            snapshot.getString("monthName") ?: "Current Month"
        } catch (e: Exception) {
            Log.e("CollectionRepository", "Error fetching selected month", e)
            "Current Month" // Default value in case of failure
        }
    }
}

