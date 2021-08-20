package com.appfiza.gowei

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import com.huawei.hms.maps.CameraUpdateFactory
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.SupportMapFragment
import com.huawei.hms.maps.model.*
import com.appfiza.gowei.common.Map
import com.appfiza.gowei.common.decodePolyline

/**
 * Created by Fayçal KADDOURI 🐈 on 3/7/21.
 */
class GoweiMap(
    activity: AppCompatActivity? = null,
    fragment: Fragment? = null,
    private var fragmentManager: FragmentManager? = null
) : Map {

    private var lifecycle: Lifecycle? = null
    private var context: Context? = null

    init {
        if (fragment != null) {
            lifecycle = fragment.lifecycle
            context = fragment.requireContext()
        } else if (activity != null) {
            lifecycle = activity.lifecycle
            context = activity
        }
    }

    lateinit var mMap: HuaweiMap
    lateinit var mapFragment: SupportMapFragment

    private var cameraIdleListener: (() -> Unit)? = null

    private var cameraMoveStartedListener: ((Int) -> Unit)? = null

    private var markerPositionListener: ((Map.Position) -> Unit)? = null
    private var mapReadyListener: ((Unit) -> Unit)? = null

    private val markers: HashMap<String, Marker> = HashMap()
    private var currentMarkerID: String? = null


    fun addMarker(markerID: String, lat: Double, lng: Double): GoweiMap {
        markers[markerID] = mMap.addMarker(
            MarkerOptions()
                .position(LatLng(lat, lng))
        )
        currentMarkerID = markerID
        return this
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle?.removeObserver(this)
        markers.clear()
        markerPositionListener = null
        mapReadyListener = null
        lifecycle = null
        context = null
    }

    fun anchor(x: Float, y: Float): GoweiMap {
        markers[currentMarkerID]?.setAnchor(x, y)
        return this
    }

    fun icon(drawable: Drawable?): GoweiMap {
        val icon = bitmapDescriptorFromVector(drawable)
        markers[currentMarkerID]?.setIcon(icon)
        return this
    }

    fun getMarkerPosition(): Map.Position? {
        val marker = markers[currentMarkerID]
        marker?.let {
            return Map.Position(marker.position.latitude, marker.position.longitude)
        }
        return null
    }

    fun icon(bitmap: Bitmap): GoweiMap {
        val icon = BitmapDescriptorFactory.fromBitmap(bitmap)
        markers[currentMarkerID]?.setIcon(icon)
        return this
    }

    fun withMarker(markerID: String): GoweiMap {
        currentMarkerID = markerID
        return this
    }

    fun remove(): GoweiMap {
        markers[currentMarkerID]?.remove()
        markers.remove(currentMarkerID)
        return this
    }

    fun removeAllMarkers() {
        markers.forEach { it.value.remove() }
        markers.clear()
    }

    fun rotation(bearing: Float): GoweiMap {
        markers[currentMarkerID]?.rotation = bearing
        return this
    }

    fun flat(isFlat: Boolean): GoweiMap {
        markers[currentMarkerID]?.isFlat = isFlat
        return this
    }

    override fun isMarkerAvailable(markerID: String): Boolean {
        return markers.containsKey(markerID)
    }

    fun position(lat: Double, lng: Double): GoweiMap {
        markers[currentMarkerID]?.position = LatLng(lat, lng)
        return this
    }

    override fun init() {
        mapFragment = fragmentManager?.findFragmentById(R.id.mMap) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            this.mMap = googleMap
            mMap.setOnMarkerClickListener { marker ->
                broadcastMarkerPosition(marker)
                true
            }
            broadcastMapReady()
        }
        lifecycle?.addObserver(this)
    }

    override fun animateCameraZoomBy(zoomBy: Float, zoom: Int) {
        mMap.animateCamera(CameraUpdateFactory.zoomBy(zoomBy), zoom, null);
    }

    override fun isScrollGesturesEnabled(): Boolean = mMap.uiSettings.isScrollGesturesEnabled


    override fun animateCameraZoomIn(zoom: Int) {
        mMap.animateCamera(CameraUpdateFactory.zoomIn(), zoom, null)
    }

    fun setMapReadyListener(mapReadyListener: ((Unit) -> Unit)) {
        this.mapReadyListener = mapReadyListener
    }

    override fun setMarkerPositionListener(markerPositionListener: (Map.Position) -> Unit) {
        this.markerPositionListener = markerPositionListener
    }

    override fun setOnCameraIdleListener(cameraIdleListener: (() -> Unit)) {
        this.cameraIdleListener = cameraIdleListener
        mMap.setOnCameraIdleListener { broadcastCameraIdle() }
    }

    fun setOnCameraMoveStartedListener(cameraMoveStartedListener: ((Int) -> Unit)) {
        this.cameraMoveStartedListener = cameraMoveStartedListener
        mMap.setOnCameraMoveStartedListener { broadcastCameraMoveStarted(it) }
    }

    private fun broadcastCameraMoveStarted(value: Int) {
        this.cameraMoveStartedListener?.let { function ->
            function(value)
        }
    }

    private fun broadcastCameraIdle() {
        this.cameraIdleListener?.let { function ->
            function()
        }
    }

    private fun broadcastMapReady() {
        this.mapReadyListener?.let { function ->
            function(Unit)
        }
    }

    private fun broadcastMarkerPosition(marker: Marker) {
        this.markerPositionListener?.let { function ->
            function(Map.Position(lat = marker.position.latitude, lng = marker.position.longitude))
        }
    }

    override fun addPolyline(polylineEncoded: String, width: Float?, color: Int?) {
        val polylineDecoded = decodePolyline(polylineEncoded)
        val defaultWidth = width ?: 3F

        val latLntPoints = ArrayList<LatLng>()
        polylineDecoded.forEach { latLntPoints.add(LatLng(it.lat, it.lng)) }

        val polylineOptions = PolylineOptions()
            .width(defaultWidth)
            .addAll(latLntPoints)

        color?.let { polylineOptions.color(it) }

        mMap.addPolyline(polylineOptions)
    }

    override fun setScrollGesturesEnabled(enabled: Boolean) {
        mMap.uiSettings.isScrollGesturesEnabled = enabled
    }

    override fun setAllGesturesEnabled(enabled: Boolean) {
        mMap.uiSettings.setAllGesturesEnabled(enabled)
    }

    override fun getTargetCameraPosition(): Map.Position {
        return Map.Position(
            mMap.cameraPosition.target.latitude,
            mMap.cameraPosition.target.longitude
        )
    }

    override fun setMapStyle(context: Context, resId: Int): Boolean {
        return mMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                context, resId
            )
        )
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        mMap.setPadding(left, top, right, bottom)
    }

    override fun setMapType(mapType: Int) {
        mMap.mapType = mapType
    }

    override fun clear() {
        mMap.clear()
    }

    override fun setScrollGesturesEnabledDuringRotateOrZoom(enabled: Boolean) {
        mMap.uiSettings.isScrollGesturesEnabledDuringRotateOrZoom = enabled
    }

    override fun setRotateGesture(enabled: Boolean) {
        mMap.uiSettings.isRotateGesturesEnabled = enabled
    }

    override fun setTiltGesturesEnabled(enabled: Boolean) {
        mMap.uiSettings.isTiltGesturesEnabled = enabled
    }

    override fun disableMarkerClick() {
        mMap.setOnMarkerClickListener { true }
    }

    override fun enableMarkerClick() {
        mMap.setOnMarkerClickListener(null)
    }

    override fun setCompassEnabled(enabled: Boolean) {
        mMap.uiSettings.isCompassEnabled = enabled
    }

    override fun setZoomControlsEnabled(enabled: Boolean) {
        mMap.uiSettings.isZoomControlsEnabled = enabled
    }

    override fun setMyLocationButtonEnabled(enable: Boolean) {
        mMap.uiSettings.isMyLocationButtonEnabled = enable
    }

    override fun animateCamera(lat: Double, lng: Double, zoom: Float) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), zoom))
    }

    override fun animateCameraWithBounds(positions: List<Map.Position>, zoom: Int) {
        val builder = LatLngBounds.Builder()
        positions.forEach {
            builder.include(LatLng(it.lat, it.lng))
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), zoom))
    }

    override fun moveCamera(lat: Double, lng: Double, zoom: Float) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), zoom))
    }


}
