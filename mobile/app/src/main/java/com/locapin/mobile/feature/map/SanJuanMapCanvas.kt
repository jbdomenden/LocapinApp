package com.locapin.mobile.feature.map

import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.Region
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import com.locapin.mobile.R
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import androidx.core.graphics.PathParser

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn

@Composable
fun SanJuanMapCanvas(
    sectors: List<MapSector>,
    selectedSectorId: String?,
    attractions: List<Attraction>,
    allAttractions: Map<String, List<Attraction>>,
    onSectorTapped: (MapSector?) -> Unit,
    onAttractionTapped: (Attraction) -> Unit,
    modifier: Modifier = Modifier,
    scale: Float = 1f,
    offset: Offset = Offset.Zero,
    showLabels: Boolean = true,
    onTransformChanged: (Float, Offset) -> Unit
) {
    val textMeasurer = rememberTextMeasurer()
    val pinPainter = painterResource(id = R.drawable.pin_location)

    // 1. Parse and cache paths
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

    // 2. Calculate sector centers for labels
    val sectorCenters = remember(parsedPaths) {
        parsedPaths.mapValues { (_, path) ->
            val bounds = RectF()
            path.computeBounds(bounds, true)
            Offset(bounds.centerX(), bounds.centerY())
        }
    }

    // 3. Calculate global bounds of all paths combined to auto-center
    val mapBounds = remember(parsedPaths) {
        val bounds = RectF()
        if (parsedPaths.isNotEmpty()) {
            val totalPath = android.graphics.Path()
            parsedPaths.values.forEach { totalPath.addPath(it) }
            totalPath.computeBounds(bounds, true)
        } else {
            bounds.set(0f, 0f, 1000f, 620f)
        }
        bounds
    }

    // 4. Pre-calculate regions for logical containment check (1000x620 space)
    val sectorRegions = remember(parsedPaths) {
        parsedPaths.mapValues { (_, path) ->
            val rectF = RectF()
            path.computeBounds(rectF, true)
            val region = Region()
            region.setPath(path, Region(rectF.left.toInt(), rectF.top.toInt(), rectF.right.toInt(), rectF.bottom.toInt()))
            region
        }
    }

    val state = rememberTransformableState { zoomChange, offsetChange, _ ->
        onTransformChanged(scale * zoomChange, offset + offsetChange)
    }

    Box(
        modifier = modifier
            .transformable(state = state)
            .pointerInput(sectors, scale, offset, mapBounds, allAttractions, selectedSectorId) {
                detectTapGestures { tapOffset ->
                    val canvasWidth = size.width.toFloat()
                    val canvasHeight = size.height.toFloat()
                    
                    val scaleX = canvasWidth / mapBounds.width()
                    val scaleY = canvasHeight / mapBounds.height()
                    val baseScale = minOf(scaleX, scaleY) * 0.9f
                    
                    val totalScale = scale * baseScale
                    val translationX = (canvasWidth / 2f - mapBounds.centerX() * totalScale) + offset.x
                    val translationY = (canvasHeight / 2f - mapBounds.centerY() * totalScale) + offset.y

                    // Check for Attraction Pin taps - ALL pins should be clickable
                    val allPinsList = allAttractions.values.flatten()
                    
                    // Prioritize selected pins for hit testing if overlapping
                    val sortedPins = allPinsList.sortedByDescending { attractions.any { a -> a.name == it.name } }
                    
                    val tappedAttraction = sortedPins.find { attraction ->
                        val entry = allAttractions.entries.find { it.value.contains(attraction) }
                        val zoneId = entry?.key
                        
                        // We always snap to center to ensure pins are visible in the correct sector
                        // as we are currently using real GPS coordinates which don't map directly to SVG space
                        val center = if (zoneId != null) sectorCenters[zoneId] else null
                        val (pinX, pinY) = if (center != null) {
                            val indexInZone = allAttractions[zoneId]?.indexOf(attraction) ?: 0
                            Pair(
                                center.x + (indexInZone % 3 - 1) * 12f,
                                center.y + (indexInZone / 3 - 1) * 12f
                            )
                        } else {
                            // FIX: Longitude is X, Latitude is Y
                            Pair(attraction.longitude.toFloat(), attraction.latitude.toFloat())
                        }

                        val pinScreenPos = Offset(
                            pinX * totalScale + translationX,
                            pinY * totalScale + translationY
                        )
                        val dist = (tapOffset - pinScreenPos).getDistance()
                        
                        // Determine hit radius: selected pins are 1.5x larger (24f * 1.5 = 36f)
                        val isSelected = attractions.any { it.name == attraction.name }
                        val hitRadius = if (isSelected) 60f else 48f
                        dist < hitRadius
                    }

                    if (tappedAttraction != null) {
                        // Check if this attraction belongs to the selected sector
                        val entry = allAttractions.entries.find { it.value.contains(tappedAttraction) }
                        val zoneId = entry?.key
                        val isAttractionInSelectedSector = selectedSectorId != null && zoneId == selectedSectorId

                        if (isAttractionInSelectedSector) {
                            // Already in the right sector, just open details
                            onAttractionTapped(tappedAttraction)
                        } else if (selectedSectorId == null) {
                            // No sector selected, pins should be click-disabled (but can select sector)
                            val tappedSector = sectors.find { it.id == zoneId }
                            onSectorTapped(tappedSector)
                        } else {
                            // Tapping a pin in another sector when one is already selected
                            // Re-evaluate if you want to switch sectors or do nothing.
                            // Requirement: "pins should be click disabled when no sector is selected"
                            // Requirement: "all pins should always show on the whole map"
                            // If we follow the "disabled" requirement strictly, we switch sectors.
                            val tappedSector = sectors.find { it.id == zoneId }
                            onSectorTapped(tappedSector)
                        }
                    } else {
                        val tappedSector = sectors.find { sector ->
                            val path = parsedPaths[sector.id] ?: return@find false
                            isPointInSector(tapOffset, path, totalScale, Offset(translationX, translationY))
                        }
                        onSectorTapped(tappedSector)
                    }
                }
            }
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            
            if (mapBounds.width() <= 0f) return@Canvas

            val scaleX = canvasWidth / mapBounds.width()
            val scaleY = canvasHeight / mapBounds.height()
            val baseScale = minOf(scaleX, scaleY) * 0.9f

            val totalScale = scale * baseScale
            
            // Calculate translation to center the mapBounds in the canvas
            val translationOffset = Offset(
                offset.x + (canvasWidth / 2f - mapBounds.centerX() * totalScale),
                offset.y + (canvasHeight / 2f - mapBounds.centerY() * totalScale)
            )

            withTransform({
                translate(translationOffset.x, translationOffset.y)
                scale(totalScale, totalScale, pivot = Offset.Zero)
            }) {
                // Draw Paths
                sectors.forEach { sector ->
                    val path = parsedPaths[sector.id]?.asComposePath() ?: return@forEach
                    val isSelected = sector.id == selectedSectorId
                    
                    drawPath(
                        path = path,
                        color = sector.fillColor,
                        style = Fill
                    )
                    if (isSelected) {
                        drawPath(
                            path = path,
                            color = Color.Black.copy(alpha = 0.5f),
                            style = Stroke(width = 2f / totalScale)
                        )
                    }
                }
            }
            
            // Draw Labels
            if (showLabels) {
                sectors.forEach { sector ->
                    val center = sectorCenters[sector.id] ?: return@forEach
                    val isSelected = sector.id == selectedSectorId
                    
                    val screenPos = Offset(
                        center.x * totalScale + translationOffset.x,
                        center.y * totalScale + translationOffset.y
                    )
                    
                    // Only draw if within canvas
                    if (screenPos.x in 0f..canvasWidth && screenPos.y in 0f..canvasHeight) {
                        // Draw Label
                        val textStyle = TextStyle(
                            color = if (isSelected) Color.Black else Color.DarkGray,
                            fontSize = (10f * (if (isSelected) 1.2f else 1f)).sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                        val textResult = textMeasurer.measure(sector.name, textStyle)
                        
                        drawText(
                            textLayoutResult = textResult,
                            topLeft = screenPos - Offset(textResult.size.width / 2f, textResult.size.height / 2f + 12f)
                        )
                    }
                }
            }

            // Draw Attraction Pins for ALL sectors
            val selectedPinNames = attractions.map { it.name }.toSet()
            val allPinsList = allAttractions.values.flatten()
            val (selectedPins, backgroundPins) = allPinsList.partition { selectedPinNames.contains(it.name) }

            fun DrawScope.drawPin(attraction: Attraction, scale: Float, alpha: Float) {
                // 1. Identify which sector this attraction belongs to
                val entry = allAttractions.entries.find { it.value.contains(attraction) }
                val zoneId = entry?.key
                
                // 2. Logic: Snap to sector center to ensure visibility within correct area
                val center = if (zoneId != null) sectorCenters[zoneId] else null
                val (pinX, pinY) = if (center != null) {
                    val indexInZone = allAttractions[zoneId]?.indexOf(attraction) ?: 0
                    Pair(
                        center.x + (indexInZone % 3 - 1) * 12f,
                        center.y + (indexInZone / 3 - 1) * 12f
                    )
                } else {
                    // FIX: Longitude is X, Latitude is Y
                    Pair(attraction.longitude.toFloat(), attraction.latitude.toFloat())
                }

                // 3. Screen position calculation
                val pinScreenPos = Offset(
                    pinX * totalScale + translationOffset.x,
                    pinY * totalScale + translationOffset.y
                )

                if (pinScreenPos.x in -50f..size.width + 50f && pinScreenPos.y in -50f..size.height + 50f) {
                    withTransform({
                        translate(pinScreenPos.x, pinScreenPos.y)
                        scale(scale, scale, pivot = Offset.Zero)
                        translate(-12f, -24f) // Center the pin
                    }) {
                        with(pinPainter) {
                            draw(
                                size = androidx.compose.ui.geometry.Size(24f, 24f),
                                alpha = alpha
                            )
                        }
                    }
                }
            }

            // Draw Background pins first, then Selected ones on top
            backgroundPins.forEach { drawPin(it, 1.5f, 0.6f) }
            selectedPins.forEach { drawPin(it, 2.2f, 1.0f) }
        }
    }
}

