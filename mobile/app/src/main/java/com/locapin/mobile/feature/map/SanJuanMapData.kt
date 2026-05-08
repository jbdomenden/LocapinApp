package com.locapin.mobile.feature.map

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

object SanJuanMapData {
    const val MAP_WIDTH = 1000f
    const val MAP_HEIGHT = 620f

    val attractionsBySector: Map<String, List<Attraction>> = emptyMap()

    val sectors: List<MapSector> = listOf(
        sector("salapan", "Salapan", 140f, 260f, color = 0xFFF0F4A4, 
            pathData = "m73 137.58l2.25-3.5 2.75-3.08 3.25-2.09 2.67-0.58 3.08-0.33 5-2 4.33-1 8.09-2.26 21.25-7.58 11.33-4.16 0.42-2 0.16-1.42c-0.33-2-0.1-3.58 0.42-3.58l1-2 2-1-1-2-2-1-2-1h-2-4l-2 1h-3c0 0-0.8-0.18-3 0-2.2 0.18-4 0-4 0h-3-3-2l-2-2-1-2-1-4v-2l-1-2-2-2-3-1h-3l-4-1h-2-2l-2 3-1 3-1 3v3 5 3l-1 2-8 10-8 12-2 3v3l2 2 2 2z",
            gridRotation = 15f, gridDensity = 25f),
        sector("ermitaño", "Ermitaño", 340f, 220f, color = 0xFFF4D2C1, 
            pathData = "m156 129l4.08-2 7.92-8.26 8.42-3.66 6.58-0.25 8 0.17-11-8-4-3-4-4-1-8-6-2h-3-2-3-3l-2 1-1 3v2l-2 4-2 1h-3l-2-1h-2l-1 2-1 2-1 4v2l1 3h2l3 1 2 2 2 2 1 3 3 3 3 3z",
            gridRotation = 10f, gridDensity = 20f),
        sector("balong-bato", "Balong-Bato", 180f, 380f, color = 0xFFD7D3E4, 
            pathData = "m77 153v-3l1-3v-2l-1-2-1-2-2-2-2-1 2-1 2-4 2-1 2-2 2-1 4-1 2-1 5-1 4-1 3-1 3-1 4-1 4-2 5-1 4-2 4-1 3-2 4-1 3-1 2-1 2 1 2 1 2 1 2 1 3 3 1 3 3 3 2 1 2 3 2 1-3 2-8 7-8 5-7 3-4 3-5 3-3 2.74-4 1.26-5.33 2.49-8.75 3.51-6.92 2-4 3-7-7z",
            gridRotation = 25f, gridDensity = 25f),
        sector("rivera", "Rivera", 160f, 320f, color = 0xFFC7E2B0, 
            pathData = "m90 167c0.37 0.09-5.05 7.51-10 14.24-4.21 5.73-8.01 10.96-8.5 10.84l-3.5-2.08-2-3-1-2v-2l-2-1-2-2-2-1-2-2 1-2 2-4 2-3 2-2 2-3 2-2 2-2 2-2 2-1 1-2 2-1 2 2 1 2 3 3 4 4z",
            gridRotation = 35f, gridDensity = 22f),
        sector("progreso", "Progreso", 120f, 380f, color = 0xFFE9DCC9, 
            pathData = "m71 193l-16 22-5-2-1-3-2-3v-2-5-4l1-3 2-3 1-3 1-3 2-3 2-2 1-2 4 2 2 3 3 2v3l2 3z",
            gridRotation = 45f, gridDensity = 22f),
        sector("san-perfecto", "San Perfecto", 180f, 440f, color = 0xFFF0F4A4, 
            pathData = "m58 212l59-16-3-2-3-1-2-1h-2l-2-2-4-1h-4l-3-2-14-6z",
            gridRotation = 50f, gridDensity = 18f),
        sector("pedro-cruz", "Pedro Cruz", 250f, 380f, color = 0xFFF0E6D2, 
            pathData = "m117 197l31-8-1.25-2.84-0.33-3.17 0.08-1.5-0.5-1.83-1-0.66-2-2-2-2-4-3v-2l-1-4v-4-3l-1-3-1-2-3-2-4-4-8 7-27 10-12 16 17 8h6z",
            gridRotation = 20f, gridDensity = 24f),
        sector("corazon-de-jesus", "Corazon de Jesus", 420f, 400f, color = 0xFFF0F4A4, 
            pathData = "m230 180l5-6 4-5h-8l-9 3-3 1h-3l-3-2-2-2-2-2-1-2-2-2-2-1h-3-3-2l-2-1-3-1-1-2-3-1-2-2-2-2-3-3-3-5-3-5-3-3-2-3-3-2-3-2-1-2-2-1-8 6-4 4-5 4-15 7 2 3 5 5 0.8 4.98 0.2 5.02 0.4 2.78 0.2 2.8 2.4 1.6 2 1.82 3 3 2.6 2.58-0.6 3.42 2 6 6 7h6l6-5 4.8-0.62 4.2 0.62 3-2 7-3 5-4 5-5 2-2 14 2 9 2z",
            gridRotation = 10f, gridDensity = 22f),
        sector("pasadena", "Pasadena", 460f, 340f, color = 0xFFC9DFEE, 
            pathData = "m245 163l-7 6-7-1-10 4-4 1-3-1-9-10h-7l-3-1-2-1-3-2-3-1-3-3-2-1-2-3-2-3-2-3-2-3-2-3-1-2-11-9 3-3 2-2 3-3 3-1 3-2h2l3-1h3 4 3l2 1 11 13h5 3 2l6 9 3 1h4l3-1h3l4 2v3 3 3l2 2 1 3v4l2 2 3 2z",
            gridRotation = 30f, gridDensity = 26f),
        sector("batis", "Batis", 220f, 540f, color = 0xFFC9DFEE, 
            pathData = "m114 280l-5-8-3-9-8-10-6-12-7-6-30-19 3-3 29-9 29-7 9 6 8 8 5 11 2 9 2 8 3 5 4 5-7 8-8 9z",
            gridRotation = 40f, gridDensity = 24f),
        sector("tibagan", "Tibagan", 340f, 520f, color = 0xFFF0E6D2, 
            pathData = "m117 197l31-8 5 8h5l2-1 3-1 1-2 2-1v3l-1 2-1 4 1 2 1 2v3l-3 2h-2l-2 3 3 2v4l1 2v3h3 2l1 2 1 3v2 4l1 2 2 3 1 2 1 2-4 2-3 3h-3l-3 3h-4-2l-2-1-3-1-2-1-2-2-3-3-2-5-1-3-1-4-1-3v-4l-2-3-1-3-1-3-2-4-2-3-1-2-2-2-2-1-2-2-2-1z",
            gridRotation = 15f, gridDensity = 25f),
        sector("kabayanan", "Kabayanan", 380f, 620f, color = 0xFFF0E6D2, 
            pathData = "m113 280l4 10 9-12h4l13 9 4 1 2-3 2 1 2 2 1 2 2 1 2-1 3-3-3-3v-2l11-4 10-8 8-6v-8l5-10v-6l-6-1-6-0.02-5 3.02v3h-2l-3 2-1 2h-4l-2 1-1 2h-3-2-3l-2-1-3-1-4 4-9 10-11 9z",
            gridRotation = 5f, gridDensity = 28f),
        sector("maytunas", "Maytunas", 440f, 680f, color = 0xFFD7D3E4, 
            pathData = "m183 307l-1-2h-2l-1-2-2-3h-2-3-2l-3-2-3-2h-2l-2-1c-0.4-1.82-1-3-1-3l-2-2 2-2 2-2-2-1-1-2 3-2 2-1 3-1 2-1 3 2 3 2 2 1h2 3 2 2l2 1c1.8-0.22 3 0 3 0h2 3 3l2-2 3-1 2-2c0 0 2.4-1.58 4-2 1.6-0.42 3 0 3 0h4 2 2 4l3-3 3-1 3-3 4-1 2-1 3-1 3-2 1 3 1 4v4 3 2l-1 2-2 2-1 2v2l-1 2-1 3-2 3-3 1-2 2-2 2-3 1-3 1-4 1-4-1h-4-3-3-2-2l-4-1h-3l-3 2h-3l-3 2h-3z",
            gridRotation = 25f, gridDensity = 26f),
        sector("santa-lucia", "Santa Lucia", 520f, 540f, color = 0xFFF0F4A4, 
            pathData = "m169 278l18-14v-9l5-9-1-7 3-3 1-2 2-1 0.58-2.34 0.34-2.25-1.92-1.41-3-1-2-3v-3l1-2h3l1-2h2 2l1-2 1.92-1.51 0.75-1.49 1.83-2.42 2.25-0.34 0.33 0.42 2.5-1 3.75-1.83 2.67 1.17 3 1 0.83 2.24 1.92 4.17 0.5 1.59 1 2.33c0 0 0.08 0.33 1.5 2.66 0.56 0.92 1.18 3.71 3.65 7.99 2.69 4.67 5.05 10.27 6.47 12.68 3.37 5.7 6.13 10.34 6.13 10.34l1 2 1 3v2l1 4v2l-2 2-3 1-2 1-3 1h-3l-2 2-2 1-2 1-2 2-2 1h-2-3-4-2-2l-4 1-1 2-3 2-3 1-3 1h-4-4l-2-1h-2-4-4l-2-1-3-2z",
            gridRotation = 45f, gridDensity = 24f),
        sector("isabelita", "Isabelita", 175f, 210f, color = 0xFFC7E2B0, 
            pathData = "m175 191h-2-2-3l-3 1v2 2l-2 2v2l2 3v3l1 2-3 1c0 0-6 2.42-3 3 3 0.58 5 0 5 0h2c1.6 0.18 3 0 3 0l2 1h2 2l-1-3-1-2 2-1 2-1 3-1-2-4-2-4v-3z",
            gridRotation = 0f, gridDensity = 15f),
        sector("halo-halo", "Halo-Halo", 195f, 205f, color = 0xFFD7D3E4, 
            pathData = "m187.58 184.83l-3.58 2.17-2.42 1.16-2.08 1c0-0.15-1.5-0.23-1.5-0.16l-1.5 1.49 0.08 2.17 0.42 1.34v3l1 4 1 2 1 3h-2l-2 2v2h6l7-1 2-2 3-2c2-0.42 4-1 4-1l2-2-2-2-2-3-1-2-2-4-2-2z",
            gridRotation = 10f, gridDensity = 12f),
        sector("onse", "Onse", 215f, 195f, color = 0xFFC9DFEE, 
            pathData = "m206 209l-2-1-3-3.59-2-1.41-5 2.08-3.08 2.08-2.92 2.5-2-0.08-4 0.25-5 0.17-2 4-5.42-2.09-5.16 0.75c0-0.35-4.75-0.46-4.75-0.42 0 0.12 1.58 2.76 1.33 2.76l1 2 0.75 4.24c-0.47 2.65 2.08 3.71 2.25 2.76l1.75 0.33 1.67 1 0.58 1.67 1 3-0.33 2.08 0.83 3.33 0.5 1.59 2 3 1 2 6-3h4l5 1 3-1.09 1-0.91 1-3 3-2 1-2v-3l-3-1-2-1-2-2v-4l1-1 2-1 3-2h2l3.42-2.92z",
            gridRotation = 20f, gridDensity = 25f),
        sector("little-baguio", "Little Baguio", 260f, 230f, color = 0xFFE9B7B7, 
            pathData = "m243 165l-7 6-6 9-33-4-9 9c0 0 6 10.62 8 13 2 2.38 8 10 8 10l5 1 3-2 2-1 3 1 3 1 8 19 8 13 5 10 2 2 3 11 6-2 9-1 3-4 2-5-1-3v-4l2-1 3-1 4 1 4 1h3l2-2 1-2v-3l4-2 1-2 1-3-1-2-1-2-1-3-2-1 1-4v-2l-1-2-2-2-2-1-3-1-3-2-2-2-3-3-3-2h-2-8l1-4-2-4-2-1-2-1h-3-2l-2-1v-3-2z",
            gridRotation = 18f, gridDensity = 30f),
        sector("greenhills", "Greenhills", 430f, 150f, color = 0xFFC7E2B0, 
            pathData = "m377 192l5-3 3 2 18-10 12-5 37-16 25 55-28 10-22-1-18-3-13-2-7 5-5 5-17 14-15 14-19 18-13 11-13 13-6 1-4 2-4-8c0 0-0.6-2.18 1-5 1.6-2.82-4-5-4-5l1-4-4-3c0 0 0.4-7.78-6-2-6.4 5.78-8.6 2.22-10 1-1.4-1.22-11-12-11-12v-3l1-3 3-4 1-3v-5l1-3 4-1 4 2h7l3-5v-2l3-2 2-4-1-5-2-2-2-3 2-5-3-4-7-3-3-4-6-4h-2-3-4l-1-4-1-3-3-2-2-1h-3l-2-1-2-4v-4-3-2-3l-1-6 1-5 16-14 17-12 24-9 11 7 18 4c0 0 2.8 25.02 7 23 4.2-2.02 17-11 17-11l3-5 2-1z",
            gridRotation = 0f, gridDensity = 35f),
        sector("west-crame", "West Crame", 360f, 130f, color = 0xFFD7D3E4, 
            pathData = "m330.67 139l5.33-2 5-1 8-3h4l5-2 14-3 6-1 19 50 4.08 5-16 8.66-3-1.83-5.16 3.41-18.17-47.75-2.75 3.51v1l-10.5 7.83-8.83 5.17c0 0-2.59-0.52-4.75-10.51-0.44-2.01-0.96-4.56-1.17-7.49-0.11-1.57 0.16-3.25-0.08-5z",
            gridRotation = 10f, gridDensity = 15f),
        sector("addition-hills", "Addition Hills", 280f, 320f, color = 0xFFF0E6D2,
            pathData = "m296 302v3l-13 4 3 10h-39l-11 3-8 5-17 8-9-11-5 1-9-10h-4l-4-1v-3l2-4 3.67 0.08 3.33-1.08 4-1.09 2-0.91 4-1h3l3 1h2 3 2 3 3 2l3.42 0.74 3.58-0.74 2-1 3-1 2-2 3-2 2-2c1.58-0.29 2.81-3.15 2-3l1-2 1.5-3.67 1.83-4.34 1.67-1.99v-4-3-2-2l-1-2-1-2v-2l3-1.84 3.83-1 1.34-0.42 2.33-0.5 2 0.34 2.5 0.42v2l2 4 3 3 2.25 2.33 1.75 1.67 1 1 1 1 2 2 2.17 0.33 2.58-1 2.25-1.33 4-3h2l1 2v2l1.25 0.83 2.08 1.83 0.09 3.92 2.58 2.42 1 2v2l-1.25 2.49 0.67 2.34 0.58 1.17 0.5 1.49 2 3.17z",
            gridRotation = 20f, gridDensity = 25f),
    )

    private fun sector(
        id: String,
        name: String,
        labelX: Float,
        labelY: Float,
        color: Long,
        pathData: String = "",
        gridRotation: Float = 45f,
        gridDensity: Float = 30f
    ): MapSector {
        val attractionCount = attractionsBySector[id]?.size ?: 0
        return MapSector(
            id = id,
            name = name,
            fillColor = Color(color),
            labelPosition = Offset(labelX, labelY),
            pathData = pathData,
            attractionsCount = attractionCount,
            gridRotation = gridRotation,
            gridDensity = gridDensity
        )
    }
}
