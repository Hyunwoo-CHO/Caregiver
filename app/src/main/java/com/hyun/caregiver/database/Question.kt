package com.hyun.caregiver.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class Question
    (
    @PrimaryKey(autoGenerate = true) val uid : Int?,
    @ColumnInfo(name = "title") var title : String,
    @ColumnInfo(name = "img", defaultValue = "") var img : String,
    @ColumnInfo(name = "opt_a") var opt_a : String,
    @ColumnInfo(name = "opt_b") var opt_b : String,
    @ColumnInfo(name = "opt_c") var opt_c : String,
    @ColumnInfo(name = "opt_d") var opt_d : String,
    @ColumnInfo(name = "opt_e") var opt_e : String,
    @ColumnInfo(name = "answer") var answer : Int,
    @ColumnInfo(name = "comment", defaultValue = "No Comment") var comment : String,
    @ColumnInfo(name = "comment_img", defaultValue = "") var comment_img : String,
    @ColumnInfo(name = "cid") var cid : String,
    @ColumnInfo(name = "classify") var classify : String,
    @ColumnInfo(name = "solved", defaultValue = "false") var solved : Boolean
)