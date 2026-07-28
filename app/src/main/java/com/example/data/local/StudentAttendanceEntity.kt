package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.remote.StudentAttendance

@Entity(tableName = "student_attendance")
data class StudentAttendanceEntity(
    @PrimaryKey val id: String,
    val admissionNo: String,
    val studentName: String,
    val date: String,
    val status: String,
    val note: String = "",
    val syncStatus: String = "SYNCED",
    val lastUpdated: Long = System.currentTimeMillis()
) {
    fun toDomainModel(): StudentAttendance {
        return StudentAttendance(
            id = id,
            admissionNo = admissionNo,
            studentName = studentName,
            date = date,
            status = status,
            note = note
        )
    }

    companion object {
        fun fromDomainModel(domain: StudentAttendance, syncStatus: String = "SYNCED"): StudentAttendanceEntity {
            return StudentAttendanceEntity(
                id = domain.id,
                admissionNo = domain.admissionNo,
                studentName = domain.studentName,
                date = domain.date,
                status = domain.status,
                note = domain.note,
                syncStatus = syncStatus
            )
        }
    }
}
