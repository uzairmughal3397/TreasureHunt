package com.daniyal.treasurehunting.fragments.listDropDownFrag.callBacks

import com.daniyal.treasurehunting.activities.dataModels.TaskType

interface TaskTypeCallBack {
    fun onTypeSelected(taskType: TaskType)
}