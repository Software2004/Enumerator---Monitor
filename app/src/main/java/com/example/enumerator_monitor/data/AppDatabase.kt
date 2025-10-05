package com.example.enumerator_monitor.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [SurveyEntry::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun surveyEntryDao(): SurveyEntryDao
}


