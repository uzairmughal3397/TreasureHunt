package com.treasure.hunting.activities.dataModels.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_tasks")
class SavedTask {
    @PrimaryKey(autoGenerate = true)
    var taskId: Int? = null

    var lat:String?=null
    var lng:String?=null
    var imageName:String?=null
    var imageId:Int=0
    var imagDes:String?=null
    var taskStatus:String?=null
    var time:String?=null


}