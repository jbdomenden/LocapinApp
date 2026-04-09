package com.locapin.mobile.feature.map

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class SanJuanMapViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(
        SanJuanMapUiState(
            sectors = SanJuanMapData.sectors,
            currentScale = 1f,
            currentOffset = Offset.Zero
        )
    )
    val uiState: StateFlow<SanJuanMapUiState> = _uiState.asStateFlow()

    fun onTransformChanged(scale: Float, offset: Offset) {
        _uiState.update {
            it.copy(
                currentScale = scale.coerceIn(1f, 4f),
                currentOffset = offset
            )
        }
    }

    fun onSectorTapped(sectorId: String?) {
        _uiState.update { it.copy(selectedSectorId = sectorId) }
    }
}
