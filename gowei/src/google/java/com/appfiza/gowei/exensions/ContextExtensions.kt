package com.appfiza.gowei.exensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import java.lang.IllegalArgumentException

/**
 * Created by Zinedine on 25/8/2021.
 * Yassir Inc
 */

@Throws(IllegalArgumentException::class)
fun Context.resizeMarkerIcon(w: Int, h: Int, @DrawableRes id: Int): Bitmap {
    val bitmapDraw: BitmapDrawable = ContextCompat.getDrawable(
        this, id
    ) as BitmapDrawable
    val b: Bitmap = bitmapDraw.bitmap
    return Bitmap.createScaledBitmap(b, w, h, false)
}