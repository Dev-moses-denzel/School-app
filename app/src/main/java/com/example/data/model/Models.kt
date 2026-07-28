package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SubjectScore(
    val code: String,
    val name: String,
    val credits: Int,
    val internalMarks: Int,
    val externalMarks: Int,
    val totalMarks: Int,
    val maxMarks: Int = 100,
    val grade: String,
    val gradePoint: Double,
    val isPass: Boolean = true
)

@Entity(tableName = "student_results")
data class StudentResult(
    @PrimaryKey val id: String, // RollNo_Term
    val rollNumber: String,
    val registrationNumber: String,
    val studentName: String,
    val institutionName: String,
    val boardOrUniversity: String,
    val courseOrProgram: String,
    val branchOrStream: String,
    val examTerm: String,
    val sessionYear: String,
    val cgpa: Double,
    val totalPercentage: Double,
    val totalCredits: Int,
    val earnedCredits: Int,
    val overallStatus: String, // "PASSED WITH DISTINCTION", "PASSED", "PROMOTED", "FAILED"
    val division: String, // "First Class with Distinction", "First Class", "Second Class"
    val publishDate: String,
    val verificationHash: String,
    val subjectsJson: String, // Serialized List<SubjectScore>
    val downloadTimestamp: Long = System.currentTimeMillis(),
    val pdfUri: String? = null
)

data class PortalBoard(
    val id: String,
    val name: String,
    val code: String,
    val location: String,
    val category: String, // "University", "Board", "Technical", "School"
    val isOnline: Boolean = true
)
