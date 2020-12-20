package com.daniyal.treasurehunting.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.daniyal.treasurehunting.activities.dataModels.database.SavedTask

@Dao
interface SavedTaskDao {

    @Query("SELECT * FROM saved_tasks where taskStatus=:taskType")
    fun getAllSavedTaskByStatus(taskType: String): LiveData<MutableList<SavedTask>>

    @Query("SELECT * FROM saved_tasks")
    fun getAllSavedTask(): LiveData<MutableList<SavedTask>>

    @Query("update saved_tasks set taskStatus=:completedTask where lat=:lat and lng=:lng")
    fun updateTaskStatus(completedTask:String,lat:String,lng:String)

    @Insert
    fun addTask(vararg savedTask: SavedTask)


}