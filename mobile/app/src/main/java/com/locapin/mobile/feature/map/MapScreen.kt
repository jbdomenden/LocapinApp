package com.locapin.mobile.feature.map

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    hasLocationPermission: Boolean,
    requestPermission: () -> Unit ,
    vm: SegmentedMapViewModel = hiltViewModel()
) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheet by remember { mutableStateOf(false) }

    LaunchedEffect(hasLocationPermission) {
        vm.onPermissionResult(hasLocationPermission)
    }

    Column(
        modifier = Modifier.padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        MapInstruction()
        state.selectedZoneId?.let { zoneId ->
            Text(
                text = state.zones.firstOrNull { it.id == zoneId }?.displayName ?: zoneId,
                style = MaterialTheme.typography.titleMedium
            )
        }
        SegmentedSanJuanMap(
            zones = state.zones,
            selectedZoneId = state.selectedZoneId,
            visibleAttractions = state.visibleAttractions,
            selectedAttractionId = state.selectedAttractionId,
            userLocation = state.userLocation,
            navigationAttraction = state.navigationAttraction,
            routePath = state.routePath,
            onZoneTapped = {
                vm.onZoneSelected(it)
                showSheet = false
            },
            onPinTapped = {
                vm.onAttractionSelected(it)
                showSheet = true
            }
        )

        state.errorMessage?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        if (!hasLocationPermission) {
            Button(onClick = requestPermission, modifier = Modifier.fillMaxWidth()) {
                Text("Enable location for real-time distance")
            }
        }
    }

    val selectedAttraction = state.selectedAttraction
    if (showSheet && selectedAttraction != null) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState
        ) {
            AttractionDetailSheetContent(
                name = selectedAttraction.name,
                description = selectedAttraction.description,
                knownFor = selectedAttraction.knownFor,
                distanceText = vm.distanceTextFor(selectedAttraction),
                onGo = { vm.onGoToAttraction(selectedAttraction.id) },
                onRefreshDistance = vm::refreshLocation,
                showPermissionAction = !hasLocationPermission,
                onRequestPermission = requestPermission
            )
        }
    }
}
