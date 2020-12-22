package com.treasure.hunting.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.treasure.hunting.R
import com.treasure.hunting.database.AppDatabase
import com.treasure.hunting.databinding.FragmentSearchMapsBinding
import com.treasure.hunting.helpers.AppConstants
import com.treasure.hunting.helpers.SharedPrefHelper
import java.util.*

class SearchMapsFragment : Fragment(), LocationListener {

    lateinit var lat: String
    lateinit var lng: String
    private lateinit var mapFragment: SupportMapFragment
    private var locationManager: LocationManager? = null
    private val MIN_TIME: Long = 10
    private val MIN_DISTANCE = 10f
    var locationLiveData = MutableLiveData<Location>()
    private lateinit var fragMapsBinding: FragmentSearchMapsBinding
    private lateinit var selectedLatLng: LatLng
    var tts: TextToSpeech? = null
    lateinit var gMap: GoogleMap
    val nearbyTreausre = arrayListOf<Int>()

    private val onMapReadyCallback = OnMapReadyCallback { googleMap ->

        //Realtime Observer which will trigger when locationLiveData gets location.
        locationLiveData.observe(this, {

            gMap = googleMap
            setUpMarkersFromDb()

            if (fragMapsBinding.tvTitle.isVisible)
                hideLoadingView()
            val latLng = LatLng(it.latitude, it.longitude)
            lat = it.latitude.toString()
            lng = it.longitude.toString()

            //to move camera to current location
            val cameraUpdateFactory = CameraUpdateFactory.newLatLngZoom(latLng, 16f)

            //clear every other marker on map
            gMap.clear()

            //customize the marker
            val marker = MarkerOptions().position(latLng)
            marker.title("My Current Location")
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
            gMap.addMarker(marker)
            gMap.animateCamera(cameraUpdateFactory)


            fragMapsBinding.btnComplete.hide()

            fragMapsBinding.btnComplete.setOnClickListener {
                val selectedLat = selectedLatLng.latitude
                val selectedLng = selectedLatLng.longitude
                AppDatabase.invoke(requireContext()).savedTaskDao()
                    .updateTaskStatus(
                        AppConstants.COMPLETED_EXPLORED_STATUS,
                        selectedLat.toString(),
                        selectedLng.toString()
                    )

                val alertBuilder = AlertDialog.Builder(requireContext())
                alertBuilder.setMessage("Treasure Completed")
                alertBuilder.setCancelable(true)
                alertBuilder.setPositiveButton("Ok") { dialogInterface, _ -> dialogInterface.cancel() }
                alertBuilder.create().show()


                gMap.clear()
                setUpMarkersFromDb()

                val marker = MarkerOptions().position(latLng)
                marker.title("My Current Location")
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                gMap.addMarker(marker)
                gMap.animateCamera(cameraUpdateFactory)
            }
        })
    }

    private fun setUpMarkersFromDb() {

        /*Setting up the radius*/
        val radius =
            SharedPrefHelper().readString(requireContext(), AppConstants.RADIUS_KEY)!!.toInt()

        /*Getting the list of un explored task*/
        val unExploredList =
            AppDatabase.invoke(requireContext()).savedTaskDao()
                .getAllSavedTaskByStatus(AppConstants.UN_EXPLORED_STATUS)

        unExploredList.observe(this, { tasksLists ->
            tasksLists.forEach {
                if (calculateDistance(
                        it.lat!!.toFloat(),
                        it.lng!!.toFloat(),
                        lat.toFloat(),
                        lng.toFloat()
                    )
                ) {
                    /*Setting up the info into marker*/
                    val marker = MarkerOptions().position(
                        LatLng(
                            it.lat!!.toDouble(),
                            it.lng!!.toDouble()
                        )
                    )


                    if (!nearbyTreausre.contains(it.taskId)) {
                        nearbyTreausre.add(it.taskId!!)
                        showMessage("There a treasure near you")
                    }

                    marker.title(it.imageName)
                    marker.snippet(it.imagDes)
                    marker.icon(BitmapDescriptorFactory.fromResource(it.imageId))
                    gMap.addMarker(marker)

                    gMap.setOnInfoWindowClickListener { selectedMarker ->
                        selectedLatLng = selectedMarker.position
                        fragMapsBinding.btnComplete.show()
                    }

                    /*Drawing the circles around marker*/
                    drawGeofence(
                        LatLng(
                            it.lat!!.toDouble(),
                            it.lng!!.toDouble()
                        ), radius, gMap
                    )

                }
            }

        })
    }

    private var geoFenceLimits: Circle? = null

    //Function to draw circle
    private fun drawGeofence(loc: LatLng, radius: Int, googleMap: GoogleMap) {
        val circleOptions = CircleOptions()
            .center(loc)
            .strokeColor(Color.argb(50, 14, 143, 207))
            .fillColor(Color.argb(100, 52, 174, 235))
            .radius(radius.toDouble())
        geoFenceLimits = googleMap.addCircle(circleOptions)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragMapsBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_search_maps,
            container,
            false
        )
        return fragMapsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapFragment = (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!
        locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        tts = TextToSpeech(requireContext()) {}


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

    }


    //when ever the location changes it will assign to liveData object
    override fun onLocationChanged(location: Location) {
        locationLiveData.value = location
        hideLoadingView()
        mapFragment.getMapAsync(onMapReadyCallback)
//        locationManager!!.removeUpdates(this)
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String) {
    }

    override fun onProviderDisabled(provider: String) {
        //if Provider is not available
        showMessage("Unable to get location provider")
    }

    private fun showMessage(msg: String) {
        if (SharedPrefHelper().readBoolean(requireContext(), AppConstants.VOICE_KEY)) {
            tts!!.language = Locale.UK
            tts!!.speak(msg, TextToSpeech.QUEUE_ADD, null)
        } else
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    private fun hideLoadingView() {
        fragMapsBinding.tvTitle.visibility = View.GONE
        fragMapsBinding.pbLoading.visibility = View.GONE
    }

    private fun showLoadingView() {
        fragMapsBinding.tvTitle.visibility = View.VISIBLE
        fragMapsBinding.pbLoading.visibility = View.VISIBLE
    }

    private fun calculateDistance(
        sourceLat: Float,
        sourceLong: Float,
        currentLat: Float,
        currentLong: Float
    ): Boolean {
        var inRadius: Boolean
        val sourceLocation = Location("source")
        sourceLocation.latitude = sourceLat.toDouble()
        sourceLocation.longitude = sourceLong.toDouble()
        val currentLocation = Location("current")
        currentLocation.latitude = currentLat.toDouble()
        currentLocation.longitude = currentLong.toDouble()
        inRadius = currentLocation.distanceTo(sourceLocation) <= (SharedPrefHelper().readString(
            requireContext(),
            AppConstants.RADIUS_KEY
        )!!.toInt())
        return inRadius
    }

    override fun onDestroyView() {
        super.onDestroyView()
        locationManager!!.removeUpdates(this)
    }
}