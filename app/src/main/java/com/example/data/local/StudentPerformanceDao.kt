package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentPerformanceDao {
    @Query("SELECT * FROM student_performance ORDER BY studentName ASC")
    fun getAllPerformance(): Flow<List<StudentPerformanceEntity>>

    @Query("SELECT * FROM student_performance WHERE admissionNo = :admissionNo LIMIT 1")
    fun getPerformanceByAdmission(admissionNo: String): Flow<StudentPerformanceEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerformance(performance: StudentPerformanceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPerformance(list: List<StudentPerformanceEntity>)
}
