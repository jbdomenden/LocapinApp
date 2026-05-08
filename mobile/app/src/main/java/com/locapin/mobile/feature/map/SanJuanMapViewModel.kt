package com.locapin.mobile.feature.map

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.locapin.mobile.feature.admin.AdminMapAreaRepository
import com.locapin.mobile.core.common.LocaPinResult
import com.locapin.mobile.domain.model.MapZone
import com.locapin.mobile.domain.model.ZoneAttraction
import com.locapin.mobile.domain.repository.AuthRepository
import com.locapin.mobile.domain.repository.SegmentedMapRepository
import com.locapin.mobile.data.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SanJuanMapViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val mapRepository: SegmentedMapRepository,
    private val adminMapAreaRepository: AdminMapAreaRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SanJuanMapUiState(
            sectors = SanJuanMapData.sectors,
            currentScale = 1f,
            currentOffset = Offset.Zero,
            isAdsDisabled = false,
            showInterstitialAd = false,
            isLoading = false
        )
    )
    val uiState: StateFlow<SanJuanMapUiState> = _uiState.asStateFlow()

    private val _attractions = MutableStateFlow<Map<String, List<Attraction>>>(SanJuanMapData.attractionsBySector)
    val attractions: StateFlow<Map<String, List<Attraction>>> = _attractions.asStateFlow()

    init {
        observeData()
        startAdCycle()
    }

    private fun observeData() {
        viewModelScope.launch {
            // Observe premium status and ads from DataStore
            launch {
                combine(
                    sessionManager.premiumSectorsFlow,
                    sessionManager.adsDisabledFlow
                ) { premiumSectors, adsDisabled ->
                    Pair(premiumSectors, adsDisabled)
                }.collect { (premiumSectors, adsDisabled) ->
                    _uiState.update { it.copy(
                        premiumAccessSectors = premiumSectors,
                        isAdsDisabled = adsDisabled
                    ) }
                }
            }

            // Observe zones from repository and sync all attributes including polygons
            launch {
                adminMapAreaRepository.mapAreas.collect { adminAreas ->
                    _uiState.update { state ->
                        state.copy(sectors = state.sectors.map { sector ->
                            val adminArea = adminAreas.find { it.id == sector.id }
                            if (adminArea != null) {
                                sector.copy(
                                    name = adminArea.name,
                                    isPremium = adminArea.isPremium,
                                    fillColor = try {
                                        Color(android.graphics.Color.parseColor(adminArea.hexColor))
                                    } catch (e: Exception) {
                                        sector.fillColor
                                    }
                                )
                            } else {
                                sector
                            }
                        })
                    }
                }
            }

            val zonesResult = mapRepository.getMapZones()
            if (zonesResult is LocaPinResult.Success && zonesResult.data.isNotEmpty()) {
                _uiState.update { state ->
                    val updatedSectors = state.sectors.map { sector ->
                        val zone = zonesResult.data.find { it.id == sector.id }
                        if (zone != null) {
                            sector.copy(name = zone.displayName)
                        } else {
                            sector
                        }
                    }
                    state.copy(sectors = updatedSectors)
                }
            }

            // Observe attractions from repository
            mapRepository.getZoneAttractionsFlow().collectLatest { result ->
                if (result is LocaPinResult.Success<List<ZoneAttraction>>) {
                    // Use only repo data
                    val groupedFromRepo = result.data.groupBy { it.zoneId }
                        .mapValues { entry ->
                            entry.value.map {
                                Attraction(
                                    id = it.id,
                                    name = it.name,
                                    knownFor = it.knownFor,
                                    distance = it.distance ?: "N/A",
                                    imageUrl = it.imageUrl ?: "",
                                    description = it.description,
                                    latitude = it.latitude,
                                    longitude = it.longitude,
                                    category = it.category ?: "Attraction",
                                    rating = it.rating,
                                    reviews = it.reviews
                                )
                            }
                        }
                    
                    _attractions.value = groupedFromRepo
                    
                    _uiState.update { state ->
                        state.copy(
                            sectors = state.sectors.map { sector ->
                                sector.copy(attractionsCount = groupedFromRepo[sector.id]?.size ?: 0)
                            },
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    private fun refreshAttractions() {
        // No longer needed as we are collecting a Flow
    }

    private fun startAdCycle() {
        viewModelScope.launch {
            while (true) {
                delay(300000) // Show ad every 5 minutes (300,000 ms)
                if (!_uiState.value.isAdsDisabled) {
                    _uiState.update { it.copy(showInterstitialAd = true) }
                }
            }
        }
    }

    fun dismissAd() {
        _uiState.update { it.copy(showInterstitialAd = false) }
    }

    fun disableAds() {
        viewModelScope.launch {
            sessionManager.setAdsDisabled(true)
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun onTransformChanged(scale: Float, offset: Offset) {
        _uiState.update {
            val newScale = scale.coerceIn(1f, 4f)
            val newOffset = if (newScale <= 1f) Offset.Zero else offset
            it.copy(
                currentScale = newScale,
                currentOffset = newOffset
            )
        }
    }

    fun onSectorTapped(sectorId: String?) {
        val sector = _uiState.value.sectors.find { it.id == sectorId }
        
        if (sector != null && sector.isPremium && !_uiState.value.premiumAccessSectors.contains(sector.id)) {
            _uiState.update { it.copy(showPremiumPrompt = sector) }
            return
        }

        _uiState.update { 
            val newTapCount = if (sectorId != null) it.tapCount + 1 else it.tapCount
            val shouldShowAd = !it.isAdsDisabled && newTapCount >= 20
            
            it.copy(
                selectedSectorId = sectorId,
                tapCount = if (shouldShowAd) 0 else newTapCount,
                showInterstitialAd = it.showInterstitialAd || shouldShowAd
            )
        }
    }

    fun dismissPremiumPrompt() {
        _uiState.update { it.copy(showPremiumPrompt = null) }
    }

    fun watchAdForPremium(sectorId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(showPremiumPrompt = null, showInterstitialAd = true) }
            // Wait for ad to "finish"
            delay(5000)
            _uiState.update { state ->
                state.copy(
                    premiumAccessSectors = state.premiumAccessSectors + sectorId
                )
            }
            // Optional: revoke access after 5 minutes
            launch {
                delay(300_000)
                _uiState.update { state ->
                    state.copy(
                        premiumAccessSectors = state.premiumAccessSectors - sectorId,
                        selectedSectorId = if (state.selectedSectorId == sectorId) null else state.selectedSectorId
                    )
                }
            }
        }
    }

    fun buyPremiumAccess(sectorId: String) {
        viewModelScope.launch {
            sessionManager.savePremiumSector(sectorId)
            _uiState.update { state ->
                state.copy(showPremiumPrompt = null)
            }
        }
    }

    fun toggleLegend() {
        _uiState.update { it.copy(isLegendVisible = !it.isLegendVisible) }
    }

    fun toggleLabels() {
        _uiState.update { it.copy(showLabels = !it.showLabels) }
    }

    private fun parsePolygonPoints(pointsStr: String): List<Offset> {
        if (pointsStr.isBlank()) return emptyList()
        return try {
            pointsStr.split(";").map {
                val coords = it.split(",")
                Offset(coords[0].toFloat(), coords[1].toFloat())
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
