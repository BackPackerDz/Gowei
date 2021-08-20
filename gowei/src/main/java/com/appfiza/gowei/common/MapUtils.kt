package com.appfiza.gowei.common

import java.util.*

/**
 * Created by Fayçal KADDOURI 🐈 on 3/16/21.
 */
/**
 * Decodes an encoded path string into a sequence of LatLngs.
 */
fun decodePolyline(encodedPath: String): List<Map.Position> {
    val len = encodedPath.length

    // For speed we preallocate to an upper bound on the final length, then
    // truncate the array before returning.
    val path: MutableList<Map.Position> = ArrayList<Map.Position>()
    var index = 0
    var lat = 0
    var lng = 0
    while (index < len) {
        var result = 1
        var shift = 0
        var b: Int
        do {
            b = encodedPath[index++].toInt() - 63 - 1
            result += b shl shift
            shift += 5
        } while (b >= 0x1f)
        lat += if (result and 1 != 0) (result shr 1).inv() else result shr 1
        result = 1
        shift = 0
        do {
            b = encodedPath[index++].toInt() - 63 - 1
            result += b shl shift
            shift += 5
        } while (b >= 0x1f)
        lng += if (result and 1 != 0) (result shr 1).inv() else result shr 1
        path.add(Map.Position(lat * 1e-5, lng * 1e-5))
    }
    return path
}