package com.appfiza.gowei

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.huawei.hms.maps.model.BitmapDescriptor
import com.huawei.hms.maps.model.BitmapDescriptorFactory

/**
 * Created by Fayçal KADDOURI 🐈 on 3/16/21.
 */

fun bitmapDescriptorFromVector(drawable: Drawable?): BitmapDescriptor? {
    drawable?.run {
        setBounds(0, 0, intrinsicWidth, intrinsicHeight)
        val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
        draw(Canvas(bitmap))
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
    return null
}

fun resizeMarkerIcon(context: Context, w: Int, h: Int, @DrawableRes id: Int): Bitmap {
    val bitmapDraw: BitmapDrawable = ContextCompat.getDrawable(
        context, id
    ) as BitmapDrawable
    val b: Bitmap = bitmapDraw.bitmap
    return Bitmap.createScaledBitmap(b, w, h, false)
}