package com.example.storiessocial.view.maps

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.storiessocial.R
import com.example.storiessocial.ViewModelFactory
import com.example.storiessocial.databinding.ActivityMapsBinding
import com.example.storiessocial.model.remote.response.ListStoryItem
import com.example.storiessocial.view.detail.StoryDetailActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var token: String
    private lateinit var mapsViewModel: MapsViewModel
    private val boundsBuilder = LatLngBounds.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setupViewModel()
    }

    private fun setupViewModel(){

        mapsViewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(application)
        )[MapsViewModel::class.java]


    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        getMyLocation()
        setMapStyle()
        // fetch token
        mapsViewModel.getToken().observe(this) {user ->
            if (user != null) {
                if(user.isLogin){
                    token = user.token
                    addManyMarker()
                }else{
                    token = ""
                }
            }
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private fun addManyMarker() {
        // ambil data dari view model
        mapsViewModel.allStories(token).observe(this){ result ->
            when(result){
                is com.example.storiessocial.model.Result.Loading -> {
                }
                is com.example.storiessocial.model.Result.Success -> {
                    val value = result.data.listStory
                    Log.e("loc view saja", value.toString())
                    setLocations(value)
                }
                is com.example.storiessocial.model.Result.Error -> {
                    val msg: String = getString(R.string.failMarker)
                    Toast.makeText(
                        this@MapsActivity,
                        msg,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setLocations(stories :List<ListStoryItem?>?) {
        Log.e("loc view saja-2", stories.toString())
        if(stories != null){
            stories.forEach{story ->
                Log.e("view story", story.toString())
                if (story != null) {
                    val item: MutableMap<String,String> = mutableMapOf()
                    item["photoUrl"] = story.photoUrl as String
                    item["createdAt"] = story.createdAt as String
                    item["name"] = story.name as String
                    item["description"] = story.description as String
                    item["id"] = story.id as String

                    if(story.lat!! < 90.0 && story.lat > -90.0){
                        val latLng = LatLng(story.lat, story.lon!!)
                        val addressName = getAddressName(story.lat, story.lon)

                        val marker = mMap.addMarker(
                            MarkerOptions()
                                .position(latLng)
                                .title(story.name)
                                .snippet(addressName))
                        marker?.tag = item
                        mMap.setOnInfoWindowClickListener {
                            val moveIntentUsers = Intent(this@MapsActivity, StoryDetailActivity::class.java)
                            val data = it.tag
                            if (data is MutableMap<*, *>) {
                                moveIntentUsers.putExtra(StoryDetailActivity.USERNAME, data["name"].toString())
                                moveIntentUsers.putExtra(StoryDetailActivity.PHOTO,  data["photoUrl"].toString())
                                moveIntentUsers.putExtra(StoryDetailActivity.DESC, data["description"].toString())
                                moveIntentUsers.putExtra(StoryDetailActivity.DATE, data["createdAt"].toString())
                                startActivity(moveIntentUsers)
                            }
                        }
                        boundsBuilder.include(latLng)
                    }

                }
            }
        }else{
            val msg: String = getString(R.string.nullResponse)
            Toast.makeText(
                this@MapsActivity,
                msg,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    @Suppress("DEPRECATION")
    private fun getAddressName(lat: Double, lon: Double): String? {
        var addressName: String? = null
        val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())
        try {
            val list = geocoder.getFromLocation(lat, lon, 1)
            if (list != null && list.size != 0) {
                addressName = list[0].getAddressLine(0)
                Log.d(ContentValues.TAG, "getAddressName: $addressName")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return addressName
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

}