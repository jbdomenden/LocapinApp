package com.locapin.mobile.feature.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.locapin.mobile.core.location.LocationProvider
import com.locapin.mobile.domain.model.ZoneAttraction
import com.locapin.mobile.domain.repository.HistoryRepository
import com.locapin.mobile.domain.repository.VisitedAttraction
import com.locapin.mobile.feature.map.AttractionDetailSheetContent
import com.locapin.mobile.domain.repository.TouristFavoritesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

data class HistoryUiState(
    val isLoading: Boolean = true,
    val items: List<VisitedAttraction> = emptyList(),
    val favoriteIds: Set<String> = emptySet(),
    val currentLocation: Pair<Double, Double>? = null
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val favoritesRepository: TouristFavoritesRepository,
    private val locationProvider: LocationProvider
) : ViewModel() {
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                historyRepository.history,
                favoritesRepository.favoriteIds
            ) { history, favorites ->
                _uiState.update { it.copy(items = history, favoriteIds = favorites, isLoading = false) }
            }.collect()
        }
    }

    fun toggleFavorite(id: String) {
        viewModelScope.launch {
            val isFavorite = _uiState.value.favoriteIds.contains(id)
            favoritesRepository.setFavorite(id, !isFavorite)
        }
    }

    fun refreshLocation() {
        viewModelScope.launch {
            _uiState.update { it.copy(currentLocation = locationProvider.getLastKnownLocation()) }
        }
    }

    fun distanceTextFor(item: VisitedAttraction): String {
        val user = _uiState.value.currentLocation ?: return "Current location unavailable"
        val result = FloatArray(1)
        android.location.Location.distanceBetween(user.first, user.second, item.latitude, item.longitude, result)
        val meters = result[0]
        return if (meters < 1000) "${meters.roundToInt()} m away" else "${"%.1f".format(meters / 1000f)} km away"
    }

    fun revisit(item: VisitedAttraction) {
        viewModelScope.launch {
            historyRepository.recordVisit(
                ZoneAttraction(
                    id = item.id,
                    name = item.name,
                    description = item.description,
                    knownFor = item.knownFor,
                    latitude = item.latitude,
                    longitude = item.longitude,
                    zoneId = "history"
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(vm: HistoryViewModel = hiltViewModel()) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selected by remember { mutableStateOf<VisitedAttraction?>(null) }

    LaunchedEffect(Unit) { vm.refreshLocation() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Visited Attractions", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        when {
            state.isLoading -> {
                Text("Loading history…", color = MaterialTheme.colorScheme.primary)
            }
            state.items.isEmpty() -> {
                Surface(tonalElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "No visits yet. Use the Map module and tap Go to record visited attractions.",
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }
            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.items, key = { it.id + it.visitedAtEpochMs }) { item ->
                        Surface(
                            tonalElevation = 2.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selected = item }
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(item.name, fontWeight = FontWeight.SemiBold)
                                Text(item.knownFor, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }

    selected?.let { item ->
        ModalBottomSheet(
            onDismissRequest = { selected = null },
            sheetState = sheetState
        ) {
            AttractionDetailSheetContent(
                name = item.name,
                description = item.description,
                knownFor = item.knownFor,
                category = item.category,
                area = item.area,
                distanceText = vm.distanceTextFor(item),
                isFavorite = state.favoriteIds.contains(item.id),
                onToggleFavorite = { vm.toggleFavorite(item.id) },
                onGo = { vm.revisit(item) },
                onRefreshDistance = vm::refreshLocation,
                showPermissionAction = false,
                onRequestPermission = {}
            )
        }
    }
}
