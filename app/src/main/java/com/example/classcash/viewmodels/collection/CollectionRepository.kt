package com.example.classcash.viewmodels.collection

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

interface CollectionRepository {
    suspend fun saveCollection(collection: Collection): Boolean
    suspend fun getCollectionById(collectionId: Int): Collection?
    suspend fun getAllCollections(): List<Collection>
    suspend fun deleteCollection(collectionId: Int): Boolean
    suspend fun fetchMonthObject(monthName: String): Collection?
}


class CollectionRepositoryImpl : CollectionRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val collectionsRef = firestore.collection("collections") // Firestore collection reference

    override suspend fun saveCollection(collection: Collection): Boolean {
        return try {
            // Use collection ID as document ID or let Firestore auto-generate one
            collectionsRef.document(collection.collectionId.toString())
                .set(collection) // Save collection to Firestore
                .await() // Await completion
            true
        } catch (e: Exception) {
            // Handle error (e.g. log it)
            false
        }
    }

    override suspend fun getCollectionById(collectionId: Int): Collection? {
        return try {
            val documentSnapshot = collectionsRef.document(collectionId.toString()).get().await()
            documentSnapshot.toObject(Collection::class.java)
        } catch (e: Exception) {
            // Handle error (e.g. log it)
            null
        }
    }

    override suspend fun getAllCollections(): List<Collection> {
        return try {
            val querySnapshot = collectionsRef.get().await() // Fetch all documents
            querySnapshot.documents.mapNotNull { it.toObject(Collection::class.java) }
        } catch (e: Exception) {
            // Handle error (e.g. log it)
            emptyList()
        }
    }

    override suspend fun fetchMonthObject(monthName: String): Collection? {
        return try {
            val querySnapshot = collectionsRef
                .whereEqualTo("monthName", monthName)
                .get()
                .await()

            if (querySnapshot.isEmpty) {
                Log.d("Repository", "No document found for monthName: $monthName")
                return null
            }

            // Assuming only one document should match
            val document = querySnapshot.documents.firstOrNull()
            val collection = document?.toObject(Collection::class.java)

            if (collection == null) {
                Log.w("Repository", "Document for $monthName could not be converted to Collection.")
            } else {
                Log.d("Repository", "Fetched collection: $collection for monthName: $monthName")
            }

            collection
        } catch (e: Exception) {
            Log.e("Repository", "Error fetching month object for $monthName", e)
            null
        }
    }


    override suspend fun deleteCollection(collectionId: Int): Boolean {
        return try {
            collectionsRef.document(collectionId.toString()).delete().await()
            true
        } catch (e: Exception) {
            // Handle error (e.g. log it)
            false
        }
    }
}
