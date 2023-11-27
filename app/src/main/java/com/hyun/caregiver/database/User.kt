package com.hyun.caregiver.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User (
    @PrimaryKey var uid : String,
    @ColumnInfo(name = "nickname") var nickname : String,
    @ColumnInfo(name = "email") var email : String,
    @ColumnInfo(name = "profile") var profile : String,
    @ColumnInfo(name = "payment") var payment : Boolean
)