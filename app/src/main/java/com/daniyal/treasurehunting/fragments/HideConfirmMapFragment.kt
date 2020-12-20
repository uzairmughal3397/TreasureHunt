package com.daniyal.treasurehunting.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.daniyal.treasurehunting.R
import com.daniyal.treasurehunting.activities.dataModels.database.SavedTask
import com.daniyal.treasurehunting.database.AppDatabase
import com.daniyal.treasurehunting.helpers.AppConstants
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class HideConfirmMapFragment : Fragment() {


    var lat = ""
    var lng = ""
    var taskDes = ""
    var imageId = 0
    var imageName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //getting data from previous fragment
        arguments.let {
            lat = it!!.getString("lat", "")
            lng = it.getString("lng", "")
            taskDes = it.getString("taskDes", "")
            imageId = it.getInt("imageId", 0)
            imageName = it.getString("imageName", "")
        }
    }


    /*Checking when will map loaded*/
    private val callback = OnMapReadyCallback { googleMap ->
        val selectedPosition = LatLng(lat.toDouble(), lng.toDouble())
        val cameraUpdateFactory = CameraUpdateFactory.newLatLngZoom(selectedPosition, 20f)
       /*Adding marker to position*/
        val marker = MarkerOptions().position(selectedPosition)
        marker.title(imageName)
        marker.snippet(taskDes)
        marker.icon(BitmapDescriptorFactory.fromResource(imageId))
        marker.visible(true)
        googleMap.addMarker(marker)
        googleMap.animateCamera(cameraUpdateFactory)

        /*Save data to local database*/
        val savedTask=SavedTask()
        savedTask.imagDes=taskDes
        savedTask.imageName=imageName
        savedTask.imageId=imageId
        savedTask.lat=lat
        savedTask.lng=lng
        savedTask.taskStatus=AppConstants.UN_EXPLORED_STATUS
        AppDatabase.invoke(requireContext()).savedTaskDao().addTask(savedTask)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hide_confirm_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}