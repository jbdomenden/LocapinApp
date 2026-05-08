package com.locapin.mobile.feature.map

import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.Region
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.PathParser

/**
 * High-Fidelity Data Structures
 */
data class Pin(
    val id: String,
    val label: String,
    val offset: Offset
)

data class Sector(
    val id: String,
    val name: String,
    val pathData: String,
    val color: Color,
    val centerX: Float,
    val centerY: Float,
    val pins: List<Pin> = emptyList()
)

object SanJuanMapDefaults {
    val Background = Color(0xFFFFFDF5)
    val BorderSelected = Color(0xFF2C2C2C)
    val BorderUnselected = Color(0xFF888888)
    
    // Exact color palette matching standard pastel map design
    val Palette = listOf(
        Color(0xFFF0F4A4), Color(0xFFD7D3E4), Color(0xFFC7E2B0),
        Color(0xFFC9DFEE), Color(0xFFF4D2C1), Color(0xFFE9DCC9),
        Color(0xFFF0E6D2), Color(0xFFE9B7B7)
    )
}

/**
 * Staff-Level implementation of the San Juan Interactive Map
 */
@Composable
fun SanJuanInteractiveMap(
    modifier: Modifier = Modifier
) {
    val sectors = remember { createSanJuanSectors() }
    var selectedSector by remember { mutableStateOf<Sector?>(null) }
    
    // Animation States
    val scale by animateFloatAsState(
        targetValue = if (selectedSector == null) 1f else 2.5f,
        animationSpec = tween(durationMillis = 600)
    )
    
    val panOffset by animateOffsetAsState(
        targetValue = if (selectedSector == null) Offset.Zero else {
            // Calculate pan to center the selected sector
            Offset(selectedSector!!.centerX, selectedSector!!.centerY)
        },
        animationSpec = tween(durationMillis = 600)
    )

    val textMeasurer = rememberTextMeasurer()

    Box(modifier = modifier.fillMaxSize().background(SanJuanMapDefaults.Background)) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val canvasSize = Offset(constraints.maxWidth.toFloat(), constraints.maxHeight.toFloat())
            val mapCenter = Offset(canvasSize.x / 2, canvasSize.y / 2)

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(sectors) {
                        detectTapGestures { tapOffset ->
                            // Hit Testing Logic
                            val hit = findSectorAt(tapOffset, sectors, scale, panOffset, mapCenter)
                            selectedSector = hit
                        }
                    }
            ) {
                // Translation & Scaling Logic
                val currentPan = if (selectedSector == null) Offset.Zero else {
                    Offset(
                        mapCenter.x - (panOffset.x * scale),
                        mapCenter.y - (panOffset.y * scale)
                    )
                }

                drawSectors(sectors, selectedSector, scale, currentPan, textMeasurer)
                
                if (selectedSector != null && scale > 2.0f) {
                    drawPins(selectedSector!!.pins, scale, currentPan)
                }
            }
        }

        // Reset UI Component
        if (selectedSector != null) {
            ExtendedFloatingActionButton(
                onClick = { selectedSector = null },
                modifier = Modifier.align(Alignment.BottomCenter).padding(32.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Show Full Map", fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun DrawScope.drawSectors(
    sectors: List<Sector>,
    selected: Sector?,
    scale: Float,
    offset: Offset,
    textMeasurer: androidx.compose.ui.text.TextMeasurer
) {
    sectors.forEach { sector ->
        val isSelected = sector == selected
        val opacity = if (selected == null || isSelected) 1.0f else 0.3f
        
        // Parse and Scale Path
        val androidPath = PathParser.createPathFromPathData(sector.pathData)
        val matrix = Matrix().apply {
            postScale(scale, scale)
            postTranslate(offset.x, offset.y)
        }
        androidPath.transform(matrix)
        val composePath = androidPath.asComposePath()

        // Draw Fill
        drawPath(
            path = composePath,
            color = sector.color.copy(alpha = opacity)
        )

        // Draw Precise Border
        drawPath(
            path = composePath,
            color = if (isSelected) SanJuanMapDefaults.BorderSelected else SanJuanMapDefaults.BorderUnselected.copy(alpha = opacity),
            style = Stroke(width = if (isSelected) 3f else 1f)
        )
    }
}

private fun DrawScope.drawPins(pins: List<Pin>, scale: Float, offset: Offset) {
    pins.forEach { pin ->
        val pinPos = Offset(pin.offset.x * scale + offset.x, pin.offset.y * scale + offset.y)
        drawCircle(
            color = Color.Red,
            radius = 6f,
            center = pinPos
        )
    }
}

/**
 * Advanced Hit Testing using Android Region and Path
 */
private fun findSectorAt(
    tap: Offset,
    sectors: List<Sector>,
    scale: Float,
    pan: Offset,
    center: Offset
): Sector? {
    val offset = if (scale == 1f) Offset.Zero else {
        Offset(center.x - (pan.x * scale), center.y - (pan.y * scale))
    }

    sectors.forEach { sector ->
        val androidPath = PathParser.createPathFromPathData(sector.pathData)
        val matrix = Matrix().apply {
            postScale(scale, scale)
            postTranslate(offset.x, offset.y)
        }
        androidPath.transform(matrix)
        
        val rectF = RectF()
        androidPath.computeBounds(rectF, true)
        val region = Region()
        region.setPath(androidPath, Region(rectF.left.toInt(), rectF.top.toInt(), rectF.right.toInt(), rectF.bottom.toInt()))
        
        if (region.contains(tap.x.toInt(), tap.y.toInt())) {
            return sector
        }
    }
    return null
}

private fun createSanJuanSectors(): List<Sector> {
    return SanJuanMapData.sectors.map { mapSector ->
        Sector(
            id = mapSector.id,
            name = mapSector.name,
            pathData = mapSector.pathData,
            color = mapSector.fillColor,
            centerX = mapSector.labelPosition.x,
            centerY = mapSector.labelPosition.y,
            pins = SanJuanMapData.attractionsBySector[mapSector.id]?.map { attraction ->
                Pin(attraction.name, attraction.name, Offset(0f, 0f)) // Placeholder pin positions
            } ?: emptyList()
        )
    }
}
