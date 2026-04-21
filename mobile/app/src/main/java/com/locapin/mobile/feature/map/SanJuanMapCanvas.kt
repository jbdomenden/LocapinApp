package com.locapin.mobile.feature.map

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun SanJuanMapCanvas(
    sectors: List<MapSector>,
    selectedSectorId: String?,
    onSectorTapped: (MapSector?) -> Unit,
    modifier: Modifier = Modifier,
    scale: Float = 1f,
    offset: Offset = Offset.Zero,
    onTransformChanged: (Float, Offset) -> Unit
) {
    val mapWidth = 1000f
    val mapHeight = 620f

    val state = rememberTransformableState { zoomChange, offsetChange, _ ->
        onTransformChanged(scale * zoomChange, offset + offsetChange)
    }

    Box(
        modifier = modifier
            .transformable(state = state)
            .pointerInput(sectors, scale, offset) {
                detectTapGestures { tapOffset ->
                    val canvasWidth = size.width.toFloat()
                    val canvasHeight = size.height.toFloat()
                    
                    val scaleX = canvasWidth / mapWidth
                    val scaleY = canvasHeight / mapHeight
                    val minScale = minOf(scaleX, scaleY)
                    
                    val startX = (canvasWidth - mapWidth * minScale) / 2
                    val startY = (canvasHeight - mapHeight * minScale) / 2

                    // Adjust tap for scale and offset
                    val adjustedTap = (tapOffset - offset) / scale
                    
                    // Adjust for centering and initial scaling
                    val mapTapX = (adjustedTap.x - startX) / minScale
                    val mapTapY = (adjustedTap.y - startY) / minScale

                    val tappedSector = sectors.find { sector ->
                        isPointInPolygon(Offset(mapTapX, mapTapY), sector.polygonPoints)
                    }
                    onSectorTapped(tappedSector)
                }
            }
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            
            val scaleX = canvasWidth / mapWidth
            val scaleY = canvasHeight / mapHeight
            val minScale = minOf(scaleX, scaleY)

            withTransform({
                translate(offset.x, offset.y)
                scale(scale, scale, pivot = Offset.Zero)
                translate(
                    (canvasWidth - mapWidth * minScale) / 2,
                    (canvasHeight - mapHeight * minScale) / 2
                )
                scale(minScale, minScale, pivot = Offset.Zero)
            }) {
                sectors.forEach { sector ->
                    val path = Path().apply {
                        sector.polygonPoints.forEachIndexed { index, point ->
                            val projected = toLatLng(point)
                            if (index == 0) moveTo(projected.x, projected.y)
                            else lineTo(projected.x, projected.y)
                        }
                        close()
                    }
                    
                    val isSelected = sector.id == selectedSectorId
                    drawPath(
                        path = path,
                        color = if (isSelected) sector.fillColor.copy(alpha = 0.9f) else sector.fillColor.copy(alpha = 0.6f),
                        style = Fill
                    )
                    drawPath(
                        path = path,
                        color = if (isSelected) Color.Black else Color.Black.copy(alpha = 0.3f),
                        style = Stroke(width = if (isSelected) 2f else 1f)
                    )
                }
            }
        }
    }
}

private fun toLatLng(offset: Offset): Offset {
    // Check if the offset is already a GPS coordinate (San Juan: Lat ~14.6, Lng ~121.0)
    // If it's a relative offset (0-1000 range), project it. 
    // If it's a GPS coordinate, we need to map it to the 1000x620 canvas space.
    
    val isGps = offset.y in 14.0..15.0 && offset.x in 120.0..122.0
    if (!isGps) return offset

    // Linear projection from San Juan GPS bounds to our 1000x620 canvas
    val minLat = 14.5900
    val maxLat = 14.6200
    val minLng = 121.0150
    val maxLng = 121.0550

    val x = (offset.x - minLng) / (maxLng - minLng) * 1000f
    val y = (1.0 - (offset.y - minLat) / (maxLat - minLat)) * 620f
    
    return Offset(x.toFloat(), y.toFloat())
}

private fun isPointInPolygon(point: Offset, polygon: List<Offset>): Boolean {
    var isInside = false
    var j = polygon.size - 1
    for (i in polygon.indices) {
        if (polygon[i].y < point.y && polygon[j].y >= point.y || polygon[j].y < point.y && polygon[i].y >= point.y) {
            if (polygon[i].x + (point.y - polygon[i].y) / (polygon[j].y - polygon[i].y) * (polygon[j].x - polygon[i].x) < point.x) {
                isInside = !isInside
            }
        }
        j = i
    }
    return isInside
}
