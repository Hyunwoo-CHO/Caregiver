package com.hsilveredu.caregiver.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Question::class, Personal::class], version = 1)
abstract class AppDatabase : RoomDatabase(){
    abstract fun myDao() : MyDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context) : AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context) : AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "Caregiver_App_Database_V1").build()
        }
    }
}