package com.locapin.mobile.feature.map

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.locapin.mobile.core.navigation.DirectionsLaunchResult
import com.locapin.mobile.core.navigation.DirectionsLauncher

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    permissionUiState: LocationPermissionUiState,
    requestPermission: () -> Unit,
    openAppSettings: () -> Unit,
    vm: SegmentedMapViewModel = hiltViewModel()
) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheet by remember { mutableStateOf(false) }

    LaunchedEffect(permissionUiState) {
        vm.onPermissionResult(permissionUiState)
    }

    Column(
        modifier = Modifier.padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        MapInstruction()
        if (state.isLoading) {
            CircularProgressIndicator()
        }
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

        when (permissionUiState) {
            LocationPermissionUiState.PERMANENTLY_DENIED -> {
                InfoCard(
                    message = "Location access is off for this app. You can still browse attractions without it.",
                    actionLabel = "Open Settings",
                    onAction = openAppSettings
                )
            }

            LocationPermissionUiState.DENIED,
            LocationPermissionUiState.UNKNOWN -> {
                InfoCard(
                    message = "Location access is needed to show your live distance to attractions."
                )
            }

            LocationPermissionUiState.GRANTED -> Unit
        }

        state.errorMessage?.let {
            ErrorCard(message = it, onRetry = vm::refreshLocation, onDismiss = vm::clearErrorMessage)
        }

        if (!state.isLoading && state.selectedZoneId != null && state.visibleAttractions.isEmpty()) {
            Text(
                text = "No attractions currently available for this area.",
                style = MaterialTheme.typography.bodySmall
            )
        }

        if (!state.isLoading && state.zones.isEmpty()) {
            ErrorCard(
                message = "No map areas are available right now.",
                onRetry = vm::loadMapData
            )
        }

        if (permissionUiState != LocationPermissionUiState.GRANTED) {
            val actionLabel = if (permissionUiState == LocationPermissionUiState.PERMANENTLY_DENIED) {
                "Open app settings"
            } else {
                "Enable location"
            }
            val action = if (permissionUiState == LocationPermissionUiState.PERMANENTLY_DENIED) {
                openAppSettings
            } else {
                requestPermission
            }
            Button(onClick = action, modifier = Modifier.fillMaxWidth()) {
                Text(actionLabel)
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
                category = selectedAttraction.category,
                area = selectedAttraction.area,
                distanceText = vm.distanceTextFor(selectedAttraction),
                isFavorite = vm.isFavoriteAttraction(selectedAttraction.id),
                onToggleFavorite = { vm.toggleFavorite(selectedAttraction.id) },
                onGo = {
                    when (
                        DirectionsLauncher.launch(
                            context = context,
                            latitude = selectedAttraction.latitude,
                            longitude = selectedAttraction.longitude,
                            destinationLabel = selectedAttraction.name
                        )
                    ) {
                        DirectionsLaunchResult.Launched -> vm.onGoToAttraction(selectedAttraction.id)
                        DirectionsLaunchResult.InvalidCoordinates -> vm.showErrorMessage("This attraction has invalid coordinates.")
                        DirectionsLaunchResult.NoNavigationApp -> vm.showErrorMessage("No navigation app is available on this device.")
                    }
                },
                onRefreshDistance = vm::refreshLocation,
                showPermissionAction = permissionUiState != LocationPermissionUiState.GRANTED,
                onRequestPermission = {
                    if (permissionUiState == LocationPermissionUiState.PERMANENTLY_DENIED) openAppSettings()
                    else requestPermission()
                }
            )
        }
    }
}

@Composable
private fun InfoCard(message: String, actionLabel: String? = null, onAction: (() -> Unit)? = null) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = message, style = MaterialTheme.typography.bodySmall)
            if (actionLabel != null && onAction != null) {
                OutlinedButton(onClick = onAction) { Text(actionLabel) }
            }
        }
    }
}

@Composable
private fun ErrorCard(message: String, onRetry: () -> Unit, onDismiss: (() -> Unit)? = null) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onRetry) { Text("Retry") }
                if (onDismiss != null) {
                    OutlinedButton(onClick = onDismiss) { Text("Dismiss") }
                }
            }
        }
    }
}
