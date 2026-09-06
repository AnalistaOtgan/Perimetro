package com.example.location

/**
 * Representação imutável do estado de geolocalização física do usuário.
 */
data class UserLocation(
    val latitude: Double,
    val longitude: Double,
    val accuracyMeters: Float? = null,
    val bearing: Float? = null,
    val speedMps: Float? = null,
    val altitudeMeters: Double? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val provider: String = "gps",
    val isMock: Boolean = false
) {
    val speedKmH: Float?
        get() = speedMps?.let { it * 3.6f }

    val formattedCoordinates: String
        get() = "%.4f, %.4f".format(latitude, longitude)

    val formattedAccuracy: String
        get() = accuracyMeters?.let { "±%.0fm".format(it) } ?: "GPS Ativo"

    companion object {
        const val DEFAULT_SP_LAT = -23.5615
        const val DEFAULT_SP_LON = -46.6560

        val DefaultSaoPaulo = UserLocation(
            latitude = DEFAULT_SP_LAT,
            longitude = DEFAULT_SP_LON,
            accuracyMeters = 8f,
            bearing = 45f,
            speedMps = 0f,
            provider = "demo_sp",
            isMock = true
        )
    }
}