internal fun isPointInSector(tap: Offset, androidPath: android.graphics.Path, scale: Float, offset: Offset): Boolean {
    val matrix = Matrix().apply {
        postScale(scale, scale)
        postTranslate(offset.x, offset.y)
    }
    val transformedPath = android.graphics.Path()
    androidPath.transform(matrix, transformedPath)
    
    val rectF = RectF()
    transformedPath.computeBounds(rectF, true)
    val region = Region()
    region.setPath(transformedPath, Region(rectF.left.toInt(), rectF.top.toInt(), rectF.right.toInt(), rectF.bottom.toInt()))
    
    return region.contains(tap.x.toInt(), tap.y.toInt())
}

fun DrawScope.drawStreetGrid(
    path: Path, 
    color: Color, 
    rotation: Float = 45f,
    density: Float = 30f
) {
    clipPath(path) {
        val step = density
        val angleRad = Math.toRadians(rotation.toDouble())
        val cos = Math.cos(angleRad).toFloat()
        val sin = Math.sin(angleRad).toFloat()
        
        // Vertical-ish lines
        for (i in -200..200) {
            val dist = i * step
            drawLine(
                color,
                Offset(dist * cos - 5000 * sin, dist * sin + 5000 * cos),
                Offset(dist * cos + 5000 * sin, dist * sin - 5000 * cos),
                0.7f
            )
        }
        
        // Horizontal-ish lines
        val angleRad2 = angleRad + Math.PI / 2
        val cos2 = Math.cos(angleRad2).toFloat()
        val sin2 = Math.sin(angleRad2).toFloat()
        for (i in -200..200) {
            val dist = i * step
            drawLine(
                color,
                Offset(dist * cos2 - 5000 * sin2, dist * sin2 + 5000 * cos2),
                Offset(dist * cos2 + 5000 * sin2, dist * sin2 - 5000 * cos2),
                0.7f
            )
        }
    }
}
