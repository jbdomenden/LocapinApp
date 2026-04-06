package com.locapin.mobile.feature.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAttractionFormScreen(
    onBack: () -> Unit,
    viewModel: AdminAttractionFormViewModel = hiltViewModel()
) {
    val state = viewModel.uiState
    val isEditMode = state.attractionId != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Attraction" else "Create Attraction") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            if (viewModel.save()) onBack()
                        }
                    ) {
                        Text("Save")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::onNameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Name *") },
                isError = state.errors.containsKey("name"),
                supportingText = { state.errors["name"]?.let { Text(it) } }
            )

            OutlinedTextField(
                value = state.knownFor,
                onValueChange = viewModel::onKnownForChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Known for *") },
                isError = state.errors.containsKey("knownFor"),
                supportingText = { state.errors["knownFor"]?.let { Text(it) } }
            )

            OutlinedTextField(
                value = state.description,
                onValueChange = viewModel::onDescriptionChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Description *") },
                minLines = 3,
                isError = state.errors.containsKey("description"),
                supportingText = { state.errors["description"]?.let { Text(it) } }
            )

            OutlinedTextField(
                value = state.category,
                onValueChange = viewModel::onCategoryChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Category") }
            )

            OutlinedTextField(
                value = state.latitude,
                onValueChange = viewModel::onLatitudeChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Latitude *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = state.errors.containsKey("latitude"),
                supportingText = { state.errors["latitude"]?.let { Text(it) } }
            )

            OutlinedTextField(
                value = state.longitude,
                onValueChange = viewModel::onLongitudeChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Longitude *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = state.errors.containsKey("longitude"),
                supportingText = { state.errors["longitude"]?.let { Text(it) } }
            )

            OutlinedTextField(
                value = state.area,
                onValueChange = viewModel::onAreaChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Area / District") }
            )

            Text(
                text = "Visibility",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FilterChip(
                    selected = state.isVisible,
                    onClick = { viewModel.onVisibilityChange(true) },
                    label = { Text("Visible") }
                )
                FilterChip(
                    selected = !state.isVisible,
                    onClick = { viewModel.onVisibilityChange(false) },
                    label = { Text("Hidden") }
                )
            }
        }
    }
}
