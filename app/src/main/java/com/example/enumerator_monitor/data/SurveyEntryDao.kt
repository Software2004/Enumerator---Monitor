package com.example.enumerator_monitor.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SurveyEntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: SurveyEntry): Long

    @Query("SELECT * FROM survey_entries ORDER BY createdAt DESC")
    fun getAllEntries(): Flow<List<SurveyEntry>>

    @Query("SELECT MAX(houseNo) FROM survey_entries")
    suspend fun getLastHouseNo(): Int?

    @Query("SELECT * FROM survey_entries WHERE houseNo LIKE :query OR respondentName LIKE :query ORDER BY createdAt DESC")
    fun search(query: String): Flow<List<SurveyEntry>>

    @Query("SELECT * FROM survey_entries WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): SurveyEntry?
}


