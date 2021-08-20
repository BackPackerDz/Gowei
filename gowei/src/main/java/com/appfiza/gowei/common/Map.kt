package com.appfiza.gowei.common

import android.content.Context
import android.os.Parcelable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import kotlinx.android.parcel.Parcelize

/**
 * Created by Fayçal KADDOURI 🐈 on 3/7/21.
 */
interface Map : LifecycleObserver {

    @Parcelize
    data class Position(val lat: Double, val lng: Double) : Parcelable

    fun init()
    fun isMarkerAvailable(markerID: String): Boolean
    fun getTargetCameraPosition(): Position
    fun enableMarkerClick()
    fun disableMarkerClick()
    fun animateCameraZoomBy(zoomBy: Float, zoom: Int)
    fun animateCameraZoomIn(zoom: Int)
    fun isScrollGesturesEnabled(): Boolean
    fun setMarkerPositionListener(markerPositionListener: (Position) -> Unit)
    fun setOnCameraIdleListener(cameraIdleListener: (() -> Unit))
    fun setScrollGesturesEnabledDuringRotateOrZoom(enabled: Boolean)
    fun setRotateGesture(enabled: Boolean)
    fun setTiltGesturesEnabled(enabled: Boolean)
    fun setCompassEnabled(enabled: Boolean)
    fun setMyLocationButtonEnabled(enabled: Boolean)
    fun setScrollGesturesEnabled(enabled: Boolean)
    fun setZoomControlsEnabled(enabled: Boolean)
    fun setAllGesturesEnabled(enabled: Boolean)
    fun setMapType(mapType: Int)
    fun setMapStyle(context: Context, resId: Int): Boolean
    fun animateCamera(lat: Double, lng: Double, zoom: Float)
    fun animateCameraWithBounds(positions: List<Position>, zoom: Int)
    fun moveCamera(lat: Double, lng: Double, zoom: Float)
    fun addPolyline(polylineEncoded: String, width: Float? = null, color: Int? = null)
    fun setPadding(left: Int, top: Int, right: Int, bottom: Int)
    fun clear()

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
    fun onDestroy() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
    }
}