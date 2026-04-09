package com.locapin.mobile.feature.map

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

data class MapSector(
    val id: String,
    val name: String,
    val fillColor: Color,
    val labelPosition: Offset,
    val polygonPoints: List<Offset>,
    val attractionsCount: Int = 0
)

data class SanJuanMapUiState(
    val sectors: List<MapSector> = emptyList(),
    val selectedSectorId: String? = null,
    val currentScale: Float = 1f,
    val currentOffset: Offset = Offset.Zero
)
