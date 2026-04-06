package com.locapin.mobile.feature.admin

import androidx.compose.runtime.Composable
import com.locapin.mobile.feature.common.ComingSoonScreen

@Composable
fun AdminModulePlaceholderScreen(
    title: String,
    description: String,
    onBack: () -> Unit
) {
    ComingSoonScreen(
        title = title,
        description = description,
        onBack = onBack
    )
}
