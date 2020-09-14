package com.senjapagi.sawitjaya.activity

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.*
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.senjapagi.sawitjaya.BaseActivity
import com.senjapagi.sawitjaya.R
import com.senjapagi.sawitjaya.util.Permissions
import kotlinx.android.synthetic.main.activity_user_sell_tbs.*
import kotlinx.android.synthetic.main.fragment_user_home.*
import kotlinx.android.synthetic.main.xd_gps_error.*
import kotlinx.android.synthetic.main.xd_internet_error.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks
import java.text.SimpleDateFormat
import java.util.*


class UserSellTBS : BaseActivity(), OnMapReadyCallback, PermissionCallbacks {

    private var mMap: GoogleMap? = null
    private lateinit var map: GoogleMap

    var lat: Double = 0.9434
    var long: Double = 116.9852
    var myMapPosition = LatLng(lat, long)
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    var gpsPermissionStat = false


    private var googleApiClient: GoogleApiClient? = null
    private val REQUESTLOCATION = 199
    lateinit var mapFragment: SupportMapFragment

    private var isInternetAvailable = true

    companion object {
        private const val PERMISSION_REQUEST = 10
    }

    lateinit var perms1: String
    lateinit var perms2: String
    var checkVal1: Int = 0
    var checkVal2: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_sell_tbs)
        super.adjustFontScale(resources.configuration)
        //Hide On Screen Keyboard
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        lyt_internet_error?.visibility = View.GONE
        lyt_gps_error.visibility = View.GONE
        checkPermission()

        //Check if network is available
        var networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this, androidx.lifecycle.Observer { isConnected ->
            isInternetAvailable = isConnected
            initiateMap(isInternetAvailable)
        })
        //Hide GPS Error Layout when GPS is Available
        if (checkVal1 == PackageManager.PERMISSION_GRANTED && checkVal2 == PackageManager.PERMISSION_GRANTED) {
            gpsPermissionStat = true
            lyt_gps_error.visibility = View.GONE
            try{
                Handler().postDelayed({
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { location : Location? ->
                            lat=location?.latitude ?: 0.9434
                            long = location?.longitude ?:116.9852
                            myMapPosition = LatLng(lat, long)
                            // Got last known location. In some rare situations this can be null.
                        }
                },2000)
            }catch(e:Exception){
                Log.e("Fused Location",e.toString())
            }

        }
        //Check Loop until GPS Permission Granted
        val handlerA = Handler()
        handlerA.postDelayed(object : Runnable {
            var gpsStatPermission = false
            override fun run() {
                checkPermission()
                if (checkVal1 == PackageManager.PERMISSION_GRANTED && checkVal2 == PackageManager.PERMISSION_GRANTED) {
                    gpsPermissionStat = true
                    lyt_gps_error.visibility = View.GONE
                    handlerA.removeCallbacksAndMessages(null)
                    gpsStatPermission = true
                } else {
                    lyt_gps_error.visibility = View.VISIBLE
                    gpsPermissionStat = false
                }
                handlerA.postDelayed(this, 500)
                if (gpsStatPermission) {
                    handlerA.removeCallbacksAndMessages(null)
                    initiateMap(isInternetAvailable)
                }
            }
        }, 0)

        btnRefreshInternet?.setOnClickListener {
            networkConnection = NetworkConnection(applicationContext)
            networkConnection.observe(this, androidx.lifecycle.Observer { isConnected ->
                isInternetAvailable = isConnected
                initiateMap(isInternetAvailable)
            })
        }

        btnNextDetailTBS.setOnClickListener {

                val intent = Intent(this,DetailSellTBS::class.java)
                intent.putExtra("address",mapsAddress.text.toString())
                intent.putExtra("lat",etLat.text.toString())
                intent.putExtra("long",etLong.text.toString())
                startActivity(intent)
        }
    }

    private fun handlerChecker() {

    }

    private fun checkPermission() {
        perms1 = Manifest.permission.ACCESS_COARSE_LOCATION
        perms2 = Manifest.permission.ACCESS_FINE_LOCATION
        checkVal1 = this.checkCallingOrSelfPermission(perms1)
        checkVal2 = this.checkCallingOrSelfPermission(perms2)
    }


    private fun makeToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun initiateMap(param: Boolean) {
        if (param) {
            lyt_internet_error?.visibility = View.GONE
            mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)
        } else {
            lyt_internet_error?.visibility = View.VISIBLE
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        if (isInternetAvailable) {
            mMap = googleMap
            mMap!!.clear()
            val myPlace = LatLng(lat, long)
            mMap!!.uiSettings.isZoomControlsEnabled = true

            mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(myPlace, 7.0f))

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                makeToast("Mohon Berikan Izin Akses Lokasi ke Aplikasi")
            }

            val perms1 = Manifest.permission.ACCESS_COARSE_LOCATION
            val perms2 = Manifest.permission.ACCESS_FINE_LOCATION
            val checkVal1: Int = this.checkCallingOrSelfPermission(perms1)
            val checkVal2: Int = this.checkCallingOrSelfPermission(perms2)

            if (checkVal1 == PackageManager.PERMISSION_GRANTED && checkVal2 == PackageManager.PERMISSION_GRANTED) {
                mMap!!.isMyLocationEnabled = true
                mMap!!.uiSettings.isMyLocationButtonEnabled = true
            } else {
                lyt_gps_error.visibility = View.VISIBLE
                btnGivePermission.setOnClickListener {
                    if (gpsPermissionStat) {
                        lyt_gps_error.visibility = View.GONE
                    }
                    grantAccess()
                    enableLoc()
                }
                btnDenyPermission.setOnClickListener {
                    finishAffinity() //Close App if the permission is denied
                }
            }
            myMapPosition = LatLng(lat, long)
            val marker = mMap!!.addMarker(
                MarkerOptions()
                    .position(myMapPosition)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    .title("$myMapPosition")
            )

            var geocoder = Geocoder(applicationContext, Locale.getDefault())
            var addresses = geocoder.getFromLocation(
                marker.position.latitude,
                marker.position.longitude,
                1
            ) //1 num of possible location returned

            var address: String? = addresses[0].getAddressLine(0)
            //0 to obtain first possible address
            var city: String? = addresses[0].locality ?: ""
            var state: String? = addresses[0].adminArea ?: ""
            var country: String? = addresses[0].countryName ?: ""
            var postalCode: String? = addresses[0].postalCode ?: ""

            val title = "$address-$city-$state"
            mapsAddress.setText(title)
            marker.title = title
            etLat.text = lat.toString()
            etLong.text = long.toString()

            mMap!!.setOnCameraMoveListener {
                val midLatLng = mMap!!.cameraPosition.target
                if (marker != null) {
                    marker.position = midLatLng
                    myMapPosition = midLatLng
                    //updating current latitude and longitude
                    lat = marker.position.latitude
                    long = marker.position.longitude
                    geocoder = Geocoder(applicationContext, Locale.getDefault())
                    addresses = geocoder.getFromLocation(
                        marker.position.latitude,
                        marker.position.longitude,
                        1
                    ) //1 num of possible location returned

                    mapsAddress.setText("Memuat Alamat Anda")
                    etLat.text = lat.toString()
                    etLong.text = long.toString()
                    Handler().postDelayed({

                        //0 to obtain first possible address
                        city = addresses[0].locality ?: ""
                        state = addresses[0].adminArea ?: ""
                        country = addresses[0].countryName ?: ""
                        postalCode = addresses[0].postalCode ?: ""
                        val titleMarker = "$address-$city-$state"
                        address = addresses[0].getAddressLine(0) ?: ""
                        mapsAddress.setText(titleMarker)
                        marker.title = titleMarker
                        etLat.text = lat.toString()
                        etLong.text = long.toString()
                    }, 2500)


                } else
                    makeToast("Marker is Null")
            }
            mMap!!.setOnMarkerClickListener { markerRed -> //create your custom title
                val titleMarker = "$address-$city-$state"
                mapsAddress.setText(titleMarker)
                markerRed.title = titleMarker
                etLat.text = lat.toString()
                etLong.text = long.toString()
//                    marker.showInfoWindow()
                true
            }

            mMap!!.setOnMyLocationButtonClickListener {
                //TODO: Any custom actions
                val titleMarker = "$address-$city-$state"
                mapsAddress.setText(titleMarker)
                marker.title = titleMarker
                etLat.text = lat.toString()
                etLong.text = long.toString()
                false
            }
        } else {
            lyt_internet_error.visibility = View.VISIBLE
        }

    }

    @AfterPermissionGranted(123)
    fun grantAccess() {
        val perms = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (EasyPermissions.hasPermissions(this, *perms)) {
            lyt_gps_error.visibility = View.GONE
        } else {
            EasyPermissions.requestPermissions(
                this,
                "Kami memerlukan izin untuk mengetahui lokasi anda",
                123,
                *perms
            )
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUESTLOCATION -> when (resultCode) {
                Activity.RESULT_OK -> {
                    Log.d("abc", "OK")
                }
                Activity.RESULT_CANCELED -> {
                    Log.d("abc", "CANCEL")
                }
            }
        }
    }

    private fun enableLoc() {
        googleApiClient = GoogleApiClient.Builder(this)
            .addApi(LocationServices.API)
            .addConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
                override fun onConnected(bundle: Bundle?) {}
                override fun onConnectionSuspended(i: Int) {
                    googleApiClient?.connect()
                }
            })
            .addOnConnectionFailedListener {
            }.build()
        googleApiClient?.connect()
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 30 * 1000.toLong()
        locationRequest.fastestInterval = 5 * 1000.toLong()
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val result: PendingResult<LocationSettingsResult> =
            LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
        result.setResultCallback { result ->
            val status: Status = result.status
            when (status.statusCode) {
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                    status.startResolutionForResult(
                        this@UserSellTBS,
                        REQUESTLOCATION
                    )
                } catch (e: IntentSender.SendIntentException) {
                }
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            lyt_gps_error.visibility = View.VISIBLE
            makeToast("permissioon denied")
            btnGivePermission.setOnClickListener {
                grantAccess()
                if (gpsPermissionStat) {
                    lyt_gps_error.visibility = View.GONE
                }
            }
            btnDenyPermission.setOnClickListener {
                finishAffinity() //Close App if the permission is denied
            }
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        lyt_gps_error.visibility = View.GONE
        makeToast("Terima Kasih")
    }

}

class NetworkConnection(private val context: Context) : LiveData<Boolean>() {
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private var connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override fun onActive() {
        super.onActive()
        updateConnection()
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                connectivityManager.registerDefaultNetworkCallback(connectivityManagerCallback())
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                lollipopNetworkRequest()
            }
            else -> {
                context.registerReceiver(
                    networkReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
                )
            }
        }
    }

    override fun onInactive() {
        super.onInactive()
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                connectivityManager.unregisterNetworkCallback(connectivityManagerCallback())
            } else {
                context.unregisterReceiver(networkReceiver)
            }
        } catch (ky: Exception) {

        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun lollipopNetworkRequest() {
        val requestBuilder = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
        connectivityManager.registerNetworkCallback(
            requestBuilder.build(), connectivityManagerCallback()
        )
    }

    private fun connectivityManagerCallback(): ConnectivityManager.NetworkCallback {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onLost(network: Network) {
                    super.onLost(network)
                    postValue(false)
                }
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    postValue(true)
                }
            }
            return networkCallback
        } else {
            throw IllegalAccessError("Error")
        }
    }

    private val networkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateConnection()
        }
    }

    private fun updateConnection() {
        val activityNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        postValue(activityNetwork?.isConnected == true)
    }
}


