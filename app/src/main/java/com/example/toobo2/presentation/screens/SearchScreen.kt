package com.example.toobo2.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.toobo2.domain.model.Location
import com.example.toobo2.presentation.components.CurrentWeatherCard
import com.example.toobo2.presentation.components.DailyForecastCard
import com.example.toobo2.presentation.components.HourlyForecastCard
import com.example.toobo2.presentation.components.WeatherDetailsCard
import com.example.toobo2.presentation.viewmodel.SearchViewModel
import com.example.toobo2.presentation.viewmodel.WeatherViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    weatherViewModel: WeatherViewModel,
    searchViewModel: SearchViewModel = hiltViewModel()
) {
    val weatherUiState by weatherViewModel.uiState.collectAsState()
    val searchUiState by searchViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    if (weatherUiState.selectedLocation != null && weatherUiState.selectedLocationWeather != null) {
        // Show weather for selected location
        Box(modifier = Modifier.fillMaxSize()) {
            PullToRefreshBox(
                isRefreshing = weatherUiState.isRefreshing,
                onRefresh = { weatherViewModel.refreshSelectedLocation() },
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
                        IconButton(onClick = { weatherViewModel.clearSelectedLocation() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Retour"
                            )
                        }
                        Text(
                            text = "Retour à la recherche",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    val weather = weatherUiState.selectedLocationWeather!!
                    CurrentWeatherCard(
                        weather = weather.current,
                        locationName = weather.location.displayName,
                        isCurrentLocation = false,
                        showSaveButton = true,
                        isSaved = weatherUiState.isSelectedLocationSaved,
                        onSaveToggle = {
                            if (weatherUiState.isSelectedLocationSaved) {
                                weatherViewModel.removeSelectedLocationFromFavorites()
                            } else {
                                weatherViewModel.saveSelectedLocation()
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
    } else if (weatherUiState.selectedLocation != null && weatherUiState.isLoading) {
        // Loading weather for selected location
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        // Show search interface
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                SearchBar(
                    query = searchUiState.query,
                    onQueryChange = searchViewModel::onQueryChange,
                    onClear = { searchViewModel.onQueryChange("") },
                    modifier = Modifier.padding(16.dp)
                )

                when {
                    searchUiState.isSearching -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    searchUiState.error != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = searchUiState.error ?: "Erreur inconnue",
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    searchUiState.hasSearched && searchUiState.results.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Aucun résultat trouvé",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    searchUiState.results.isNotEmpty() -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(searchUiState.results) { location ->
                                LocationItem(
                                    location = location,
                                    onClick = { weatherViewModel.selectLocation(location) },
                                    onSave = {
                                        scope.launch {
                                            searchViewModel.saveLocation(location)
                                            snackbarHostState.showSnackbar("${location.name} ajouté aux favoris")
                                        }
                                    },
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        }
                    }

                    else -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = null,
                                    modifier = Modifier.padding(16.dp),
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                )
                                Text(
                                    text = "Recherchez une ville",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Entrez le nom d'une ville...") },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(Icons.Default.Clear, contentDescription = "Effacer")
                }
            }
        },
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        shape = MaterialTheme.shapes.large
    )
}

@Composable
private fun LocationItem(
    location: Location,
    onClick: () -> Unit,
    onSave: () -> Unit,
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

            IconButton(onClick = onSave) {
                Icon(
                    Icons.Default.BookmarkAdd,
                    contentDescription = "Sauvegarder",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
