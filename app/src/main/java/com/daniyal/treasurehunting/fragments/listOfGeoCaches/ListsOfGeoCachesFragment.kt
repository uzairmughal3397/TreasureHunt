package com.daniyal.treasurehunting.fragments.listOfGeoCaches

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.daniyal.treasurehunting.R
import com.daniyal.treasurehunting.database.AppDatabase
import com.daniyal.treasurehunting.databinding.FragmentListsOfGeoCachesBinding
import com.daniyal.treasurehunting.fragments.listOfGeoCaches.adapter.GeoCacheRvAdapter
import com.daniyal.treasurehunting.helpers.AppConstants

class ListsOfGeoCachesFragment : Fragment() {
    private lateinit var type: String
    lateinit var binding:FragmentListsOfGeoCachesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            type = it.getString("type")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_lists_of_geo_caches, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvType.text=type

         if (type==AppConstants.UN_EXPLORED_STATUS){
            AppDatabase.invoke(requireContext()).savedTaskDao().getAllSavedTaskByStatus(type)
                .observe(this, {
                    binding.rvLists.adapter=GeoCacheRvAdapter(it)
                })
        }
        else if (type==AppConstants.COMPLETED_EXPLORED_STATUS){
             AppDatabase.invoke(requireContext()).savedTaskDao().getAllSavedTaskByStatus(type)
                 .observe(this, {
                     binding.rvLists.adapter=GeoCacheRvAdapter(it)
                 })

         }

    }

}