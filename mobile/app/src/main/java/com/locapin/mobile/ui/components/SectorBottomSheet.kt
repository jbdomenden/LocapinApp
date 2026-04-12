package com.locapin.mobile.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.locapin.mobile.core.designsystem.theme.LocaPinPrimary
import com.locapin.mobile.core.designsystem.theme.LocaPinSecondary
import com.locapin.mobile.feature.map.Attraction

@Composable
fun SectorBottomSheet(
    sectorName: String,
    attractionsCount: Int,
    attractions: List<Attraction>,
    onViewAttractions: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = sectorName, 
            style = MaterialTheme.typography.headlineSmall,
            color = LocaPinPrimary,
            fontWeight = FontWeight.ExtraBold
        )
        
        Text(
            text = if (attractionsCount == 1) "1 attraction available" else "$attractionsCount attractions available",
            style = MaterialTheme.typography.bodyLarge,
            color = LocaPinPrimary.copy(alpha = 0.7f),
            fontWeight = FontWeight.Medium
        )

        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (attractions.isEmpty()) {
                Text(
                    text = "Attractions for this sector will appear here soon.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            } else {
                attractions.take(3).forEach { attraction ->
                    Text(
                        text = "• ${attraction.name}", 
                        style = MaterialTheme.typography.bodyMedium,
                        color = LocaPinPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                if (attractionsCount > 3) {
                    Text(
                        text = "and ${attractionsCount - 3} more...",
                        style = MaterialTheme.typography.bodySmall,
                        color = LocaPinPrimary.copy(alpha = 0.5f),
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onViewAttractions, 
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = LocaPinSecondary),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                "EXPLORE ATTRACTIONS", 
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.25.sp,
                color = Color.White
            )
        }
    }
}
