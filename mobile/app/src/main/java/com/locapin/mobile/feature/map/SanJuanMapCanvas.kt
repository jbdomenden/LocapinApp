package com.locapin.mobile.feature.map

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
fun SanJuanMapCanvas(
    sectors: List<MapSector>,
    selectedSectorId: String?,
    scale: Float,
    offset: Offset,
    onTransformChanged: (Float, Offset) -> Unit,
    onSectorTapped: (MapSector?) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(430.dp)
            .background(color = Color(0xFFEFE8D2), shape = RoundedCornerShape(8.dp))
            .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(410.dp)
                .pointerInput(scale, offset) {
                    detectTransformGestures { centroid, pan, zoom, _ ->
                        val newScale = (scale * zoom).coerceIn(1f, 4f)
                        val scaledCentroid = (centroid - offset) / scale
                        val newOffset = centroid - (scaledCentroid * newScale) + pan
                        onTransformChanged(newScale, newOffset)
                    }
                }
                .pointerInput(sectors, scale, offset) {
                    detectTapGestures { tapOffset ->
                        val mapPoint = (tapOffset - offset) / scale
                        val tappedSector = sectors.firstOrNull { sector ->
                            pointInPolygon(mapPoint, sector.polygonPoints)
                        }
                        onSectorTapped(tappedSector)
                    }
                }
        ) {
            val sx = size.width / SanJuanMapData.mapWidth
            val sy = size.height / SanJuanMapData.mapHeight

            withTransform({
                translate(left = offset.x, top = offset.y)
                scale(scale, scale)
                scale(sx, sy)
            }) {
                sectors.forEach { sector ->
                    val path = Path().apply {
                        moveTo(sector.polygonPoints.first().x, sector.polygonPoints.first().y)
                        for (i in 1 until sector.polygonPoints.size) {
                            lineTo(sector.polygonPoints[i].x, sector.polygonPoints[i].y)
                        }
                        close()
                    }
                    val isSelected = sector.id == selectedSectorId
                    drawPath(
                        path = path,
                        color = if (isSelected) sector.fillColor.copy(alpha = 0.95f) else sector.fillColor,
                    )
                    drawPath(
                        path = path,
                        color = if (isSelected) Color.Black else Color(0xFF9D978A),
                        style = Stroke(width = if (isSelected) 3f else 2f)
                    )

                    drawContext.canvas.nativeCanvas.drawText(
                        sector.name.uppercase(),
                        sector.labelPosition.x,
                        sector.labelPosition.y,
                        Paint().apply {
                            color = android.graphics.Color.BLACK
                            textSize = 20f
                            textAlign = Paint.Align.CENTER
                            isFakeBoldText = true
                        }
                    )
                }
            }
        }
    }
}

private fun pointInPolygon(point: Offset, polygon: List<Offset>): Boolean {
    var intersections = 0
    polygon.indices.forEach { i ->
        val a = polygon[i]
        val b = polygon[(i + 1) % polygon.size]
        val intersects = ((a.y > point.y) != (b.y > point.y)) &&
            (point.x < (b.x - a.x) * (point.y - a.y) / ((b.y - a.y).takeIf { it != 0f } ?: 0.0001f) + a.x)
        if (intersects) intersections++
    }
    return intersections % 2 == 1
}

private operator fun Offset.minus(other: Offset): Offset = Offset(x - other.x, y - other.y)
private operator fun Offset.div(value: Float): Offset = Offset(x / value, y / value)
private operator fun Offset.times(value: Float): Offset = Offset(x * value, y * value)
private operator fun Offset.plus(other: Offset): Offset = Offset(x + other.x, y + other.y)
