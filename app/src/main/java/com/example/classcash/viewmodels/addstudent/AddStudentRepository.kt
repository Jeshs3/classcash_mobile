package com.example.classcash.viewmodels.addstudent

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class StudentRepository(private val db: FirebaseFirestore) {

    // Save or update a single student
    suspend fun saveStudentName(studentId: Int, name: String): Result<Unit> = safeRepositoryCall {
        val docRef = db.collection("students").document("student_$studentId")
        docRef.set(mapOf("id" to studentId, "name" to name), SetOptions.merge()).await()
    }

    suspend fun saveStudent(student: Student): Result<Unit> = safeRepositoryCall {
        val docRef = db.collection("students").document("student_${student.studentId}")
        val studentData = mapOf(
            "id" to student.studentId,
            "name" to student.name,
            "balance" to student.balance,
            "transactionLogs" to student.transactionLogs
        )
        docRef.set(studentData).await()
        //_students.value = _students.value + student
    }

    // Remove a student
    suspend fun removeStudent(studentId: Int): Result<Unit> = safeRepositoryCall {
        val docRef = db.collection("students").document("student_$studentId")
        docRef.delete().await()
    }

    // Batch save students
    suspend fun saveStudentsBatch(students: List<Pair<Int, String>>): Result<Unit> = safeRepositoryCall {
        val batch = db.batch()
        val collectionRef = db.collection("students")

        students.forEach { (id, name) ->
            val docRef = collectionRef.document("student_$id")
            batch.set(docRef, mapOf("id" to id, "name" to name))
        }

        batch.commit().await()
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
                    val id = doc.getLong("id")?.toInt() ?: 0
                    val name = doc.getString("name") ?: ""
                    id to name
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

    fun getStudentObjectsFlow(): Flow<List<Student>> = flow {
        // Simulating a database call to fetch all students
        val studentDocs = db.collection("students").get().await() // Use Firebase's `get()` to fetch all student data
        val students = studentDocs.documents.mapIndexed { index, doc ->
            val name = doc.getString("name") ?: "Unknown"
            val id = doc.getLong("id")?.toInt() ?: (index + 1) // Ensure fallback ID
            StudentWarehouse.createStudent(studentId = id, name = name)
        }
        emit(students)
    }



    // Helper function to reduce duplicate try-catch blocks
    private suspend fun <T> safeRepositoryCall(action: suspend () -> T): Result<T> = withContext(Dispatchers.IO) {
        try {
            Result.success(action())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
