package com.example.enumerator_monitor.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE survey_entries ADD COLUMN ownsAC INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE survey_entries ADD COLUMN ownsRefrigerator INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE survey_entries ADD COLUMN ownsMotorcycle INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE survey_entries ADD COLUMN ownsScooter INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE survey_entries ADD COLUMN ownsCar INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE survey_entries ADD COLUMN ownsTractor INTEGER NOT NULL DEFAULT 0")
    }
}

@Database(
    entities = [SurveyEntry::class],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun surveyEntryDao(): SurveyEntryDao
}


