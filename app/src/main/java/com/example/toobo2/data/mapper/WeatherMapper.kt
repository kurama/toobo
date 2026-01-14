package com.example.toobo2.data.mapper

import com.example.toobo2.data.local.entity.WeatherCacheEntity
import com.example.toobo2.data.remote.dto.WeatherResponse
import com.example.toobo2.domain.model.CurrentWeatherData
import com.example.toobo2.domain.model.DailyWeatherData
import com.example.toobo2.domain.model.HourlyWeatherData
import com.example.toobo2.domain.model.Location
import com.example.toobo2.domain.model.Weather
import com.example.toobo2.domain.model.getWeatherDescription
import com.example.toobo2.domain.model.getWeatherIcon
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

fun WeatherResponse.toWeather(location: Location): Weather {
    val current = current ?: throw IllegalStateException("Current weather data is null")
    val hourly = hourly
    val daily = daily

    val dailyMaxTemp = daily?.temperatureMax?.firstOrNull() ?: current.temperature
    val dailyMinTemp = daily?.temperatureMin?.firstOrNull() ?: current.temperature

    return Weather(
        location = location,
        current = CurrentWeatherData(
            temperature = current.temperature,
            apparentTemperature = current.apparentTemperature,
            humidity = current.relativeHumidity,
            weatherCode = current.weatherCode,
            weatherDescription = getWeatherDescription(current.weatherCode),
            weatherIcon = getWeatherIcon(current.weatherCode, current.isDay == 1),
            windSpeed = current.windSpeed,
            windDirection = current.windDirection,
            precipitation = current.precipitation,
            pressureMsl = current.pressureMsl,
            uvIndex = current.uvIndex ?: 0.0,
            isDay = current.isDay == 1,
            dailyMaxTemp = dailyMaxTemp,
            dailyMinTemp = dailyMinTemp
        ),
        hourly = hourly?.let { mapHourlyData(it) } ?: emptyList(),
        daily = daily?.let { mapDailyData(it) } ?: emptyList()
    )
}

private fun mapHourlyData(hourly: com.example.toobo2.data.remote.dto.HourlyWeather): List<HourlyWeatherData> {
    val now = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("HH:mm")

    return hourly.time.mapIndexedNotNull { index, timeString ->
        val dateTime = LocalDateTime.parse(timeString)
        if (dateTime.isBefore(now.minusHours(1))) {
            null
        } else {
            HourlyWeatherData(
                time = dateTime.format(formatter),
                temperature = hourly.temperature.getOrNull(index) ?: 0.0,
                apparentTemperature = hourly.apparentTemperature.getOrNull(index) ?: 0.0,
                humidity = hourly.relativeHumidity.getOrNull(index) ?: 0,
                precipitation = hourly.precipitation.getOrNull(index) ?: 0.0,
                weatherCode = hourly.weatherCode.getOrNull(index) ?: 0,
                weatherIcon = getWeatherIcon(
                    hourly.weatherCode.getOrNull(index) ?: 0,
                    hourly.isDay.getOrNull(index) == 1
                ),
                windSpeed = hourly.windSpeed.getOrNull(index) ?: 0.0,
                isDay = hourly.isDay.getOrNull(index) == 1
            )
        }
    }.take(24)
}

private fun mapDailyData(daily: com.example.toobo2.data.remote.dto.DailyWeather): List<DailyWeatherData> {
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    return daily.time.mapIndexed { index, dateString ->
        val date = LocalDate.parse(dateString)
        val dayName = if (index == 0) {
            "Aujourd'hui"
        } else if (index == 1) {
            "Demain"
        } else {
            date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.FRENCH)
                .replaceFirstChar { it.uppercase() }
        }

        DailyWeatherData(
            date = date.format(dateFormatter),
            dayName = dayName,
            weatherCode = daily.weatherCode.getOrNull(index) ?: 0,
            weatherIcon = getWeatherIcon(daily.weatherCode.getOrNull(index) ?: 0, true),
            weatherDescription = getWeatherDescription(daily.weatherCode.getOrNull(index) ?: 0),
            maxTemperature = daily.temperatureMax.getOrNull(index) ?: 0.0,
            minTemperature = daily.temperatureMin.getOrNull(index) ?: 0.0,
            sunrise = daily.sunrise.getOrNull(index)?.let {
                LocalDateTime.parse(it).format(timeFormatter)
            } ?: "",
            sunset = daily.sunset.getOrNull(index)?.let {
                LocalDateTime.parse(it).format(timeFormatter)
            } ?: "",
            uvIndexMax = daily.uvIndexMax.getOrNull(index) ?: 0.0,
            precipitationSum = daily.precipitationSum.getOrNull(index) ?: 0.0,
            precipitationProbability = daily.precipitationProbabilityMax?.getOrNull(index),
            windSpeedMax = daily.windSpeedMax.getOrNull(index) ?: 0.0
        )
    }
}

fun Weather.toCacheEntity(locationId: Long): WeatherCacheEntity {
    val gson = Gson()
    return WeatherCacheEntity(
        locationId = locationId,
        temperature = current.temperature,
        apparentTemperature = current.apparentTemperature,
        humidity = current.humidity,
        weatherCode = current.weatherCode,
        windSpeed = current.windSpeed,
        windDirection = current.windDirection,
        precipitation = current.precipitation,
        pressureMsl = current.pressureMsl,
        uvIndex = current.uvIndex,
        isDay = current.isDay,
        dailyMaxTemp = current.dailyMaxTemp,
        dailyMinTemp = current.dailyMinTemp,
        hourlyDataJson = gson.toJson(hourly),
        dailyDataJson = gson.toJson(daily),
        lastUpdated = lastUpdated
    )
}

fun WeatherCacheEntity.toWeather(location: Location): Weather {
    val gson = Gson()
    val hourlyType = object : TypeToken<List<HourlyWeatherData>>() {}.type
    val dailyType = object : TypeToken<List<DailyWeatherData>>() {}.type

    return Weather(
        location = location,
        current = CurrentWeatherData(
            temperature = temperature,
            apparentTemperature = apparentTemperature,
            humidity = humidity,
            weatherCode = weatherCode,
            weatherDescription = getWeatherDescription(weatherCode),
            weatherIcon = getWeatherIcon(weatherCode, isDay),
            windSpeed = windSpeed,
            windDirection = windDirection,
            precipitation = precipitation,
            pressureMsl = pressureMsl,
            uvIndex = uvIndex,
            isDay = isDay,
            dailyMaxTemp = dailyMaxTemp,
            dailyMinTemp = dailyMinTemp
        ),
        hourly = gson.fromJson(hourlyDataJson, hourlyType) ?: emptyList(),
        daily = gson.fromJson(dailyDataJson, dailyType) ?: emptyList(),
        lastUpdated = lastUpdated
    )
}
