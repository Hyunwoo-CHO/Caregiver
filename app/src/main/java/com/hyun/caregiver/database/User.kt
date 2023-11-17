package com.hyun.caregiver.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User (
    @PrimaryKey var uid : Int,
    @ColumnInfo(name = "id") var id : String,
    @ColumnInfo(name = "payment") var payment : Boolean
)