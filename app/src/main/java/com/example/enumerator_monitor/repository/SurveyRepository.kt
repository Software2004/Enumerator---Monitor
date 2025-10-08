package com.example.enumerator_monitor.repository

import com.example.enumerator_monitor.data.SurveyEntry
import com.example.enumerator_monitor.data.SurveyEntryDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SurveyRepository @Inject constructor(
    private val surveyEntryDao: SurveyEntryDao
) {
    suspend fun addEntry(entry: SurveyEntry): Long = surveyEntryDao.insert(entry)

    fun getAllEntries(): Flow<List<SurveyEntry>> = surveyEntryDao.getAllEntries()

    suspend fun getNextHouseNo(): Int {
        val last = surveyEntryDao.getLastHouseNo() ?: 0
        return last + 1
    }

    fun search(queryText: String): Flow<List<SurveyEntry>> =
        surveyEntryDao.search("%$queryText%")

    suspend fun getById(id: Long): SurveyEntry? = surveyEntryDao.getById(id)

    suspend fun updateEntry(entry: SurveyEntry) = surveyEntryDao.update(entry)

    suspend fun deleteEntry(entry: SurveyEntry) = surveyEntryDao.delete(entry)
}


