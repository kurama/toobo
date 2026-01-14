package com.example.toobo2.domain.model

data class Weather(
    val location: Location,
    val current: CurrentWeatherData,
    val hourly: List<HourlyWeatherData>,
    val daily: List<DailyWeatherData>,
    val lastUpdated: Long = System.currentTimeMillis()
)

data class CurrentWeatherData(
    val temperature: Double,
    val apparentTemperature: Double,
    val humidity: Int,
    val weatherCode: Int,
    val weatherDescription: String,
    val weatherIcon: WeatherIcon,
    val windSpeed: Double,
    val windDirection: Int,
    val precipitation: Double,
    val pressureMsl: Double,
    val uvIndex: Double,
    val isDay: Boolean,
    val dailyMaxTemp: Double,
    val dailyMinTemp: Double
)

data class HourlyWeatherData(
    val time: String,
    val temperature: Double,
    val apparentTemperature: Double,
    val humidity: Int,
    val precipitation: Double,
    val weatherCode: Int,
    val weatherIcon: WeatherIcon,
    val windSpeed: Double,
    val isDay: Boolean
)

data class DailyWeatherData(
    val date: String,
    val dayName: String,
    val weatherCode: Int,
    val weatherIcon: WeatherIcon,
    val weatherDescription: String,
    val maxTemperature: Double,
    val minTemperature: Double,
    val sunrise: String,
    val sunset: String,
    val uvIndexMax: Double,
    val precipitationSum: Double,
    val precipitationProbability: Int?,
    val windSpeedMax: Double
)

enum class WeatherIcon {
    CLEAR_DAY,
    CLEAR_NIGHT,
    PARTLY_CLOUDY_DAY,
    PARTLY_CLOUDY_NIGHT,
    CLOUDY,
    FOG,
    DRIZZLE,
    RAIN,
    SNOW,
    THUNDERSTORM,
    UNKNOWN
}

fun getWeatherDescription(code: Int): String = when (code) {
    0 -> "Ciel dégagé"
    1 -> "Principalement dégagé"
    2 -> "Partiellement nuageux"
    3 -> "Couvert"
    45, 48 -> "Brouillard"
    51, 53, 55 -> "Bruine"
    56, 57 -> "Bruine verglaçante"
    61, 63, 65 -> "Pluie"
    66, 67 -> "Pluie verglaçante"
    71, 73, 75 -> "Neige"
    77 -> "Grains de neige"
    80, 81, 82 -> "Averses de pluie"
    85, 86 -> "Averses de neige"
    95 -> "Orage"
    96, 99 -> "Orage avec grêle"
    else -> "Inconnu"
}

fun getWeatherIcon(code: Int, isDay: Boolean): WeatherIcon = when (code) {
    0 -> if (isDay) WeatherIcon.CLEAR_DAY else WeatherIcon.CLEAR_NIGHT
    1, 2 -> if (isDay) WeatherIcon.PARTLY_CLOUDY_DAY else WeatherIcon.PARTLY_CLOUDY_NIGHT
    3 -> WeatherIcon.CLOUDY
    45, 48 -> WeatherIcon.FOG
    51, 53, 55, 56, 57 -> WeatherIcon.DRIZZLE
    61, 63, 65, 66, 67, 80, 81, 82 -> WeatherIcon.RAIN
    71, 73, 75, 77, 85, 86 -> WeatherIcon.SNOW
    95, 96, 99 -> WeatherIcon.THUNDERSTORM
    else -> WeatherIcon.UNKNOWN
}
