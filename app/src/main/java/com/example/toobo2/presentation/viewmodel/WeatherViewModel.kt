package com.example.toobo2.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toobo2.data.repository.LocationRepository
import com.example.toobo2.data.repository.WeatherRepository
import com.example.toobo2.domain.model.Location
import com.example.toobo2.domain.model.Weather
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

data class WeatherUiState(
    // Current GPS location
    val currentLocation: Location? = null,
    val currentLocationWeather: Weather? = null,
    // Selected location (for favorites/search)
    val selectedLocation: Location? = null,
    val selectedLocationWeather: Weather? = null,
    // Saved locations
    val savedLocations: List<Location> = emptyList(),
    // UI state
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val hasLocationPermission: Boolean = false,
    val isSelectedLocationSaved: Boolean = false,
    val saveSuccess: String? = null
)

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    companion object {
        private const val OPERATION_TIMEOUT_MS = 30000L
    }

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    init {
        checkLocationPermission()
        observeSavedLocations()
        observeCurrentLocation()
    }

    private fun checkLocationPermission() {
        _uiState.value = _uiState.value.copy(
            hasLocationPermission = locationRepository.hasLocationPermission()
        )
    }

    private fun observeSavedLocations() {
        viewModelScope.launch {
            locationRepository.getSavedLocations().collectLatest { locations ->
                _uiState.value = _uiState.value.copy(savedLocations = locations)
                checkIfSelectedLocationIsSaved()
            }
        }
    }

    private fun observeCurrentLocation() {
        viewModelScope.launch {
            locationRepository.getCurrentLocationFlow().collectLatest { location ->
                _uiState.value = _uiState.value.copy(currentLocation = location)
                if (location != null && _uiState.value.currentLocationWeather == null) {
                    fetchCurrentLocationWeather(location)
                }
            }
        }
    }

    private fun checkIfSelectedLocationIsSaved() {
        viewModelScope.launch {
            val selectedLocation = _uiState.value.selectedLocation
            if (selectedLocation != null) {
                val isSaved = locationRepository.isLocationSaved(
                    selectedLocation.latitude,
                    selectedLocation.longitude
                )
                _uiState.value = _uiState.value.copy(isSelectedLocationSaved = isSaved)
            }
        }
    }

    fun onPermissionResult(granted: Boolean) {
        _uiState.value = _uiState.value.copy(hasLocationPermission = granted)
        if (granted) {
            updateCurrentLocation()
        }
    }

    fun updateCurrentLocation() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val result = withTimeout(OPERATION_TIMEOUT_MS) {
                    locationRepository.updateCurrentLocation()
                }

                result.onSuccess { location ->
                    _uiState.value = _uiState.value.copy(
                        currentLocation = location,
                        isLoading = false
                    )
                    fetchCurrentLocationWeather(location)
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Erreur lors de la récupération de la position"
                    )
                }
            } catch (e: TimeoutCancellationException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Délai d'attente dépassé. Vérifiez votre connexion."
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Erreur inconnue"
                )
            }
        }
    }

    private fun fetchCurrentLocationWeather(location: Location, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = !forceRefresh,
                isRefreshing = forceRefresh,
                error = null
            )

            try {
                val result = withTimeout(OPERATION_TIMEOUT_MS) {
                    weatherRepository.fetchWeather(location, forceRefresh)
                }

                result.onSuccess { weather ->
                    _uiState.value = _uiState.value.copy(
                        currentLocationWeather = weather,
                        isLoading = false,
                        isRefreshing = false
                    )
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = exception.message ?: "Erreur lors de la récupération de la météo"
                    )
                }
            } catch (e: TimeoutCancellationException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isRefreshing = false,
                    error = "Délai d'attente dépassé. Vérifiez votre connexion."
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isRefreshing = false,
                    error = e.message ?: "Erreur inconnue"
                )
            }
        }
    }

    fun refreshCurrentLocation() {
        _uiState.value.currentLocation?.let { location ->
            fetchCurrentLocationWeather(location, forceRefresh = true)
        }
    }

    fun selectLocation(location: Location) {
        _uiState.value = _uiState.value.copy(
            selectedLocation = location,
            selectedLocationWeather = null,
            error = null
        )
        checkIfSelectedLocationIsSaved()
        fetchSelectedLocationWeather(location)
    }

    private fun fetchSelectedLocationWeather(location: Location, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = !forceRefresh,
                isRefreshing = forceRefresh,
                error = null
            )

            try {
                val result = withTimeout(OPERATION_TIMEOUT_MS) {
                    weatherRepository.fetchWeather(location, forceRefresh)
                }

                result.onSuccess { weather ->
                    _uiState.value = _uiState.value.copy(
                        selectedLocationWeather = weather,
                        isLoading = false,
                        isRefreshing = false
                    )
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = exception.message ?: "Erreur lors de la récupération de la météo"
                    )
                }
            } catch (e: TimeoutCancellationException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isRefreshing = false,
                    error = "Délai d'attente dépassé. Vérifiez votre connexion."
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isRefreshing = false,
                    error = e.message ?: "Erreur inconnue"
                )
            }
        }
    }

    fun refreshSelectedLocation() {
        _uiState.value.selectedLocation?.let { location ->
            fetchSelectedLocationWeather(location, forceRefresh = true)
        }
    }

    fun clearSelectedLocation() {
        _uiState.value = _uiState.value.copy(
            selectedLocation = null,
            selectedLocationWeather = null
        )
    }

    fun saveSelectedLocation() {
        viewModelScope.launch {
            val location = _uiState.value.selectedLocation ?: return@launch

            if (_uiState.value.isSelectedLocationSaved) {
                return@launch
            }

            locationRepository.saveLocation(location)
            _uiState.value = _uiState.value.copy(
                isSelectedLocationSaved = true,
                saveSuccess = "${location.name} ajouté aux favoris"
            )
        }
    }

    fun removeSelectedLocationFromFavorites() {
        viewModelScope.launch {
            val location = _uiState.value.selectedLocation ?: return@launch

            val savedLocations = _uiState.value.savedLocations
            val savedLocation = savedLocations.find {
                it.latitude == location.latitude && it.longitude == location.longitude
            }

            if (savedLocation != null) {
                locationRepository.deleteLocation(savedLocation)
                _uiState.value = _uiState.value.copy(
                    isSelectedLocationSaved = false,
                    saveSuccess = "${location.name} retiré des favoris"
                )
            }
        }
    }

    fun saveLocation(location: Location) {
        viewModelScope.launch {
            locationRepository.saveLocation(location)
        }
    }

    fun deleteLocation(location: Location) {
        viewModelScope.launch {
            locationRepository.deleteLocation(location)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSaveSuccess() {
        _uiState.value = _uiState.value.copy(saveSuccess = null)
    }
}
