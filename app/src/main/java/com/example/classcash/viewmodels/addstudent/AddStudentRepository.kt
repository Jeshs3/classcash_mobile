package com.example.classcash.viewmodels.addstudent

import android.util.Log
import com.example.classcash.viewmodels.addstudent.Student.TransactionLog
import com.example.classcash.viewmodels.collection.CollectionRepository
import com.example.classcash.viewmodels.collection.Collection
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class StudentRepository(
    private val db: FirebaseFirestore,
    private val collectionRepository: CollectionRepository
) {

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
                    val progress = if (targetAmt > 0) (currentBal / targetAmt) * 100 else 0.0 // Progress calculation
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

    suspend fun getStudentObjectsFlow(): Flow<List<Student>> = callbackFlow {
        val listener = db.collection("students").addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error) // Propagate the error to the Flow
                return@addSnapshotListener
            }

            if (snapshot == null) {
                trySend(emptyList<Student>()).isSuccess // Emit an empty list if snapshot is null
                return@addSnapshotListener
            }

            // Launch a coroutine to fetch selected month and monthly fund
            launch {
                try {
                    // Fetch the selected month asynchronously
                    val selectedMonth = collectionRepository.getSelectedMonth() // Get the selected month
                    if (selectedMonth.isEmpty()) {
                        trySend(emptyList<Student>()).isSuccess
                        return@launch
                    }

                    // Fetch the monthly fund for the selected month asynchronously
                    val monthlyFund = collectionRepository.getMonthlyFund(selectedMonth) // Get the monthly fund

                    // Prepare a dummy Collection object
                    val dummyCollection = Collection(
                        collectionId = 0,
                        dailyFund = 0.0,
                        duration = 0,
                        monthName = selectedMonth,
                        activeDays = emptyList(), // Replace with real activeDays if necessary
                        monthlyFund = monthlyFund
                    )

                    // Create a list of students based on the snapshot documents
                    val students = snapshot.documents.mapIndexed { index, doc ->
                        val studentName = doc.getString("studentName") ?: "Unknown"
                        val studentId = doc.getLong("studentId")?.toInt() ?: (index + 1)
                        val currentBal = doc.getDouble("currentBal") ?: 0.0
                        val targetAmt = doc.getDouble("targetAmt") ?: 0.0
                        val progress = if (targetAmt > 0) (currentBal / targetAmt) * 100 else 0.0 // Calculate progress


                        // Use StudentWarehouse to create the student
                        StudentWarehouse.createStudent(
                            studentId = studentId,
                            studentName = studentName,
                            collection = dummyCollection, // Pass the dummy Collection
                            currentBal = currentBal,
                            progress = progress
                        )
                    }.onEach { student ->
                        // Log progress for debugging purposes
                        val progress = student.calculateProgress()
                         }

                    trySend(students).isSuccess // Emit the updated list
                } catch (e: Exception) {
                    Log.e("StudentFlow", "Error fetching data", e)
                    trySend(emptyList<Student>()).isSuccess // Send empty list in case of error
                }
            }
        }

        awaitClose { listener.remove() } // Clean up the listener when Flow collection stops
    }.flowOn(Dispatchers.IO) // Ensure that the flow is processed on the IO dispatcher

    suspend fun updateStudentsBatch(students: List<Student>): Result<Unit> {
        return try {
            val batch = db.batch()
            students.forEach { student ->
                val docRef = db.collection("students").document(student.studentId.toString())
                batch.update(docRef, "targetAmt", student.targetAmt)

                val progress = if (student.targetAmt > 0) {
                    (student.currentBal / student.targetAmt) * 100
                } else {
                    0.0
                }

                Log.d("updateStudent", "Successfully updated student with ID: ${student.studentId}")
                Log.d("StudentProgress", "Student: ${student.studentName}, Progress: $progress%, Target Amount: ${student.targetAmt}")

            }
            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("StudentRepository", "Error updating students batch", e)
            Result.failure(e)
        }
    }

    suspend fun getAllStudents(): List<Student> {
        return try {
            // Fetch all student documents from the "students" collection
            val snapshot = db.collection("students").get().await()

            // Map each document to a Student object
            snapshot.documents.map { doc ->
                Student(
                    studentId = doc.getLong("studentId")?.toInt() ?: 0,
                    studentName = doc.getString("studentName") ?: "Unknown",
                    targetAmt = doc.getDouble("targetAmt") ?: 0.0,
                    currentBal = doc.getDouble("currentBal") ?: 0.0,
                    transactionLogs = (doc.get("transactionLogs") as? List<Map<String, Any>>)?.map { log ->
                        TransactionLog(
                            date = log["date"] as? String ?: "Unknown Date",
                            amount = log["amount"] as? Double ?: 0.0,
                            description = log["description"] as? String ?: "No Description"
                        )
                    }?.toMutableList() ?: mutableListOf()
                )
            }
        } catch (e: Exception) {
            Log.e("StudentRepository", "Error fetching students", e)
            emptyList() // Return an empty list in case of failure
        }
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