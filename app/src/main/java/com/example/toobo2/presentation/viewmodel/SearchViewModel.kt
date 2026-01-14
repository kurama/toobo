package com.example.toobo2.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toobo2.data.repository.LocationRepository
import com.example.toobo2.domain.model.Location
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val results: List<Location> = emptyList(),
    val isSearching: Boolean = false,
    val error: String? = null,
    val hasSearched: Boolean = false
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val locationRepository: LocationRepository
) : ViewModel() {

    companion object {
        private const val SEARCH_TIMEOUT_MS = 15000L
        private const val DEBOUNCE_MS = 300L
    }

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(query = query)

        searchJob?.cancel()

        if (query.length < 2) {
            _uiState.value = _uiState.value.copy(
                results = emptyList(),
                hasSearched = false,
                error = null,
                isSearching = false
            )
            return
        }

        searchJob = viewModelScope.launch {
            delay(DEBOUNCE_MS)
            search(query)
        }
    }

    private suspend fun search(query: String) {
        _uiState.value = _uiState.value.copy(isSearching = true, error = null)

        try {
            val result = withTimeout(SEARCH_TIMEOUT_MS) {
                locationRepository.searchCities(query)
            }

            result.onSuccess { locations ->
                _uiState.value = _uiState.value.copy(
                    results = locations,
                    isSearching = false,
                    hasSearched = true
                )
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isSearching = false,
                    hasSearched = true,
                    error = exception.message ?: "Erreur de recherche"
                )
            }
        } catch (e: TimeoutCancellationException) {
            _uiState.value = _uiState.value.copy(
                isSearching = false,
                hasSearched = true,
                error = "Délai d'attente dépassé. Vérifiez votre connexion."
            )
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                isSearching = false,
                hasSearched = true,
                error = e.message ?: "Erreur inconnue"
            )
        }
    }

    suspend fun saveLocation(location: Location): Long {
        return locationRepository.saveLocation(location)
    }

    suspend fun isLocationSaved(location: Location): Boolean {
        return locationRepository.isLocationSaved(location.latitude, location.longitude)
    }

    fun clearSearch() {
        searchJob?.cancel()
        _uiState.value = SearchUiState()
    }
}
