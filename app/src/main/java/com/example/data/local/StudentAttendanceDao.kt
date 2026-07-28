package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentAttendanceDao {
    @Query("SELECT * FROM student_attendance ORDER BY date DESC")
    fun getAllAttendance(): Flow<List<StudentAttendanceEntity>>

    @Query("SELECT * FROM student_attendance WHERE admissionNo = :admissionNo ORDER BY date DESC")
    fun getAttendanceForStudent(admissionNo: String): Flow<List<StudentAttendanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: StudentAttendanceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAttendance(list: List<StudentAttendanceEntity>)

    @Query("DELETE FROM student_attendance WHERE id = :id")
    suspend fun deleteAttendanceById(id: String)
}
