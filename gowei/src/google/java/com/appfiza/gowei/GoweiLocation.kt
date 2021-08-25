package com.appfiza.gowei

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.appfiza.gowei.common.YLocation
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions


/**
 * Created by Fayçal KADDOURI 🐈 on 3/17/21.
 */

private const val RC_LOCATION = 9999
private const val REQUEST_CODE_LOCATION_ACTIVATION = 1111

class GoweiLocation : YLocation() {

    private val TAG = "GoweiLocation"

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var settingsClient: SettingsClient
    private lateinit var locationCallback: LocationCallback

    override fun init(
        activity: AppCompatActivity?,
        fragment: Fragment?,
        rationalMessage: String,
        keepTracking: Boolean,
        locationListener: LocationListener?,
        interval: Long,
        fastestInterval: Long,
        smallestDisplacement: Float,
        priority: Int
    ) {
        super.init(
            activity,
            fragment,
            rationalMessage,
            keepTracking,
            locationListener,
            interval,
            fastestInterval,
            smallestDisplacement,
            priority
        )
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        settingsClient = LocationServices.getSettingsClient(context)
        buildLocationRequest()
        buildLocationCallBack()
    }

    private fun buildLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            priority
            interval
            fastestInterval
            smallestDisplacement
        }

    }

    //Build the location callback object and obtain the location results //as demonstrated below:
    private fun buildLocationCallBack() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult.locations.isNotEmpty()) {
                    locationListener?.onLocationChanged(locationResult.lastLocation)
                    /**
                     * To avoid having new locations
                     */
                    if (!keepTracking) {
                        stopRequestingLocation()
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_LOCATION_ACTIVATION && resultCode == Activity.RESULT_OK) {
            requestLocationUpdatesWithCallback()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdatesWithCallback() {
        try {
            val builder = LocationSettingsRequest.Builder()
            builder.addLocationRequest(locationRequest)
            val locationSettingsRequest = builder.build()
            // check devices settings before request location updates.
            settingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener {
                    Log.i(TAG, "check location settings success")
                    //request location updates
                    fusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.getMainLooper()
                    ).addOnSuccessListener {
                        Log.i(
                            TAG,
                            "requestLocationUpdatesWithCallback onSuccess"
                        )
                    }.addOnFailureListener { e ->
                        locationListener?.onLocationFailed()
                        Log.e(
                            TAG,
                            "requestLocationUpdatesWithCallback onFailure:" + e.message
                        )
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "checkLocationSetting onFailure:" + e.message)
                    when ((e as ApiException).statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                            val rae = e as ResolvableApiException
                            if (activity != null) {
                                rae.startResolutionForResult(
                                    activity,
                                    REQUEST_CODE_LOCATION_ACTIVATION
                                )
                            } else {
                                fragment?.startIntentSenderForResult(
                                    rae.resolution.intentSender,
                                    REQUEST_CODE_LOCATION_ACTIVATION,
                                    null,
                                    0,
                                    0,
                                    0,
                                    null
                                )
                            }
                        } catch (sie: IntentSender.SendIntentException) {
                            Log.e(TAG, "PendingIntent unable to execute request.")
                        }
                    }
                }
        } catch (e: Exception) {

        }
    }


    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(RC_LOCATION)
    override fun requestLocation() {
        val perms = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (EasyPermissions.hasPermissions(context, *perms)) {
            requestLocationUpdatesWithCallback()
        } else {
            // Do not have permissions, request them now
            if (activity != null) {
                EasyPermissions.requestPermissions(
                    activity as Activity, rationalMessage,
                    RC_LOCATION, *perms
                )
            } else {
                EasyPermissions.requestPermissions(
                    fragment as Fragment, rationalMessage,
                    RC_LOCATION, *perms
                )
            }
        }
    }

    override fun stopRequestingLocation() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }


}