package com.example.toobo2.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.toobo2.domain.model.Location
import com.example.toobo2.presentation.components.CurrentWeatherCard
import com.example.toobo2.presentation.components.DailyForecastCard
import com.example.toobo2.presentation.components.HourlyForecastCard
import com.example.toobo2.presentation.components.WeatherDetailsCard
import com.example.toobo2.presentation.viewmodel.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: WeatherViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.selectedLocation != null && uiState.selectedLocationWeather != null) {
        // Show weather for selected location
        Box(modifier = Modifier.fillMaxSize()) {
            PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh = { viewModel.refreshSelectedLocation() },
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Back button row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { viewModel.clearSelectedLocation() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Retour"
                            )
                        }
                        Text(
                            text = "Retour aux favoris",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    val weather = uiState.selectedLocationWeather!!
                    CurrentWeatherCard(
                        weather = weather.current,
                        locationName = weather.location.displayName,
                        isCurrentLocation = false,
                        showSaveButton = true,
                        isSaved = uiState.isSelectedLocationSaved,
                        onSaveToggle = {
                            if (uiState.isSelectedLocationSaved) {
                                viewModel.removeSelectedLocationFromFavorites()
                            } else {
                                viewModel.saveSelectedLocation()
                            }
                        }
                    )

                    WeatherDetailsCard(weather = weather.current)

                    if (weather.hourly.isNotEmpty()) {
                        HourlyForecastCard(hourlyData = weather.hourly)
                    }

                    if (weather.daily.isNotEmpty()) {
                        DailyForecastCard(dailyData = weather.daily)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    } else if (uiState.selectedLocation != null && uiState.isLoading) {
        // Loading weather for selected location
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        // Show favorites list
        if (uiState.savedLocations.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Bookmark,
                        contentDescription = null,
                        modifier = Modifier.padding(16.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                    Text(
                        text = "Aucun favori",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Ajoutez des villes depuis la recherche",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(
                    items = uiState.savedLocations,
                    key = { it.id }
                ) { location ->
                    FavoriteLocationItem(
                        location = location,
                        onClick = { viewModel.selectLocation(location) },
                        onDelete = { viewModel.deleteLocation(location) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoriteLocationItem(
    location: Location,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = location.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (location.admin1 != null || location.country != null) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = listOfNotNull(location.admin1, location.country)
                                .joinToString(", "),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Supprimer",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
