package com.treasure.hunting.fragments.listOfGeoCaches.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.treasure.hunting.R
import com.treasure.hunting.activities.dataModels.database.SavedTask
import com.treasure.hunting.databinding.GeoCacheTypeRvLayoutBinding

class GeoCacheRvAdapter(private val arrayList: MutableList<SavedTask>) :
    RecyclerView.Adapter<GeoCacheRvAdapter.ViewHolderClass>() {

    inner class ViewHolderClass(private val itemBinding: GeoCacheTypeRvLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindData(taskType: SavedTask) {
            itemBinding.tvName.text=taskType.imagDes
            itemBinding.ivIcon.setImageResource(taskType.imageId)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        return ViewHolderClass(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.geo_cache_type_rv_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        holder.bindData(arrayList[position])
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

}