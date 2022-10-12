package com.mtis.gowith.widget.utils

import com.google.android.gms.maps.model.LatLng

object LocationUtils {
    private val EARTH_RADIUS = 6371.0

    /**
     * mady by hanjw
     * 정확한 두 점 사이의 거리를 확인하기 위해 만들어진 거리 구하는 공식
     * 직선 좌표로 거리를 구하게 되면, 지구 곡률이 반영되지 않기 때문에,
     * 구면 좌표계를 적용하여 거리를 구한다.
     * @param start : 시작 좌표
     * @param end : 도착 좌표
     * @return 미터 단위로 반환한다.
     */
    fun calculateDistance(start: LatLng, end: LatLng): Double {
        var dLat: Double = Math.toRadians(end.latitude - start.latitude)
        var dLng: Double = Math.toRadians(end.longitude - start.longitude)

        var a: Double =
            Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(start.latitude)) * Math.cos(
                Math.toRadians(end.latitude)
            ) * Math.sin(dLng / 2) * Math.sin(dLng / 2)
        var c: Double = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        return (EARTH_RADIUS * 1000) * c
    }
}