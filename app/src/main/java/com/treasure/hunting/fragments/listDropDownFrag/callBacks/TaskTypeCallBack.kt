package com.treasure.hunting.fragments.listDropDownFrag.callBacks

import com.treasure.hunting.activities.dataModels.TaskType

interface TaskTypeCallBack {
    fun onTypeSelected(taskType: TaskType)
}