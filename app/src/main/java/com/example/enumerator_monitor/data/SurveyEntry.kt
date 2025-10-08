package com.example.enumerator_monitor.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "survey_entries")
data class SurveyEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val houseNo: Int,
    val respondentName: String,
    val familyMembers: Int,
    val houseType: String,
    val ownsBuffalo: Boolean,
    val ownsCow: Boolean,
    val ownsGoat: Boolean,
    val ownsSheep: Boolean,
    val hasInfantChild: Boolean,
    val familyType: String,
    val chitsCount: Int,
    val phoneNumber: String
)


