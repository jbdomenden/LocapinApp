package com.locapin.mobile.feature.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.locapin.mobile.ui.components.SectorBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SanJuanCityMapScreen(vm: SanJuanMapViewModel = hiltViewModel()) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    var showSheet by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val selectedSector = state.sectors.firstOrNull { it.id == state.selectedSectorId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3EBD7))
            .padding(horizontal = 12.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "CITY MAP OF",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black
        )
        Text(
            text = "SAN JUAN",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black
        )

        SanJuanMapCanvas(
            sectors = state.sectors,
            selectedSectorId = state.selectedSectorId,
            scale = state.currentScale,
            offset = state.currentOffset,
            onTransformChanged = vm::onTransformChanged,
            onSectorTapped = { sector ->
                vm.onSectorTapped(sector?.id)
                showSheet = sector != null
            }
        )

        Text(
            text = selectedSector?.name ?: "Tap a sector to explore attractions",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF4A4A4A)
        )
    }

    if (showSheet && selectedSector != null) {
        val attractions = SanJuanMapData.attractionsBySector[selectedSector.id].orEmpty()
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = bottomSheetState
        ) {
            SectorBottomSheet(
                sectorName = selectedSector.name,
                attractionsCount = selectedSector.attractionsCount,
                attractions = attractions,
                onViewAttractions = { showSheet = false }
            )
        }
    }
}
