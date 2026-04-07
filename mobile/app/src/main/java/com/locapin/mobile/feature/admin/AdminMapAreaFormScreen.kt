package com.locapin.mobile.feature.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMapAreaFormScreen(
    onBack: () -> Unit,
    viewModel: AdminMapAreaFormViewModel = hiltViewModel()
) {
    val state = viewModel.uiState
    val isEditMode = state.mapAreaId != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Map Area" else "Create Map Area") },
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
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
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
                value = state.description,
                onValueChange = viewModel::onDescriptionChange,
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                label = { Text("Description") }
            )

            OutlinedTextField(
                value = state.districtLabel,
                onValueChange = viewModel::onDistrictLabelChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("District / Zone") },
                singleLine = true
            )

            OutlinedTextField(
                value = state.centerLatitude,
                onValueChange = viewModel::onCenterLatitudeChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Center Latitude *") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = state.errors.containsKey("centerLatitude"),
                supportingText = { state.errors["centerLatitude"]?.let { Text(it) } }
            )

            OutlinedTextField(
                value = state.centerLongitude,
                onValueChange = viewModel::onCenterLongitudeChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Center Longitude *") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = state.errors.containsKey("centerLongitude"),
                supportingText = { state.errors["centerLongitude"]?.let { Text(it) } }
            )
        }
    }
}
