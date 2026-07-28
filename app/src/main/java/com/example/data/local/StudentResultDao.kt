package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.StudentResult
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentResultDao {
    @Query("SELECT * FROM student_results ORDER BY downloadTimestamp DESC")
    fun getAllResults(): Flow<List<StudentResult>>

    @Query("SELECT * FROM student_results WHERE id = :id LIMIT 1")
    fun getResultById(id: String): Flow<StudentResult?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: StudentResult)

    @Delete
    suspend fun deleteResult(result: StudentResult)

    @Query("DELETE FROM student_results WHERE id = :id")
    suspend fun deleteResultById(id: String)

    @Query("SELECT * FROM student_results WHERE studentName LIKE '%' || :query || '%' OR rollNumber LIKE '%' || :query || '%' OR boardOrUniversity LIKE '%' || :query || '%' ORDER BY downloadTimestamp DESC")
    fun searchResults(query: String): Flow<List<StudentResult>>
}
