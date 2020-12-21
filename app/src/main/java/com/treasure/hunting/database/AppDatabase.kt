package com.treasure.hunting.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.treasure.hunting.activities.dataModels.database.SavedTask
import com.treasure.hunting.database.dao.SavedTaskDao

/*Database class which handles all the communication of activities with backend data*/
@Database(
    entities = [
        /*let of classes which are annotated with entity*/
        SavedTask::class
    ],
    /*Database Version*/
    version = 2,
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {
    /*Interfaces which are annotated with DAO*/
    abstract fun savedTaskDao(): SavedTaskDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        /*Building the database*/
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context,
            /*Database Name*/
            AppDatabase::class.java, "treasure_hunt.db"
        )
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }
}