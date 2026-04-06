package com.locapin.mobile.feature.explore

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.locapin.mobile.domain.model.Destination
import com.locapin.mobile.ui.MainViewModel
import com.locapin.mobile.ui.components.DestinationCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(vm: MainViewModel, onDetails: (String) -> Unit) {
    val state by vm.state.collectAsStateWithLifecycle()
    var selectedDestination by remember { mutableStateOf<Destination?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val hasActiveFilters = state.attractionsQuery.isNotBlank() ||
        !state.selectedAttractionCategory.isNullOrBlank() ||
        !state.selectedAttractionArea.isNullOrBlank()

    Column(Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = state.attractionsQuery,
            onValueChange = vm::setAttractionsQuery,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            label = { Text("Search attractions") },
            singleLine = true
        )
        FilterChipRow(
            title = "Category",
            options = state.attractionCategoryFilters,
            selected = state.selectedAttractionCategory,
            onSelect = vm::setAttractionsCategory
        )
        FilterChipRow(
            title = "Area",
            options = state.attractionAreaFilters,
            selected = state.selectedAttractionArea,
            onSelect = vm::setAttractionsArea
        )
        if (hasActiveFilters) {
            OutlinedButton(
                onClick = vm::clearAttractionsFilters,
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .padding(top = 4.dp)
            ) {
                Text("Clear filters")
            }
        }

        if (state.destinations.isEmpty()) {
            EmptyAttractionsState(
                title = "No attractions yet",
                subtitle = "Attractions will appear here once local data is available."
            )
        } else if (state.filteredAttractions.isEmpty()) {
            val isSearchOnly = state.attractionsQuery.isNotBlank() &&
                state.selectedAttractionCategory.isNullOrBlank() &&
                state.selectedAttractionArea.isNullOrBlank()
            EmptyAttractionsState(
                title = if (isSearchOnly) "No search results" else "No matching attractions",
                subtitle = if (isSearchOnly) {
                    "Try another attraction name or known-for keyword."
                } else {
                    "Try adjusting your category, area, or search filters."
                },
                onClearFilters = if (hasActiveFilters) vm::clearAttractionsFilters else null
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(state.filteredAttractions, key = { _, item -> item.id }) { _, destination ->
                    DestinationCard(
                        destination = destination,
                        onClick = {
                            selectedDestination = destination
                            onDetails(destination.id)
                        },
                        onFavoriteToggle = { vm.toggleFavorite(destination) }
                    )
                }
            }
        }
    }

    val detail = selectedDestination
    if (detail != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedDestination = null },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                androidx.compose.foundation.layout.Row(modifier = Modifier.fillMaxWidth()) {
                    Text(detail.name, style = MaterialTheme.typography.titleLarge)
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = { vm.toggleFavorite(detail) }) {
                        Icon(
                            imageVector = if (detail.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (detail.isFavorite) "Remove from favorites" else "Add to favorites"
                        )
                    }
                }
                Text(detail.description.ifBlank { "No description available." }, style = MaterialTheme.typography.bodyMedium)
                Text("Known for: ${detail.knownFor}")
                Text("Category: ${detail.categoryName}")
                Text("Area: ${detail.area}")
                Button(
                    onClick = { selectedDestination = null },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
private fun FilterChipRow(
    title: String,
    options: List<String>,
    selected: String?,
    onSelect: (String?) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selected.isNullOrBlank(),
                    onClick = { onSelect(null) },
                    label = { Text("All") }
                )
            }
            items(options, key = { it }) { option ->
                FilterChip(
                    selected = selected == option,
                    onClick = { onSelect(option) },
                    label = { Text(option) }
                )
            }
        }
    }
}

@Composable
private fun EmptyAttractionsState(
    title: String,
    subtitle: String,
    onClearFilters: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (onClearFilters != null) {
                OutlinedButton(onClick = onClearFilters) {
                    Text("Reset filters")
                }
            }
        }
    }
}
