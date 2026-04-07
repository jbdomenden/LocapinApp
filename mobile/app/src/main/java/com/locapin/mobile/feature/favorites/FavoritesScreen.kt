package com.locapin.mobile.feature.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.locapin.mobile.feature.common.CenteredStateCard
import com.locapin.mobile.ui.MainViewModel
import com.locapin.mobile.ui.components.DestinationCard

@Composable
fun FavoritesScreen(vm: MainViewModel, onDetails: (String) -> Unit) {
    val state by vm.state.collectAsStateWithLifecycle()
    if (state.favorites.isEmpty()) {
        CenteredStateCard(
            title = "No favorites saved yet",
            description = "Save attractions from the list, map, or details screen to see them here."
        )
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(state.favorites, key = { it.id }) { item ->
            DestinationCard(
                destination = item,
                onClick = { onDetails(item.id) },
                onFavoriteToggle = { vm.toggleFavorite(item) }
            )
        }
    }
}
