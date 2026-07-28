package com.example.data.sync

import android.util.Log
import com.example.data.local.AppDatabase
import com.example.data.local.StudentAttendanceEntity
import com.example.data.local.StudentPerformanceEntity
import com.example.data.remote.CbcClassStudent
import com.example.data.remote.StudentAttendance
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SyncState {
    object Idle : SyncState()
    object Syncing : SyncState()
    data class Success(val message: String) : SyncState()
    data class Error(val message: String) : SyncState()
}

class FirestoreSyncManager(
    private val database: AppDatabase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    private var attendanceListener: ListenerRegistration? = null
    private var performanceListener: ListenerRegistration? = null

    companion object {
        private const val TAG = "FirestoreSyncManager"
        private const val COLLECTION_ATTENDANCE = "student_attendance"
        private const val COLLECTION_PERFORMANCE = "student_performance"
    }

    /**
     * Start real-time Firestore sync listening.
     * Listens to remote changes in student_attendance and student_performance
     * and updates the local Room database automatically.
     */
    fun startRealtimeSync() {
        _syncState.value = SyncState.Syncing
        try {
            // Listen to student_attendance changes in Firestore
            attendanceListener = firestore.collection(COLLECTION_ATTENDANCE)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.e(TAG, "Attendance listener failed: ", e)
                        _syncState.value = SyncState.Error("Firestore Attendance sync offline: ${e.localizedMessage}")
                        return@addSnapshotListener
                    }

                    if (snapshot != null && !snapshot.isEmpty) {
                        scope.launch {
                            val attendanceList = snapshot.documents.mapNotNull { doc ->
                                val id = doc.id
                                val admNo = doc.getString("admissionNo") ?: return@mapNotNull null
                                val name = doc.getString("studentName") ?: ""
                                val date = doc.getString("date") ?: ""
                                val status = doc.getString("status") ?: "PRESENT"
                                val note = doc.getString("note") ?: ""
                                StudentAttendanceEntity(
                                    id = id,
                                    admissionNo = admNo,
                                    studentName = name,
                                    date = date,
                                    status = status,
                                    note = note,
                                    syncStatus = "SYNCED"
                                )
                            }
                            database.studentAttendanceDao().insertAllAttendance(attendanceList)
                            _syncState.value = SyncState.Success("Real-time Attendance synced (${attendanceList.size} records)")
                        }
                    }
                }

            // Listen to student_performance changes in Firestore
            performanceListener = firestore.collection(COLLECTION_PERFORMANCE)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.e(TAG, "Performance listener failed: ", e)
                        _syncState.value = SyncState.Error("Firestore Performance sync offline: ${e.localizedMessage}")
                        return@addSnapshotListener
                    }

                    if (snapshot != null && !snapshot.isEmpty) {
                        scope.launch {
                            val performanceList = snapshot.documents.mapNotNull { doc ->
                                val admNo = doc.id
                                val name = doc.getString("studentName") ?: ""
                                val grade = doc.getString("gradeLevel") ?: "Grade 10"
                                val stream = doc.getString("stream") ?: "East"
                                val gender = doc.getString("gender") ?: "Male"
                                val json = doc.getString("subjectScoresJson") ?: "[]"
                                val avg = doc.getDouble("annualAverage") ?: 0.0
                                val rating = doc.getString("overallRating") ?: "Proficient"
                                StudentPerformanceEntity(
                                    admissionNo = admNo,
                                    studentName = name,
                                    gradeLevel = grade,
                                    stream = stream,
                                    gender = gender,
                                    subjectScoresJson = json,
                                    annualAverage = avg,
                                    overallRating = rating,
                                    syncStatus = "SYNCED"
                                )
                            }
                            database.studentPerformanceDao().insertAllPerformance(performanceList)
                            _syncState.value = SyncState.Success("Real-time Performance synced (${performanceList.size} records)")
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start real-time sync: ", e)
            _syncState.value = SyncState.Error("Real-time sync running in local-only mode")
        }
    }

    /**
     * Stop snapshot listeners
     */
    fun stopRealtimeSync() {
        attendanceListener?.remove()
        performanceListener?.remove()
        attendanceListener = null
        performanceListener = null
    }

    /**
     * Sync attendance update locally to Room and remotely to Firestore
     */
    suspend fun saveAttendanceRecord(attendance: StudentAttendance) {
        val entity = StudentAttendanceEntity.fromDomainModel(attendance, syncStatus = "PENDING")
        database.studentAttendanceDao().insertAttendance(entity)

        try {
            val map = hashMapOf(
                "admissionNo" to attendance.admissionNo,
                "studentName" to attendance.studentName,
                "date" to attendance.date,
                "status" to attendance.status,
                "note" to attendance.note,
                "lastUpdated" to System.currentTimeMillis()
            )
            firestore.collection(COLLECTION_ATTENDANCE)
                .document(attendance.id)
                .set(map, SetOptions.merge())
                .addOnSuccessListener {
                    scope.launch {
                        database.studentAttendanceDao().insertAttendance(entity.copy(syncStatus = "SYNCED"))
                        _syncState.value = SyncState.Success("Attendance record synced to cloud")
                    }
                }
                .addOnFailureListener { err ->
                    _syncState.value = SyncState.Error("Saved locally. Firestore sync pending: ${err.localizedMessage}")
                }
        } catch (e: Exception) {
            Log.e(TAG, "Firestore write error for attendance", e)
            _syncState.value = SyncState.Error("Saved locally. Firestore offline")
        }
    }

    /**
     * Sync batch attendance update to Room and Firestore
     */
    suspend fun saveBatchAttendanceRecords(list: List<StudentAttendance>) {
        val entities = list.map { StudentAttendanceEntity.fromDomainModel(it, syncStatus = "PENDING") }
        database.studentAttendanceDao().insertAllAttendance(entities)

        try {
            val batch = firestore.batch()
            list.forEach { item ->
                val ref = firestore.collection(COLLECTION_ATTENDANCE).document(item.id)
                val map = hashMapOf(
                    "admissionNo" to item.admissionNo,
                    "studentName" to item.studentName,
                    "date" to item.date,
                    "status" to item.status,
                    "note" to item.note,
                    "lastUpdated" to System.currentTimeMillis()
                )
                batch.set(ref, map, SetOptions.merge())
            }
            batch.commit()
                .addOnSuccessListener {
                    scope.launch {
                        val syncedEntities = list.map { StudentAttendanceEntity.fromDomainModel(it, syncStatus = "SYNCED") }
                        database.studentAttendanceDao().insertAllAttendance(syncedEntities)
                        _syncState.value = SyncState.Success("Batch attendance synced to cloud")
                    }
                }
                .addOnFailureListener { err ->
                    _syncState.value = SyncState.Error("Saved locally. Cloud sync failed: ${err.localizedMessage}")
                }
        } catch (e: Exception) {
            Log.e(TAG, "Batch attendance sync error", e)
            _syncState.value = SyncState.Error("Saved locally. Firestore offline")
        }
    }

    /**
     * Sync student performance record to Room and Firestore
     */
    suspend fun saveStudentPerformance(student: CbcClassStudent) {
        val entity = StudentPerformanceEntity.fromDomainModel(student, syncStatus = "PENDING")
        database.studentPerformanceDao().insertPerformance(entity)

        try {
            val map = hashMapOf(
                "admissionNo" to student.admissionNo,
                "studentName" to student.studentName,
                "gradeLevel" to student.gradeLevel,
                "stream" to student.stream,
                "gender" to student.gender,
                "subjectScoresJson" to entity.subjectScoresJson,
                "annualAverage" to student.annualAverage,
                "overallRating" to student.overallRating,
                "lastUpdated" to System.currentTimeMillis()
            )
            firestore.collection(COLLECTION_PERFORMANCE)
                .document(student.admissionNo)
                .set(map, SetOptions.merge())
                .addOnSuccessListener {
                    scope.launch {
                        database.studentPerformanceDao().insertPerformance(entity.copy(syncStatus = "SYNCED"))
                        _syncState.value = SyncState.Success("Performance updated in cloud")
                    }
                }
                .addOnFailureListener { err ->
                    _syncState.value = SyncState.Error("Saved locally. Cloud sync pending: ${err.localizedMessage}")
                }
        } catch (e: Exception) {
            Log.e(TAG, "Firestore write error for performance", e)
            _syncState.value = SyncState.Error("Saved locally. Firestore offline")
        }
    }

    /**
     * Sync full roster of student performance
     */
    suspend fun saveBatchStudentPerformance(roster: List<CbcClassStudent>) {
        val entities = roster.map { StudentPerformanceEntity.fromDomainModel(it, syncStatus = "PENDING") }
        database.studentPerformanceDao().insertAllPerformance(entities)

        try {
            val batch = firestore.batch()
            roster.forEach { student ->
                val ref = firestore.collection(COLLECTION_PERFORMANCE).document(student.admissionNo)
                val entity = StudentPerformanceEntity.fromDomainModel(student, syncStatus = "PENDING")
                val map = hashMapOf(
                    "admissionNo" to student.admissionNo,
                    "studentName" to student.studentName,
                    "gradeLevel" to student.gradeLevel,
                    "stream" to student.stream,
                    "gender" to student.gender,
                    "subjectScoresJson" to entity.subjectScoresJson,
                    "annualAverage" to student.annualAverage,
                    "overallRating" to student.overallRating,
                    "lastUpdated" to System.currentTimeMillis()
                )
                batch.set(ref, map, SetOptions.merge())
            }
            batch.commit()
                .addOnSuccessListener {
                    scope.launch {
                        val syncedEntities = roster.map { StudentPerformanceEntity.fromDomainModel(it, syncStatus = "SYNCED") }
                        database.studentPerformanceDao().insertAllPerformance(syncedEntities)
                        _syncState.value = SyncState.Success("Full roster performance synced to cloud")
                    }
                }
                .addOnFailureListener { err ->
                    _syncState.value = SyncState.Error("Saved locally. Cloud sync failed: ${err.localizedMessage}")
                }
        } catch (e: Exception) {
            Log.e(TAG, "Batch performance sync error", e)
            _syncState.value = SyncState.Error("Saved locally. Firestore offline")
        }
    }
}
