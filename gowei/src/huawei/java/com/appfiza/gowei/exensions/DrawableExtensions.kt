package com.appfiza.gowei.exensions

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.RuntimeRemoteException

/**
 * Created by Zinedine on 25/8/2021.
 * Yassir Inc
 */

@Throws(RuntimeRemoteException::class)
fun Drawable.bitmapDescriptorFromVector(): BitmapDescriptor?{
    setBounds(0, 0, intrinsicWidth, intrinsicHeight)
    val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
    draw(Canvas(bitmap))
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}