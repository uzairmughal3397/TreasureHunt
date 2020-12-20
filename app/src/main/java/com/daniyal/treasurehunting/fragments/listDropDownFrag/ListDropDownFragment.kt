package com.daniyal.treasurehunting.fragments.listDropDownFrag

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import com.daniyal.treasurehunting.R
import com.daniyal.treasurehunting.activities.dataModels.TaskType
import com.daniyal.treasurehunting.databinding.FragmentListDropDownBinding
import com.daniyal.treasurehunting.fragments.HideConfirmMapFragment
import com.daniyal.treasurehunting.fragments.listDropDownFrag.adapter.TaskTypeRvAdapter
import com.daniyal.treasurehunting.fragments.listOfGeoCaches.adapter.GeoCacheRvAdapter
import com.daniyal.treasurehunting.fragments.listDropDownFrag.callBacks.TaskTypeCallBack

class ListDropDownFragment : Fragment(), TaskTypeCallBack {

    lateinit var binding:FragmentListDropDownBinding

    var lat=""
    var lng=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*getting data from previous fragment*/
        arguments.let {
            lat=it!!.getString("lat","")
            lng= it.getString("lng","")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.inflate(layoutInflater,R.layout.fragment_list_drop_down,container,false)

        /*Initializing the list of categories*/
        val arrayList= arrayListOf(
            TaskType(R.drawable.anchor_24px,"Anchor"),
            TaskType(R.drawable.book_24px,"Book"),
            TaskType(R.drawable.camera_24px,"Camera"),
            TaskType(R.drawable.create_24px,"Create"),
            TaskType(R.drawable.draw_24px,"Draw"),
            TaskType(R.drawable.eco_24px,"Eco"),
            TaskType(R.drawable.event_24px,"Event"),
            TaskType(R.drawable.face_24px,"Face"),
            TaskType(R.drawable.flag_24px,"Flag"),
            TaskType(R.drawable.joga_24px,"Yoga"),
            TaskType(R.drawable.pets_24px,"Pets"),
            TaskType(R.drawable.play_24px,"Play"),
            TaskType(R.drawable.push_pin_24px,"Push Pin"),
            TaskType(R.drawable.puzzle_24px,"Puzzle"),
            TaskType(R.drawable.record_voice_24px,"Record Voice"),
            TaskType(R.drawable.school_24px,"School"),
            TaskType(R.drawable.secret_24px,"Secret"),
            TaskType(R.drawable.tennis_24px,"Tennis"),
            TaskType(R.drawable.tour_24px,"Tour")
        )

        binding.rvTaskType.adapter= TaskTypeRvAdapter(this,arrayList)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    /*Callback from Recyclerview's adapter*/
    override fun onTypeSelected(taskType: TaskType) {
        binding.btnAdd.setOnClickListener {
            val hideConfirmation = HideConfirmMapFragment()
            hideConfirmation.arguments= bundleOf(
                Pair("lat",lat),
                Pair("lng",lng),
                Pair("imageId",taskType.iconId),
                Pair("imageName",taskType.iconName),
                Pair("taskDes",binding.edtTask.text.toString()),
            )

            /*starting the confirmation fragment*/
            val transaction=activity!!.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.clMain, hideConfirmation, "fragmnetId")
            transaction.addToBackStack("hideFrag")
            transaction.commit()
        }
    }
}