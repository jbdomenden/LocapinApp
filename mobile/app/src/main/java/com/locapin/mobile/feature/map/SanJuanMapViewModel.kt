package com.locapin.mobile.feature.map

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.locapin.mobile.core.common.LocaPinResult
import com.locapin.mobile.domain.model.ZoneAttraction
import com.locapin.mobile.domain.repository.AuthRepository
import com.locapin.mobile.domain.repository.SegmentedMapRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SanJuanMapViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val mapRepository: SegmentedMapRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SanJuanMapUiState(
            sectors = SanJuanMapData.sectors,
            currentScale = 1f,
            currentOffset = Offset.Zero,
            isAdsDisabled = false,
            showInterstitialAd = false
        )
    )
    val uiState: StateFlow<SanJuanMapUiState> = _uiState.asStateFlow()

    private val _attractions = MutableStateFlow<Map<String, List<Attraction>>>(emptyMap())
    val attractions: StateFlow<Map<String, List<Attraction>>> = _attractions.asStateFlow()

    init {
        observeData()
        startAdCycle()
    }

    private fun observeData() {
        viewModelScope.launch {
            // Observe zones from repository but keep the high-quality polygon data from SanJuanMapData
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
                    val grouped = result.data.groupBy { it.zoneId }
                        .mapValues { entry ->
                            entry.value.map {
                                Attraction(
                                    name = it.name,
                                    knownFor = it.knownFor,
                                    distance = it.distance ?: "N/A",
                                    imageUrl = it.imageUrl ?: "",
                                    description = it.description
                                )
                            }
                        }
                    _attractions.value = grouped
                    
                    _uiState.update { state ->
                        state.copy(sectors = state.sectors.map { sector ->
                            sector.copy(attractionsCount = grouped[sector.id]?.size ?: 0)
                        })
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
        _uiState.update { it.copy(isAdsDisabled = true) }
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
}
