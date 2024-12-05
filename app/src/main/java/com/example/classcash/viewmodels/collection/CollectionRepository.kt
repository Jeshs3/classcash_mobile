package com.example.classcash.viewmodels.collection

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CollectionRepository(private val db: FirebaseFirestore) {

    suspend fun saveCollection(collection: Collection): Boolean {
        return try {
            val docRef = db.collection("fundsetting").document("fund_${collection.collectionId}")
            val calculatedMonthlyFund = collection.calculateMonthlyFund()

            val collectionData = mapOf(
                "collectionId" to collection.collectionId,
                "dailyFund" to collection.dailyFund,
                "duration" to collection.duration,
                "monthName" to collection.monthName,
                "activeDays" to collection.activeDays,
                "monthlyFund" to calculatedMonthlyFund
            )

            Log.d("CollectionRepository", "Saving collection: $collectionData")
            docRef.set(collectionData).await()
            Log.d("CollectionRepository", "Successfully saved collection with ID: ${collection.collectionId}")
            true
        } catch (e: Exception) {
            Log.e("CollectionRepository", "Error saving collection: ${collection.collectionId}", e)
            false
        }
    }

    suspend fun getCollectionById(collectionId: Int): Collection? {
        return try {
            Log.d("CollectionRepository", "Fetching collection by ID: $collectionId")
            val documentSnapshot = db.collection("fundsetting").document("fund_$collectionId").get().await()
            val collectionData = documentSnapshot.data
            collectionData?.let {
                Collection(
                    collectionId = (it["collectionId"] as? Number)?.toInt() ?: 0,
                    dailyFund = (it["dailyFund"] as? Number)?.toDouble() ?: 0.0,
                    duration = (it["duration"] as? Number)?.toInt() ?: 0,
                    monthName = it["monthName"] as? String ?: "",
                    activeDays = it["activeDays"] as? List<String> ?: emptyList(),
                    monthlyFund = (it["monthlyFund"] as? Number)?.toDouble() ?: 0.0
                )
            }
        } catch (e: Exception) {
            Log.e("CollectionRepository", "Error fetching collection by ID: $collectionId", e)
            null
        }
    }

    suspend fun deleteCollectionSettings(collectionId: Int): Boolean {
        return try {
            Log.d("CollectionRepository", "Deleting collection with ID: $collectionId")
            db.collection("fundsetting").document("fund_$collectionId").delete().await()
            Log.d("CollectionRepository", "Successfully deleted collection with ID: $collectionId")
            true
        } catch (e: Exception) {
            Log.e("CollectionRepository", "Error deleting collection with ID: $collectionId", e)
            false
        }
    }

    suspend fun getActiveDays(monthName: String): List<String> {
        return try {
            Log.d("CollectionRepository", "Fetching active days for monthName: $monthName")
            val querySnapshot = db.collection("fundsetting").whereEqualTo("monthName", monthName).get().await()

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
}

