package com.locapin.mobile.feature.explore

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.locapin.mobile.ui.MainViewModel
import com.locapin.mobile.ui.components.DestinationCard

@Composable
fun ExploreScreen(vm: MainViewModel, onDetails: (String) -> Unit) {
    val state by vm.state.collectAsStateWithLifecycle()
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    
    // Reset selection if categories change and current selection is no longer available
    LaunchedEffect(state.categories) {
        if (selectedCategoryId != null && state.categories.none { it.id == selectedCategoryId }) {
            selectedCategoryId = null
        }
    }

    Column(Modifier.fillMaxSize()) {
        if (state.categories.isNotEmpty()) {
            val selectedTabIndex = if (selectedCategoryId == null) 0 
                else (state.categories.indexOfFirst { it.id == selectedCategoryId }.takeIf { it != -1 } ?: 0) + 1
            
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = 16.dp,
                divider = {}
            ) {
                Tab(
                    selected = selectedCategoryId == null,
                    onClick = { selectedCategoryId = null },
                    text = { Text("All") }
                )
                state.categories.forEach { c ->
                    Tab(
                        selected = selectedCategoryId == c.id,
                        onClick = { selectedCategoryId = c.id },
                        text = { Text(c.name) }
                    )
                }
            }
        }
        
        LazyVerticalGrid(
            columns = GridCells.Adaptive(160.dp),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val filteredDestinations = if (selectedCategoryId == null) {
                state.destinations
            } else {
                val categoryName = state.categories.find { it.id == selectedCategoryId }?.name
                state.destinations.filter { it.categoryName == categoryName }
            }
            
            items(filteredDestinations, key = { it.id }) { destination ->
                DestinationCard(destination = destination, onClick = { onDetails(destination.id) })
            }
        }
    }
}
