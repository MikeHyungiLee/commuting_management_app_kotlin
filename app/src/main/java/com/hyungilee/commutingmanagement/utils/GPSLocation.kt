package com.hyungilee.commutingmanagement.utils

import android.location.Location

class GPSLocation {

    companion object{
        // 距離計算メソッド
        fun calDistance(currentLocationLat: Double,
                        currentLocationLon: Double,
                        workLocationLat: Double,
                        workLocationLon: Double): Float{
            //現在の位置情報
            val currentLoc = Location("Current_Location")
            currentLoc.latitude = currentLocationLat
            currentLoc.longitude = currentLocationLon

            //勤務地の位置情報
            val workLoc = Location("Work_Location")
            workLoc.latitude = workLocationLat
            workLoc.longitude = workLocationLon

            //現在位置から勤務地までの距離を返却
            return currentLoc.distanceTo(workLoc)
        }
    }
}