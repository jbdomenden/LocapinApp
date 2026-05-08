package com.locapin.mobile.feature.map

import android.annotation.SuppressLint
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ZoomOutMap
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.PathParser

/**
 * Material 3 Standard Color Palette extracted from image_3.png
 */
object LocapinColors {
    val Background = Color(0xFFFFFDF5)
    val BorderDark = Color(0xFF2C2C2C)
    val StreetGrid = Color(0x15333333)
}

/**
 * High-fidelity Interactive Map Component using SVG Path Data
 */
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun SanJuanMapDetail(
    modifier: Modifier = Modifier
) {
    val sectors = remember { SanJuanMapData.sectors }
    var selectedSector by remember { mutableStateOf<MapSector?>(null) }
    val textMeasurer = rememberTextMeasurer()

    // Cache parsed paths
    val parsedPaths = remember(sectors) {
        sectors.associate { sector ->
            val androidPath = try {
                PathParser.createPathFromPathData(sector.pathData)
            } catch (e: Exception) {
                android.graphics.Path()
            }
            sector.id to androidPath
        }
    }

    // Calculate global bounds for normalization
    val mapBounds = remember(parsedPaths) {
        val bounds = RectF()
        if (parsedPaths.isNotEmpty()) {
            val totalPath = android.graphics.Path()
            parsedPaths.values.forEach { totalPath.addPath(it) }
            totalPath.computeBounds(bounds, true)
        }
        bounds
    }

    // Animation orchestration
    val scale by animateFloatAsState(
        targetValue = if (selectedSector == null) 1f else 2.8f,
        animationSpec = tween(700)
    )
    val panOffset by animateOffsetAsState(
        targetValue = selectedSector?.labelPosition ?: Offset(mapBounds.centerX(), mapBounds.centerY()),
        animationSpec = tween(700)
    )

    Box(modifier = modifier.fillMaxSize().background(LocapinColors.Background)) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = constraints.maxWidth.toFloat()
            val canvasHeight = constraints.maxHeight.toFloat()
            val mapCenter = Offset(canvasWidth / 2, canvasHeight / 2)

            if (mapBounds.width() <= 0f) return@BoxWithConstraints

            val scaleX = canvasWidth / mapBounds.width()
            val scaleY = canvasHeight / mapBounds.height()
            val baseScale = minOf(scaleX, scaleY) * 0.9f

            Canvas(
                modifier = Modifier.fillMaxSize().pointerInput(scale, panOffset, mapBounds, baseScale) {
                    detectTapGestures { tap ->
                        val translation = if (selectedSector == null) {
                            Offset(
                                (canvasWidth - mapBounds.width() * baseScale) / 2 - mapBounds.left * baseScale,
                                (canvasHeight - mapBounds.height() * baseScale) / 2 - mapBounds.top * baseScale
                            )
                        } else {
                            Offset(
                                mapCenter.x - panOffset.x * scale * baseScale,
                                mapCenter.y - panOffset.y * scale * baseScale
                            )
                        }
                        
                        val hit = sectors.find { sector ->
                            val path = parsedPaths[sector.id] ?: return@find false
                            isPointInSector(tap, path, scale * baseScale, translation)
                        }
                        selectedSector = hit
                    }
                }
            ) {
                val translation = if (selectedSector == null) {
                    Offset(
                        (canvasWidth - mapBounds.width() * baseScale) / 2 - mapBounds.left * baseScale,
                        (canvasHeight - mapBounds.height() * baseScale) / 2 - mapBounds.top * baseScale
                    )
                } else {
                    Offset(
                        mapCenter.x - panOffset.x * scale * baseScale,
                        mapCenter.y - panOffset.y * scale * baseScale
                    )
                }

                // Render each sector
                sectors.forEach { sector ->
                    val isSelected = sector == selectedSector
                    val opacity = if (selectedSector == null || isSelected) 1f else 0.4f
                    
                    val androidPath = parsedPaths[sector.id] ?: return@forEach
                    val matrix = Matrix().apply {
                        postScale(scale * baseScale, scale * baseScale)
                        postTranslate(translation.x, translation.y)
                    }
                    val transformedPath = android.graphics.Path()
                    androidPath.transform(matrix, transformedPath)
                    val composePath = transformedPath.asComposePath()

                    // Fill & Grid
                    drawPath(composePath, sector.fillColor.copy(alpha = opacity))
                    drawStreetGrid(
                        path = composePath,
                        color = LocapinColors.StreetGrid.copy(alpha = opacity),
                        rotation = sector.gridRotation,
                        density = sector.gridDensity
                    )

                    // Border
                    drawPath(
                        path = composePath,
                        color = LocapinColors.BorderDark.copy(alpha = opacity),
                        style = Stroke(width = if (isSelected) 3f else 1.2f)
                    )
                    
                    // Label
                    if (selectedSector == null || isSelected) {
                        drawSectorLabel(sector, translation, scale * baseScale, textMeasurer)
                    }
                }
            }
        }

        if (selectedSector != null) {
            FloatingActionButton(
                onClick = { selectedSector = null },
                modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Icon(Icons.Default.ZoomOutMap, contentDescription = "Reset View")
            }
        }
    }
}

private fun DrawScope.drawSectorLabel(
    sector: MapSector,
    translation: Offset,
    totalScale: Float,
    textMeasurer: androidx.compose.ui.text.TextMeasurer
) {
    val textStyle = TextStyle(
        color = LocapinColors.BorderDark,
        fontSize = (11 * (totalScale / 2f)).coerceIn(8f, 24f).sp,
        fontWeight = FontWeight.Bold
    )
    val result = textMeasurer.measure(sector.name, textStyle)
    drawText(
        textLayoutResult = result,
        topLeft = Offset(
            sector.labelPosition.x * totalScale + translation.x - result.size.width / 2,
            sector.labelPosition.y * totalScale + translation.y - result.size.height / 2
        )
    )
}
