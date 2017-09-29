package com.sumasoft.coffeeshopfinder.activities

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import com.google.android.gms.location.LocationListener
import android.os.Build
import android.support.v4.app.FragmentActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.RatingBar
import android.widget.TextView

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.sumasoft.coffeeshopfinder.R
import com.sumasoft.coffeeshopfinder.interfaces.RetrofitMaps
import com.sumasoft.coffeeshopfinder.model.CoffeeShopResponse
import com.sumasoft.coffeeshopfinder.model.Result
import com.sumasoft.coffeeshopfinder.utils.MapUtils
import com.google.android.gms.location.LocationServices

import retrofit.Callback
import retrofit.Response
import retrofit.Retrofit
import retrofit.GsonConverterFactory

class MapsActivity : FragmentActivity(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMarkerClickListener {

    private var mMap: GoogleMap? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLocationRequest: LocationRequest? = null
    private var mLastLocation: Location? = null
    private var mCurrLocationMarker: Marker? = null
    private val PROXIMITY_RADIUS = 10000
    internal var mResponse: Response<CoffeeShopResponse>? = null
    private var mCurrentbitmapDescriptor: BitmapDescriptor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        //show error dialog if Google Play Services not available
        if (!MapUtils.isGooglePlayServicesAvailable(this@MapsActivity, this@MapsActivity)) {
            Log.d("onCreate", "Google Play Services not available. Ending Test case.")
            finish()
        } else {
            Log.d("onCreate", "Google Play Services available. Continuing.")
        }

        //checked for gps is enabled or not
        MapUtils.isGpsEnabled(this@MapsActivity, this@MapsActivity)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap!!.isMyLocationEnabled = true
        try {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    buildGoogleApiClient()
                    mMap!!.isMyLocationEnabled = true
                }
            } else {
                buildGoogleApiClient()
                mMap!!.isMyLocationEnabled = true
            }
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

    @Synchronized protected fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
        mGoogleApiClient!!.connect()
    }

    private fun getCurrentLatLong(location: Location?): LatLng {
        //get current location marker
        return LatLng(location!!.latitude, location.longitude)
    }

    override fun onLocationChanged(location: Location) {
        Log.d("onLocationChanged", "entered")

        mLastLocation = location

        //current latitude and longitude
        val latLng = getCurrentLatLong(location)

        //get Bitmapdescriptor from vector resource
        mCurrentbitmapDescriptor = bitmapDescriptorFromVector(this@MapsActivity, R.drawable.ic_person_pin_circle_blue_24dp)
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker!!.remove()
        }
        //add current location marker
        mCurrLocationMarker = addMarker(latLng, mCurrentbitmapDescriptor, "Current Location")

        Log.d("onLocationChanged", String.format("latitude:%.3f longitude:%.3f", latLng.latitude, latLng.longitude))

        Log.d("onLocationChanged", "Exit")

        //find coffee shops of near by location
        getCoffeeShopsNearMe(latLng)

        mMap!!.setOnMarkerClickListener(this)
    }

    private fun addMarker(latLng: LatLng, bitmapDescriptor: BitmapDescriptor?, title: String?): Marker {

        val markerOptions = MarkerOptions()
        //position of marker on map
        markerOptions.position(latLng)
        //title to marker
        markerOptions.title(title)

        // Adding icon to the marker
        markerOptions.icon(bitmapDescriptor)

        // Adding Marker to the Map
        var marker = mMap!!.addMarker(markerOptions)

        //move map camera
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap!!.animateCamera(CameraUpdateFactory.zoomTo(11f))

        return marker
    }


    private fun getCoffeeShopsNearMe(latLng: LatLng) {
        val url = "https://maps.googleapis.com/maps/"

        val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val service = retrofit.create<RetrofitMaps>(RetrofitMaps::class.java!!)

        val call = service.getNearbyPlaces("cafe", latLng.latitude.toString() + "," + latLng.longitude, PROXIMITY_RADIUS)

        call.enqueue(object : Callback<CoffeeShopResponse> {
            override fun onResponse(response: Response<CoffeeShopResponse>, retrofit: Retrofit) {

                try {
                    mMap!!.clear()

                    if(mLastLocation != null) {
                        //current latitude and longitude
                        val mlatLng = getCurrentLatLong(mLastLocation)
                        //add current location marker
                        mCurrLocationMarker = addMarker(mlatLng, mCurrentbitmapDescriptor, "Current Location")
                    }

                    mResponse = response
                    // This loop will go through all the results and add marker on each location.
                    for (i in 0..response.body().results.size - 1) {
                        val lat = response.body().results.get(i).geometry!!.location!!.lat
                        val lng = response.body().results.get(i).geometry!!.location!!.lng
                        val latLng = LatLng(lat!!, lng!!)

                        val bitmapDescriptor = bitmapDescriptorFromVector(this@MapsActivity, R.drawable.ic_map_cafe_brown_24dp)
                        val marker = addMarker(latLng, bitmapDescriptor, response.body().results.get(i).name)
                        try {
                            if (response.body().results.get(i).id == null) {
                                marker.tag = i
                            } else {
                                marker.tag = response.body().results.get(i).id
                            }
                        }catch (e : Exception){
                            e.printStackTrace()
                        }
                    }

                } catch (e: Exception) {
                    Log.d("onResponse", "There is an error")
                    e.printStackTrace()
                }

            }

            override fun onFailure(t: Throwable) {
                Log.d("onFailure", t.toString())
            }
        })

    }

    //create bitmap descriptor from vector resource
    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    override fun onConnected(bundle: Bundle?) {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = 60000
        mLocationRequest!!.fastestInterval = 60000
        mLocationRequest!!.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this@MapsActivity)
        }
    }

    override fun onConnectionSuspended(i: Int) {

    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {

    }

    override fun onMarkerClick(marker: Marker): Boolean {
        var tag = ""
        if (marker.tag != null) {
            tag = marker.tag!!.toString()
        }
        if (mResponse != null) {
            for (i in 0..mResponse!!.body().results.size - 1) {
                if (mResponse!!.body().results.get(i).id.equals(tag) || mResponse!!.body().results.get(i).id!!.equals(i)) {
                    showInfoWindow(mResponse!!.body().results.get(i))
                    break
                }
            }
        }
        return true
    }

    private fun showInfoWindow(result: Result?) {
        if (result != null) {
            try {
                var isOpenNow = false
                val name = result!!.name
                val vivinity = result!!.vicinity
                if (result!!.openingHours != null) {
                    isOpenNow = result!!.openingHours!!.openNow!!
                }
                val rating = result!!.rating

                // Create custom dialog object
                val dialog = Dialog(this@MapsActivity)
                // Include dialog.xml file
                dialog.setContentView(R.layout.popup_map_info_window)

                dialog.setCancelable(true)
                // Set dialog title
                dialog.setTitle("Information")
                val txtName = dialog.findViewById<TextView>(R.id.txtName)
                val txtVicinity = dialog.findViewById<TextView>(R.id.txtVicinity)
                val txtIsOpen = dialog.findViewById<TextView>(R.id.txtIsOpen)
                val txtRating = dialog.findViewById<TextView>(R.id.txtRating)
                val ratingbar = dialog.findViewById<RatingBar>(R.id.rating)

                //set name of coffee shop
                if (name != null) {
                    txtName.text = name
                }
                //set address of coffee shop
                if (vivinity != null) {
                    txtVicinity.text = vivinity
                }
                //set open status of coffe shop
                if (isOpenNow) {
                    txtIsOpen.text = resources.getString(R.string.open_yes)
                } else {
                    txtIsOpen.text = resources.getString(R.string.open_no)
                }
                if (result!!.openingHours == null) {
                    txtIsOpen.visibility = View.GONE
                }
                if (rating != null) {
                    ratingbar.rating = rating.toFloat()
                }
                txtRating.text = rating.toString()
                dialog.show()
            }catch (e : Exception){
                e.printStackTrace()
            }
        }
    }
}

