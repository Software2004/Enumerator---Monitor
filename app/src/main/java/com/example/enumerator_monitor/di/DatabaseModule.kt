package com.example.enumerator_monitor.di

import android.content.Context
import androidx.room.Room
import com.example.enumerator_monitor.data.AppDatabase
import com.example.enumerator_monitor.data.MIGRATION_1_2
import com.example.enumerator_monitor.data.SurveyEntryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "enumerator_monitor.db")
            .addMigrations(MIGRATION_1_2)
            .build()

    @Provides
    fun provideSurveyEntryDao(db: AppDatabase): SurveyEntryDao = db.surveyEntryDao()
}


