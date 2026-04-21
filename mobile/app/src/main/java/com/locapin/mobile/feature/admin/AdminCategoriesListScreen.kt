package com.locapin.mobile.feature.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
fun AdminCategoriesListScreen(
    onBack: () -> Unit,
    onCreateCategory: () -> Unit,
    onEditCategory: (String) -> Unit,
    viewModel: AdminCategoriesListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var deleteTarget by rememberSaveable { mutableStateOf<AdminCategory?>(null) }

    Scaffold(
        containerColor = LocaPinSurface,
        topBar = {
            TopAppBar(
                title = { Text("Manage Categories", color = LocaPinPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = LocaPinPrimary)
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = LocaPinSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateCategory,
                containerColor = LocaPinPrimary,
                contentColor = LocaPinWhite
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create category")
            }
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
                label = { Text("Search by category name", color = LocaPinPrimary.copy(alpha = 0.7f)) },
                singleLine = true,
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LocaPinPrimary,
                    unfocusedBorderColor = LocaPinBorder,
                    focusedLabelColor = LocaPinPrimary,
                    cursorColor = LocaPinPrimary,
                    focusedContainerColor = LocaPinFieldBackground,
                    unfocusedContainerColor = LocaPinFieldBackground
                ),
                trailingIcon = {
                    TextButton(onClick = viewModel::populateFromAttractions) {
                        Text("Import", color = LocaPinPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            )

            if (uiState.categories.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = LocaPinCardBackground.copy(alpha = 0.5f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LocaPinBorder)
                ) {
                    Text(
                        text = if (uiState.searchQuery.isBlank()) {
                            "No categories yet. Tap + to add one."
                        } else {
                            "No categories match your search."
                        },
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = LocaPinPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 84.dp)
                ) {
                    items(uiState.categories, key = { it.id }) { category ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = LocaPinCardBackground.copy(alpha = 0.5f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, LocaPinBorder)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = category.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = LocaPinPrimary
                                )
                                if (category.description.isNotBlank()) {
                                    Text(
                                        text = category.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = LocaPinPrimary.copy(alpha = 0.8f),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    IconButton(onClick = { onEditCategory(category.id) }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit ${category.name}", tint = LocaPinPrimary)
                                    }
                                    IconButton(onClick = { deleteTarget = category }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete ${category.name}", tint = LocaPinPrimary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (deleteTarget != null) {
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("Delete category") },
            text = {
                Text("Are you sure you want to delete '${deleteTarget?.name}'? This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        deleteTarget?.let { viewModel.deleteCategory(it.id) }
                        deleteTarget = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
