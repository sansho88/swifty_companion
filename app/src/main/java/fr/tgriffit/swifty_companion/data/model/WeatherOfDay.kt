package fr.tgriffit.swifty_companion.data.model

data class WeatherOfDay(
    /*val latitude: Double,
    val longitude: Double,
    val generationtime_ms: Double,
    val utc_offset_seconds: Int,
    val timezone: String,
    val timezone_abbreviation: String,
    val elevation: Double,
    val hourly_units: HourlyUnits,
    val hourly: Hourly*/
    val longitude: Double,
    val generationtime_ms: Double,
    val utc_offset_seconds: Int,
    val timezone: String,
    val timezone_abbreviation: String,
    val elevation: Double
){
    override fun toString(): String {
        return "WeatherOfDay(elevation=$elevation, generationtime_ms=$generationtime_ms, longitude=$longitude, timezone='$timezone', timezone_abbreviation='$timezone_abbreviation', utc_offset_seconds=$utc_offset_seconds)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WeatherOfDay

        if (elevation != other.elevation) return false
        if (generationtime_ms != other.generationtime_ms) return false
        //if (latitude != other.latitude) return false
        if (longitude != other.longitude) return false
        if (timezone != other.timezone) return false
        if (timezone_abbreviation != other.timezone_abbreviation) return false
        return utc_offset_seconds == other.utc_offset_seconds
    }

    override fun hashCode(): Int {
        var result = elevation.hashCode()
        result = 31 * result + generationtime_ms.hashCode()
        //result = 31 * result + latitude.hashCode()
        result = 31 * result + longitude.hashCode()
        result = 31 * result + timezone.hashCode()
        result = 31 * result + timezone_abbreviation.hashCode()
        result = 31 * result + utc_offset_seconds
        return result
    }
}

data class Hourly(
    val time: List<String>,
    val temperature_2m: List<Double>
)

data class HourlyUnits(
    val time: String,
    val temperature_2m: String,
)


