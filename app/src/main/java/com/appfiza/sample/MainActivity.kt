package com.appfiza.sample

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appfiza.gowei.GoweiLocation
import com.appfiza.gowei.GoweiMap
import com.appfiza.gowei.common.Map
import com.appfiza.gowei.common.YLocation
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var goweiMap: GoweiMap
    private lateinit var goweiLocation: GoweiLocation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        goweiLocation = GoweiLocation()
        goweiLocation.init(
            activity = this,
            rationalMessage = "Rational message",
            keepTracking = true,
            locationListener = object : YLocation.LocationListener {
                override fun onLocationFailed() {}

                override fun onLocationChanged(location: Location) {
                    super.onLocationChanged(location)
                    txtLocation.text = "lat : ${location.latitude}, lng : ${location.longitude}"
                }
            })


        btnLocation.setOnClickListener {
            Toast.makeText(this, "Request location", Toast.LENGTH_SHORT).show()
            goweiLocation.requestLocation()
        }

        goweiMap = GoweiMap(activity = this, fragmentManager = supportFragmentManager)
        goweiMap.init()

        goweiMap.setMapReadyListener {
            onMapReady()
        }

        goweiMap.setMarkerPositionListener {
            println("$it")
        }

        btnAnimateCamera.setOnClickListener {
            goweiMap.animateCamera(36.7731144, 3.0594618, 18F)
        }

        btnAddmarker.setOnClickListener {
            goweiMap.addMarker("markerId_1", 36.763825, 3.048627)
            goweiMap.animateCamera(36.763825, 3.048627, 18F)
        }

        btnClear.setOnClickListener {
            goweiMap.clear()
        }

        btnPolyline.setOnClickListener {
            val bounds: List<Map.Position> = arrayListOf(
                Map.Position(36.763825, 3.048627),
                Map.Position(36.7731144, 3.0594618)
            )
            val polylineEncoded = "{lk_F}lrQay@ubA"
            goweiMap.animateCameraWithBounds(bounds, 48)
            goweiMap.addPolyline(polylineEncoded, width = 5F)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        goweiLocation.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        goweiLocation.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun onMapReady() {
        goweiMap.setPadding(0, 0, 0, 400)
    }


}