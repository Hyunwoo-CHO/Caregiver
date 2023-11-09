package com.hyun.caregiver.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "personal")
data class Personal (
    @PrimaryKey var qid : Int,
    @ColumnInfo(name = "answer") var answer : Int,
    @ColumnInfo(name = "m_answer") var m_answer : Int,
    @ColumnInfo(name = "correction") var correction : Boolean
)