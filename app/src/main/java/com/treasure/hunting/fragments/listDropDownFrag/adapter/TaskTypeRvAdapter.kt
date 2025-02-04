package com.treasure.hunting.fragments.listDropDownFrag.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.treasure.hunting.R
import com.treasure.hunting.activities.dataModels.TaskType
import com.treasure.hunting.databinding.TaskTypeRvLayoutBinding
import com.treasure.hunting.fragments.listDropDownFrag.callBacks.TaskTypeCallBack

class TaskTypeRvAdapter(val callBack: TaskTypeCallBack, val arrayList: ArrayList<TaskType>) :
    RecyclerView.Adapter<TaskTypeRvAdapter.ViewHolderClass>() {
    private var rowIndex = 0

    inner class ViewHolderClass(private val itemBinding: TaskTypeRvLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindData(position: Int, taskType: TaskType) {

            itemBinding.rbTaskType.text=taskType.iconName
            itemBinding.imageView.setImageResource(taskType.iconId)

            itemBinding.rbTaskType.setOnClickListener {
                rowIndex = position
                notifyDataSetChanged()
                callBack.onTypeSelected(taskType)
            }
            itemBinding.rbTaskType.isChecked = rowIndex == position

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        return ViewHolderClass(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.task_type_rv_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        holder.bindData(position,arrayList[position])
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

}