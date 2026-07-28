package com.example.data.sync

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AlertCategory {
    GRADE_UPLOAD,
    HOMEWORK_ASSIGNED,
    ASSIGNMENT_PDF_UPLOAD,
    GENERAL_ANNOUNCEMENT
}

data class AcademicAlert(
    val id: String = "",
    val title: String = "",
    val body: String = "",
    val category: AlertCategory = AlertCategory.GRADE_UPLOAD,
    val studentAdmissionNo: String = "",
    val subject: String = "",
    val teacherName: String = "",
    val attachmentUrl: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

class FcmNotificationManager(
    private val context: Context,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val messaging: FirebaseMessaging by lazy { FirebaseMessaging.getInstance() }

    private val _fcmToken = MutableStateFlow<String?>(null)
    val fcmToken: StateFlow<String?> = _fcmToken.asStateFlow()

    private val _alertsList = MutableStateFlow<List<AcademicAlert>>(emptyList())
    val alertsList: StateFlow<List<AcademicAlert>> = _alertsList.asStateFlow()

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    companion object {
        private const val TAG = "FcmNotificationManager"
        private const val COLLECTION_ALERTS = "academic_alerts"
        const val TOPIC_ALL_STUDENTS = "all_students"
        const val TOPIC_GRADES = "grade_updates"
        const val TOPIC_ASSIGNMENTS = "assignment_pdfs"
    }

    init {
        subscribeToTopics()
        fetchDeviceToken()
        startRealtimeAlertsListener()
    }

    private fun subscribeToTopics() {
        try {
            messaging.subscribeToTopic(TOPIC_ALL_STUDENTS)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Subscribed to FCM topic: $TOPIC_ALL_STUDENTS")
                    }
                }
            messaging.subscribeToTopic(TOPIC_GRADES)
            messaging.subscribeToTopic(TOPIC_ASSIGNMENTS)
        } catch (e: Exception) {
            Log.e(TAG, "Error subscribing to FCM topics", e)
        }
    }

    private fun fetchDeviceToken() {
        try {
            messaging.token.addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    val token = task.result
                    _fcmToken.value = token
                    Log.d(TAG, "Retrieved FCM Token: $token")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching FCM token", e)
        }
    }

    private fun startRealtimeAlertsListener() {
        try {
            firestore.collection(COLLECTION_ALERTS)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(20)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(TAG, "Firestore alerts listener error", error)
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val alerts = snapshot.documents.mapNotNull { doc ->
                            try {
                                val categoryStr = doc.getString("category") ?: "GRADE_UPLOAD"
                                val category = try {
                                    AlertCategory.valueOf(categoryStr)
                                } catch (e: Exception) {
                                    AlertCategory.GRADE_UPLOAD
                                }
                                AcademicAlert(
                                    id = doc.id,
                                    title = doc.getString("title") ?: "Academic Alert",
                                    body = doc.getString("body") ?: "",
                                    category = category,
                                    studentAdmissionNo = doc.getString("studentAdmissionNo") ?: "",
                                    subject = doc.getString("subject") ?: "",
                                    teacherName = doc.getString("teacherName") ?: "Class Teacher",
                                    attachmentUrl = doc.getString("attachmentUrl") ?: "",
                                    timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis(),
                                    isRead = doc.getBoolean("isRead") ?: false
                                )
                            } catch (e: Exception) {
                                null
                            }
                        }
                        _alertsList.value = alerts
                        _unreadCount.value = alerts.count { !it.isRead }
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start real-time alerts listener", e)
        }
    }

    /**
     * Called when a teacher uploads new grades for a student or class
     */
    fun sendGradeUploadAlert(
        studentName: String,
        admissionNo: String,
        subjectName: String,
        rating: String,
        teacherName: String = "CBC Teacher"
    ) {
        val alert = AcademicAlert(
            id = "ALERT_${System.currentTimeMillis()}",
            title = "📊 New Grade Uploaded: $subjectName",
            body = "$teacherName posted new CBC evaluation ($rating) for $studentName ($admissionNo).",
            category = AlertCategory.GRADE_UPLOAD,
            studentAdmissionNo = admissionNo,
            subject = subjectName,
            teacherName = teacherName,
            timestamp = System.currentTimeMillis()
        )
        dispatchAlertToFirestore(alert)
    }

    /**
     * Called when a teacher uploads new homework or assignment PDFs
     */
    fun sendAssignmentPdfAlert(
        title: String,
        subjectName: String,
        pdfFileName: String,
        pdfUrl: String,
        dueDate: String,
        teacherName: String = "CBC Teacher"
    ) {
        val alert = AcademicAlert(
            id = "PDF_${System.currentTimeMillis()}",
            title = "📄 New Assignment PDF Uploaded: $pdfFileName",
            body = "$teacherName uploaded $pdfFileName for $subjectName. Due: $dueDate. Tap to view document.",
            category = AlertCategory.ASSIGNMENT_PDF_UPLOAD,
            subject = subjectName,
            teacherName = teacherName,
            attachmentUrl = pdfUrl,
            timestamp = System.currentTimeMillis()
        )
        dispatchAlertToFirestore(alert)
    }

    /**
     * Called when a teacher posts new homework
     */
    fun sendHomeworkAlert(
        subjectName: String,
        taskDescription: String,
        dueDate: String,
        teacherName: String = "CBC Teacher"
    ) {
        val alert = AcademicAlert(
            id = "HW_${System.currentTimeMillis()}",
            title = "📝 Homework Assigned: $subjectName",
            body = "$taskDescription. Due Date: $dueDate.",
            category = AlertCategory.HOMEWORK_ASSIGNED,
            subject = subjectName,
            teacherName = teacherName,
            timestamp = System.currentTimeMillis()
        )
        dispatchAlertToFirestore(alert)
    }

    private fun dispatchAlertToFirestore(alert: AcademicAlert) {
        scope.launch {
            try {
                val map = hashMapOf(
                    "title" to alert.title,
                    "body" to alert.body,
                    "category" to alert.category.name,
                    "studentAdmissionNo" to alert.studentAdmissionNo,
                    "subject" to alert.subject,
                    "teacherName" to alert.teacherName,
                    "attachmentUrl" to alert.attachmentUrl,
                    "timestamp" to alert.timestamp,
                    "isRead" to alert.isRead
                )
                firestore.collection(COLLECTION_ALERTS)
                    .document(alert.id)
                    .set(map)
                    .addOnSuccessListener {
                        Log.d(TAG, "Alert broadcasted to Firestore/FCM stream: ${alert.id}")
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Error dispatching FCM alert", e)
            }
        }
    }

    fun markAlertAsRead(alertId: String) {
        scope.launch {
            try {
                firestore.collection(COLLECTION_ALERTS)
                    .document(alertId)
                    .update("isRead", true)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to mark alert as read", e)
            }
        }
    }
}
