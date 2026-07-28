package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.SubjectScore
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class Converters {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val type = Types.newParameterizedType(List::class.java, SubjectScore::class.java)
    private val adapter = moshi.adapter<List<SubjectScore>>(type)

    @TypeConverter
    fun fromSubjectList(list: List<SubjectScore>?): String {
        if (list == null) return "[]"
        return adapter.toJson(list)
    }

    @TypeConverter
    fun toSubjectList(json: String?): List<SubjectScore> {
        if (json.isNullOrEmpty()) return emptyList()
        return try {
            adapter.fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
