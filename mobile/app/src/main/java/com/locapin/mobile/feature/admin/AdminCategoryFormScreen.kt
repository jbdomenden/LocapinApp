package com.locapin.mobile.feature.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.locapin.mobile.core.designsystem.theme.LocaPinBorder
import com.locapin.mobile.core.designsystem.theme.LocaPinFieldBackground
import com.locapin.mobile.core.designsystem.theme.LocaPinPrimary
import com.locapin.mobile.core.designsystem.theme.LocaPinSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCategoryFormScreen(
    onBack: () -> Unit,
    viewModel: AdminCategoryFormViewModel = hiltViewModel()
) {
    val state = viewModel.uiState
    val isEditMode = state.categoryId != null

    Scaffold(
        containerColor = LocaPinSurface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditMode) "Edit Category" else "Create Category",
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
                value = state.description,
                onValueChange = viewModel::onDescriptionChange,
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                label = { Text("Description") },
                colors = commonTextFieldColors()
            )

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
