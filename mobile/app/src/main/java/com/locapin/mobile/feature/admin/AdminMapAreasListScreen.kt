package com.locapin.mobile.feature.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.ui.graphics.vector.ImageVector
import com.locapin.mobile.R
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.locapin.mobile.core.designsystem.theme.LocaPinBorder
import com.locapin.mobile.core.designsystem.theme.LocaPinCardBackground
import com.locapin.mobile.core.designsystem.theme.LocaPinFieldBackground
import com.locapin.mobile.core.designsystem.theme.LocaPinPrimary
import com.locapin.mobile.core.designsystem.theme.LocaPinSurface
import com.locapin.mobile.core.designsystem.theme.LocaPinWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMapAreasListScreen(
    onBack: () -> Unit,
    viewModel: AdminMapAreasListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        containerColor = LocaPinSurface,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Manage Map Areas", color = LocaPinPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = LocaPinPrimary)
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = LocaPinSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search map area by name", color = LocaPinPrimary.copy(alpha = 0.5f)) },
                singleLine = true,
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LocaPinPrimary,
                    unfocusedBorderColor = LocaPinBorder,
                    focusedLabelColor = LocaPinPrimary,
                    cursorColor = LocaPinPrimary,
                    focusedContainerColor = LocaPinFieldBackground,
                    unfocusedContainerColor = LocaPinFieldBackground
                )
            )

            if (uiState.mapAreas.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = LocaPinCardBackground.copy(alpha = 0.5f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LocaPinBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (uiState.searchQuery.isBlank()) {
                                "No map areas found."
                            } else {
                                "No map areas match your search."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = LocaPinPrimary,
                            fontWeight = FontWeight.Medium,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(uiState.mapAreas, key = { it.id }) { area ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = LocaPinCardBackground.copy(alpha = 0.8f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, LocaPinBorder)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = area.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = LocaPinPrimary
                                        )
                                        if (area.isPremium) {
                                            Icon(
                                                Icons.Default.Star,
                                                contentDescription = "Premium",
                                                tint = Color(0xFFFFD700),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                    
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(
                                                try {
                                                    Color(android.graphics.Color.parseColor(area.hexColor))
                                                } catch (e: Exception) {
                                                    Color.Gray
                                                }
                                            )
                                            .border(1.dp, LocaPinBorder, CircleShape)
                                    )
                                }

                                if (area.description.isNotBlank()) {
                                    Text(
                                        text = area.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = LocaPinPrimary.copy(alpha = 0.8f),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                if (area.districtLabel.isNotBlank()) {
                                    Text(
                                        text = area.districtLabel,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = LocaPinPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "Center: %.5f, %.5f".format(area.centerLatitude, area.centerLongitude),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = LocaPinPrimary.copy(alpha = 0.6f),
                                    fontWeight = FontWeight.Bold
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = "Premium",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = LocaPinPrimary
                                        )
                                        androidx.compose.foundation.layout.Spacer(Modifier.size(8.dp))
                                        Switch(
                                            checked = area.isPremium,
                                            onCheckedChange = { viewModel.togglePremium(area.id) },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = LocaPinPrimary,
                                                checkedTrackColor = LocaPinPrimary.copy(alpha = 0.5f),
                                                uncheckedThumbColor = LocaPinWhite,
                                                uncheckedTrackColor = LocaPinBorder
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
