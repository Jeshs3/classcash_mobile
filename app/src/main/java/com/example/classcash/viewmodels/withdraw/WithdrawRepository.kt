package com.example.classcash.viewmodels.withdraw

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class WithdrawalRepository(private val firestore: FirebaseFirestore) {

    private val withdrawalsCollection = firestore.collection("withdrawals")
    private val classBalanceDoc = firestore.collection("classBalance").document("currentBalance")

    // Save withdrawal to Firestore
    suspend fun saveWithdrawal(amount: Double, purpose: String) {
        val withdrawalData = mapOf(
            "amount" to amount,
            "purpose" to purpose,
            "timestamp" to System.currentTimeMillis()
        )
        withdrawalsCollection.add(withdrawalData).await()
    }

    // Update the class balance in Firestore
    suspend fun updateClassBalance(newBalance: Double) {
        classBalanceDoc.set(mapOf("balance" to newBalance)).await()
    }

    // Get the current class balance
    suspend fun getClassBalance(): Double {
        val snapshot = classBalanceDoc.get().await()
        return snapshot.getDouble("balance") ?: 0.0
    }
}
