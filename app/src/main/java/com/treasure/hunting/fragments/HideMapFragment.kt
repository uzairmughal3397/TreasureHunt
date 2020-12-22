package com.treasure.hunting.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.treasure.hunting.R
import com.treasure.hunting.activities.addTaskActivity.AddTask
import com.treasure.hunting.databinding.FragmentHideMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar


class HideMapFragment : Fragment(), LocationListener {

    lateinit var lat: String
    lateinit var lng: String
    private lateinit var mapFragment: SupportMapFragment
    private var locationManager: LocationManager? = null
    private val MIN_TIME: Long = 10
    private val MIN_DISTANCE = 10f
    var locationLiveData = MutableLiveData<Location>()
    lateinit var fragMapsBinding: FragmentHideMapBinding

    private val onMapReadyCallback = OnMapReadyCallback { googleMap ->

        //Realtime Observer which will trigger when locationLiveData gets location.
        locationLiveData.observe(this, {
            if (fragMapsBinding.tvTitle.isVisible)
                hideLoadingView()

            val latLng = LatLng(it.latitude, it.longitude)
            lat = it.latitude.toString()
            lng = it.longitude.toString()

            //to move camera to current location
            val cameraUpdateFactory = CameraUpdateFactory.newLatLngZoom(latLng, 20f)

            //clear every other marker on map
            googleMap.clear()

            //customize the marker
            val marker = MarkerOptions().position(latLng)
            marker.title("My Current Location")
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
            googleMap.addMarker(marker)
            googleMap.animateCamera(cameraUpdateFactory)

            fragMapsBinding.btnNewTreasure.setOnClickListener {
                startActivity(Intent(requireActivity(), AddTask::class.java).putExtra("lat",lat)
                    .putExtra("lng",lng))
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragMapsBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_hide_map, container, false)
        return fragMapsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        //getting Location Manager
        locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager



        mapFragment.getMapAsync(onMapReadyCallback)

        //Get Current Location continuously after specific times.
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        locationManager!!.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            MIN_TIME,
            MIN_DISTANCE,
            this
        )

        fragMapsBinding.btnCurrentLoc.setOnClickListener {
            showLoadingView()
            locationManager!!.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this
            )
        }



        /*Getting last known location and trying to get current location*/
//        getLastLocation()

    }

    private fun getLastLocation() {

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        //show last known location of user when location manager is busy in finding current location.
        locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)?.let {
            locationLiveData.value = it
            hideLoadingView()
        }

        //Call onMapReadyCallback
        mapFragment.getMapAsync(onMapReadyCallback)

        //Get Current Location continuously after specific times.
        locationManager!!.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            MIN_TIME,
            MIN_DISTANCE,
            this
        )

        fragMapsBinding.btnCurrentLoc.setOnClickListener {
            locationManager!!.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this
            )
        }
    }

    //when ever the location changes it will assign to liveData object
    override fun onLocationChanged(location: Location) {
        locationLiveData.value = location
        hideLoadingView()
        mapFragment.getMapAsync(onMapReadyCallback)
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String) {
    }

    override fun onProviderDisabled(provider: String) {
        //if Provider is not available
        showSnackBar("Unable to get location provider")
    }

    private fun showSnackBar(msg: String) {
        val snackBar =
            Snackbar.make(fragMapsBinding.root, msg, Snackbar.LENGTH_LONG)

        snackBar.view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.yellow))
        snackBar.show()
    }

    private fun hideLoadingView() {
        fragMapsBinding.tvTitle.visibility = View.GONE
        fragMapsBinding.pbLoading.visibility = View.GONE
    }

    private fun showLoadingView() {
        fragMapsBinding.tvTitle.visibility = View.VISIBLE
        fragMapsBinding.pbLoading.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onStop() {
        super.onStop()
    }
}