package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.remote.CbcClassStudent
import com.example.data.remote.CbcStudentScore
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

@Entity(tableName = "student_performance")
data class StudentPerformanceEntity(
    @PrimaryKey val admissionNo: String,
    val studentName: String,
    val gradeLevel: String = "Grade 10",
    val stream: String = "East",
    val gender: String = "Male",
    val subjectScoresJson: String,
    val annualAverage: Double = 0.0,
    val overallRating: String = "Proficient",
    val syncStatus: String = "SYNCED",
    val lastUpdated: Long = System.currentTimeMillis()
) {
    fun toDomainModel(): CbcClassStudent {
        val scores = try {
            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            val type = Types.newParameterizedType(List::class.java, CbcStudentScore::class.java)
            val adapter = moshi.adapter<List<CbcStudentScore>>(type)
            adapter.fromJson(subjectScoresJson) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
        return CbcClassStudent(
            admissionNo = admissionNo,
            studentName = studentName,
            gradeLevel = gradeLevel,
            stream = stream,
            gender = gender,
            subjectScores = scores
        )
    }

    companion object {
        fun fromDomainModel(domain: CbcClassStudent, syncStatus: String = "SYNCED"): StudentPerformanceEntity {
            val json = try {
                val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                val type = Types.newParameterizedType(List::class.java, CbcStudentScore::class.java)
                val adapter = moshi.adapter<List<CbcStudentScore>>(type)
                adapter.toJson(domain.subjectScores)
            } catch (e: Exception) {
                "[]"
            }
            return StudentPerformanceEntity(
                admissionNo = domain.admissionNo,
                studentName = domain.studentName,
                gradeLevel = domain.gradeLevel,
                stream = domain.stream,
                gender = domain.gender,
                subjectScoresJson = json,
                annualAverage = domain.annualAverage,
                overallRating = domain.overallRating,
                syncStatus = syncStatus
            )
        }
    }
}
