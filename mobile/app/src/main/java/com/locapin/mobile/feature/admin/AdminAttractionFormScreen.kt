package com.locapin.mobile.feature.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.locapin.mobile.core.designsystem.theme.LocaPinBorder
import com.locapin.mobile.core.designsystem.theme.LocaPinFieldBackground
import com.locapin.mobile.core.designsystem.theme.LocaPinPrimary
import com.locapin.mobile.core.designsystem.theme.LocaPinSurface
import com.locapin.mobile.core.designsystem.theme.LocaPinWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAttractionFormScreen(
    onBack: () -> Unit,
    viewModel: AdminAttractionFormViewModel = hiltViewModel()
) {
    val state = viewModel.uiState
    val isEditMode = state.attractionId != null

    Scaffold(
        containerColor = LocaPinSurface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditMode) "Edit Attraction" else "Create Attraction",
                        color = LocaPinPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = LocaPinPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LocaPinSurface
                ),
                actions = {
                    TextButton(
                        onClick = {
                            if (viewModel.save()) onBack()
                        }
                    ) {
                        Text("Save", color = LocaPinPrimary, fontWeight = FontWeight.Bold)
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            state.errors["form"]?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }

            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::onNameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Name *") },
                isError = state.errors.containsKey("name"),
                supportingText = { state.errors["name"]?.let { Text(it) } },
                colors = commonTextFieldColors()
            )

            OutlinedTextField(
                value = state.knownFor,
                onValueChange = viewModel::onKnownForChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Known for *") },
                isError = state.errors.containsKey("knownFor"),
                supportingText = { state.errors["knownFor"]?.let { Text(it) } },
                colors = commonTextFieldColors()
            )

            OutlinedTextField(
                value = state.description,
                onValueChange = viewModel::onDescriptionChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Description *") },
                minLines = 3,
                isError = state.errors.containsKey("description"),
                supportingText = { state.errors["description"]?.let { Text(it) } },
                colors = commonTextFieldColors()
            )

            val categories by viewModel.categories.collectAsStateWithLifecycle()
            var expanded by remember { mutableStateOf(false) }

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = state.category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category *") },
                    trailingIcon = { 
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(
                                imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = LocaPinPrimary
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isError = state.errors.containsKey("category"),
                    supportingText = { state.errors["category"]?.let { Text(it) } },
                    colors = commonTextFieldColors()
                )

                // Invisible overlay to trigger the menu when clicking the text field
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { expanded = true }
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .background(LocaPinSurface)
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name, color = LocaPinPrimary, fontWeight = FontWeight.Bold) },
                            onClick = {
                                viewModel.onCategoryChange(category.name)
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = state.latitude,
                onValueChange = viewModel::onLatitudeChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Latitude *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = state.errors.containsKey("latitude"),
                supportingText = { state.errors["latitude"]?.let { Text(it) } },
                colors = commonTextFieldColors()
            )

            OutlinedTextField(
                value = state.longitude,
                onValueChange = viewModel::onLongitudeChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Longitude *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = state.errors.containsKey("longitude"),
                supportingText = { state.errors["longitude"]?.let { Text(it) } },
                colors = commonTextFieldColors()
            )

            OutlinedTextField(
                value = state.area,
                onValueChange = viewModel::onAreaChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Area / District") },
                colors = commonTextFieldColors()
            )

            OutlinedTextField(
                value = state.imageUrl,
                onValueChange = viewModel::onImageUrlChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Image URL") },
                placeholder = { Text("https://example.com/image.jpg") },
                supportingText = { Text("Direct link to a high-quality image of the attraction.") },
                colors = commonTextFieldColors()
            )

            Text(
                text = "Visibility",
                style = MaterialTheme.typography.labelLarge,
                color = LocaPinPrimary,
                fontWeight = FontWeight.Bold
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FilterChip(
                    selected = state.isVisible,
                    onClick = { viewModel.onVisibilityChange(true) },
                    label = { Text("Visible") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = LocaPinPrimary,
                        selectedLabelColor = LocaPinWhite,
                        labelColor = LocaPinPrimary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = LocaPinBorder,
                        selectedBorderColor = LocaPinPrimary,
                        enabled = true,
                        selected = state.isVisible
                    )
                )
                FilterChip(
                    selected = !state.isVisible,
                    onClick = { viewModel.onVisibilityChange(false) },
                    label = { Text("Hidden") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = LocaPinPrimary,
                        selectedLabelColor = LocaPinWhite,
                        labelColor = LocaPinPrimary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = LocaPinBorder,
                        selectedBorderColor = LocaPinPrimary,
                        enabled = true,
                        selected = !state.isVisible
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun commonTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = LocaPinPrimary,
    unfocusedBorderColor = LocaPinBorder,
    focusedLabelColor = LocaPinPrimary,
    cursorColor = LocaPinPrimary,
    focusedContainerColor = LocaPinFieldBackground,
    unfocusedContainerColor = LocaPinFieldBackground
)
