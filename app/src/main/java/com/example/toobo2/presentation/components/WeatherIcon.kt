package com.example.toobo2.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.FilterDrama
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.toobo2.domain.model.WeatherIcon
import com.example.toobo2.ui.theme.CloudyGray
import com.example.toobo2.ui.theme.NightBlue
import com.example.toobo2.ui.theme.RainyBlue
import com.example.toobo2.ui.theme.SnowWhite
import com.example.toobo2.ui.theme.SunnyYellow
import com.example.toobo2.ui.theme.ThunderPurple

@Composable
fun WeatherIconView(
    weatherIcon: WeatherIcon,
    modifier: Modifier = Modifier,
    tint: Color? = null
) {
    val (icon, defaultTint) = when (weatherIcon) {
        WeatherIcon.CLEAR_DAY -> Icons.Default.WbSunny to SunnyYellow
        WeatherIcon.CLEAR_NIGHT -> Icons.Default.NightsStay to NightBlue
        WeatherIcon.PARTLY_CLOUDY_DAY -> Icons.Default.FilterDrama to SunnyYellow
        WeatherIcon.PARTLY_CLOUDY_NIGHT -> Icons.Default.WbCloudy to NightBlue
        WeatherIcon.CLOUDY -> Icons.Default.Cloud to CloudyGray
        WeatherIcon.FOG -> Icons.Default.Air to CloudyGray
        WeatherIcon.DRIZZLE -> Icons.Default.Grain to RainyBlue
        WeatherIcon.RAIN -> Icons.Default.WaterDrop to RainyBlue
        WeatherIcon.SNOW -> Icons.Default.Grain to SnowWhite
        WeatherIcon.THUNDERSTORM -> Icons.Default.Thunderstorm to ThunderPurple
        WeatherIcon.UNKNOWN -> Icons.Default.Cloud to MaterialTheme.colorScheme.onSurface
    }

    Icon(
        imageVector = icon,
        contentDescription = weatherIcon.name,
        modifier = modifier,
        tint = tint ?: defaultTint
    )
}
