package com.example.classcash.viewmodels.addstudent

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class StudentRepository(private val db: FirebaseFirestore) {

    // Save or update a single student
    suspend fun saveStudent(student: Student): Result<Unit> = safeRepositoryCall {
        val docRef = db.collection("students").document("student_${student.studentId}")
        val studentData = mapOf(
            "studentId" to student.studentId,
            "studentName" to student.studentName,
            "targetAmt" to student.targetAmt,
            "currentBal" to student.currentBal,
            "progress" to student.calculateProgress(),
            "transactionLogs" to student.transactionLogs
        )
        docRef.set(studentData).await()
    }

    // Batch save students
    suspend fun saveStudentsBatch(students: List<Student>): Result<Unit> = safeRepositoryCall {
        val batch = db.batch()
        val collectionRef = db.collection("students")

        students.forEach { student ->
            val docRef = collectionRef.document("student_${student.studentId}")
            val studentData = mapOf(
                "studentId" to student.studentId,
                "studentName" to student.studentName,
                "targetAmt" to student.targetAmt,
                "currentBal" to student.currentBal,
                "progress" to student.calculateProgress(),
                "transactionLogs" to student.transactionLogs
            )
            batch.set(docRef, studentData)
        }

        batch.commit().await()
    }

    // Remove a student
    suspend fun removeStudent(studentId: Int): Result<Unit> = safeRepositoryCall {
        val docRef = db.collection("students").document("student_$studentId")
        docRef.delete().await()
    }

    // Fetch student names initially
    fun fetchStudentNames(): Flow<Map<Int, String>> = callbackFlow {
        val listener = db.collection("students").addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                close(exception)
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                val studentMap = snapshot.documents.associate { doc ->
                    val studentId = doc.getLong("studentId")?.toInt() ?: 0
                    val studentName = doc.getString("studentName") ?: ""
                    val targetAmt = doc.getDouble("targetAmt") ?: 0.0
                    val currentBal = doc.getDouble("currentBal") ?: 0.0
                    val progress = doc.getDouble("progress") ?: 0.0
                    val transactionLogs = doc.get("transactionLogs") as? List<Map<String, Any>> ?: emptyList()
                    studentId to studentName
                }
                trySend(studentMap).isSuccess
            }
        }

        awaitClose { listener.remove() }
    }

    fun deleteAllStudents(): Result<Unit> {
        return try {
            val collection = db.collection("students")
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

    fun getStudentObjectsFlow(): Flow<List<Student>> = callbackFlow {
        val listener = db.collection("students").addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error) // Propagate the error to the Flow
                return@addSnapshotListener
            }

            val students = snapshot?.documents?.mapIndexed { index, doc ->
                val studentName = doc.getString("studentName") ?: "Unknown"
                val studentId = doc.getLong("studentId")?.toInt() ?: (index + 1)
                val targetAmt = doc.getDouble("targetAmt") ?: 0.0
                val currentBal = doc.getDouble("currentBal") ?: 0.0

                // Create the student with the calculated progress
                StudentWarehouse.createStudent(
                    studentId = studentId,
                    studentName = studentName,
                    targetAmt = targetAmt,
                    currentBal = currentBal
                )
            }?.onEach { student ->
                // Log progress for debugging purposes
                val progress = student.calculateProgress()
                Log.d("StudentProgress", "Student: ${student.studentName}, Progress: $progress%")
            } ?: emptyList()

            trySend(students).isSuccess // Emit the updated list
        }

        awaitClose { listener.remove() } // Clean up the listener when Flow collection stops
    }.flowOn(Dispatchers.IO)


    // Helper function to reduce duplicate try-catch blocks
    private suspend fun <T> safeRepositoryCall(action: suspend () -> T): Result<T> = withContext(Dispatchers.IO) {
        try {
            Result.success(action())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}