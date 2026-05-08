package com.locapin.mobile.feature.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.locapin.mobile.core.designsystem.theme.LocaPinBorder
import com.locapin.mobile.core.designsystem.theme.LocaPinFieldBackground
import com.locapin.mobile.core.designsystem.theme.LocaPinPrimary
import com.locapin.mobile.core.designsystem.theme.LocaPinSecondary
import com.locapin.mobile.core.designsystem.theme.LocaPinSurface
import com.locapin.mobile.core.designsystem.theme.LocaPinWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMapAreaFormScreen(
    onBack: () -> Unit,
    viewModel: AdminMapAreaFormViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState

    Scaffold(
        containerColor = LocaPinSurface,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (uiState.areaId == null) "Add Map Area" else "Edit Map Area",
                        color = LocaPinPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
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
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChange,
                label = { Text("Area Name") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.errors.containsKey("name"),
                supportingText = { uiState.errors["name"]?.let { Text(it) } }
            )

            OutlinedTextField(
                value = uiState.districtLabel,
                onValueChange = viewModel::onDistrictLabelChange,
                label = { Text("District Label (e.g., District 1)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = uiState.centerLatitude,
                    onValueChange = viewModel::onCenterLatitudeChange,
                    label = { Text("Center Lat") },
                    modifier = Modifier.weight(1f),
                    isError = uiState.errors.containsKey("centerLatitude")
                )
                OutlinedTextField(
                    value = uiState.centerLongitude,
                    onValueChange = viewModel::onCenterLongitudeChange,
                    label = { Text("Center Lng") },
                    modifier = Modifier.weight(1f),
                    isError = uiState.errors.containsKey("centerLongitude")
                )
            }

            OutlinedTextField(
                value = uiState.polygonPoints,
                onValueChange = viewModel::onPolygonPointsChange,
                label = { Text("Polygon Points (x1,y1;x2,y2...)") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.errors.containsKey("polygonPoints"),
                supportingText = { uiState.errors["polygonPoints"]?.let { Text(it) } }
            )

            OutlinedTextField(
                value = uiState.pathData,
                onValueChange = viewModel::onPathDataChange,
                label = { Text("SVG Path Data") },
                modifier = Modifier.fillMaxWidth(),
                supportingText = { Text("Optional: Use instead of polygon points if complex") }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = uiState.gridRotation,
                    onValueChange = viewModel::onGridRotationChange,
                    label = { Text("Grid Rotation") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = uiState.gridDensity,
                    onValueChange = viewModel::onGridDensityChange,
                    label = { Text("Grid Density") },
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = uiState.hexColor,
                onValueChange = viewModel::onHexColorChange,
                label = { Text("Hex Color (e.g., #F0F4A4)") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Premium Area", color = LocaPinPrimary, fontWeight = FontWeight.Bold)
                Switch(
                    checked = uiState.isPremium,
                    onCheckedChange = viewModel::onIsPremiumChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = LocaPinPrimary,
                        checkedTrackColor = LocaPinPrimary.copy(alpha = 0.5f)
                    )
                )
            }

            Button(
                onClick = {
                    if (viewModel.save()) {
                        onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LocaPinPrimary)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                androidx.compose.foundation.layout.Spacer(Modifier.padding(4.dp))
                Text("Save Map Area", fontWeight = FontWeight.Bold)
            }
        }
    }
}
