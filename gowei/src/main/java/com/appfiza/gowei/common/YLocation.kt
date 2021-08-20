package com.appfiza.gowei.common

import android.content.Context
import android.content.Intent
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

/**
 * Created by Fayçal KADDOURI 🐈 on 3/17/21.
 */
abstract class YLocation : LifecycleObserver {

    private lateinit var lifecycle: Lifecycle
    protected lateinit var context: Context
    protected var activity: AppCompatActivity? = null
    protected var fragment: Fragment? = null
    protected var locationListener: LocationListener? = null
    protected var interval: Long = 0
    protected var fastestInterval: Long = 0
    protected var smallestDisplacement: Float = 0F
    protected var priority: Int = 0

    companion object {
        const val PRIORITY_HIGH_ACCURACY = 100
        const val PRIORITY_BALANCED_POWER_ACCURACY = 102
        const val PRIORITY_LOW_POWER = 104
        const val PRIORITY_NO_POWER = 105
    }

    protected lateinit var rationalMessage: String
    var keepTracking = true

    interface LocationListener {
        fun onLocationChanged(location: Location) {}
        fun onLocationFailed()
    }

    open fun init(
        activity: AppCompatActivity? = null,
        fragment: Fragment? = null,
        rationalMessage: String,
        keepTracking: Boolean = true,
        locationListener: LocationListener?,
        interval: Long = 100,
        fastestInterval: Long = 100,
        smallestDisplacement: Float = 0F,
        priority: Int = PRIORITY_HIGH_ACCURACY
    ) {
        this.keepTracking = keepTracking
        this.rationalMessage = rationalMessage
        this.locationListener = locationListener
        this.interval = interval
        this.fastestInterval = fastestInterval
        this.smallestDisplacement = smallestDisplacement
        this.priority = priority

        if (fragment != null) {
            this.fragment = fragment
            lifecycle = fragment.lifecycle
            context = fragment.requireContext()
        } else if (activity != null) {
            this.activity = activity
            lifecycle = activity.lifecycle
            context = activity
        }

        lifecycle.addObserver(this)

    }


    abstract fun requestLocation()

    abstract fun stopRequestingLocation()

    abstract fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)

    abstract fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    )

    /**
     *  Maps life cycle
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    open fun onDestroy() {
        lifecycle.removeObserver(this)
        activity = null
        fragment = null
        locationListener = null
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    open fun onPause() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    open fun onResume() {
    }
}