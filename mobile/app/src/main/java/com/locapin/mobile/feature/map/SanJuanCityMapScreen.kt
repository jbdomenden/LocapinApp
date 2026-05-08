package com.locapin.mobile.feature.map

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.core.graphics.PathParser
import android.graphics.RectF
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Person
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.locapin.mobile.core.designsystem.theme.LocaPinBorder
import com.locapin.mobile.core.designsystem.theme.LocaPinCardBackground
import com.locapin.mobile.core.designsystem.theme.LocaPinDark
import com.locapin.mobile.core.designsystem.theme.LocaPinFieldBackground
import com.locapin.mobile.core.designsystem.theme.LocaPinPrimary
import com.locapin.mobile.core.designsystem.theme.LocaPinSecondary
import com.locapin.mobile.core.designsystem.theme.LocaPinSurface
import com.locapin.mobile.R
import androidx.compose.ui.res.painterResource
import com.locapin.mobile.ui.components.SectorBottomSheet
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SanJuanCityMapScreen(
    onLogout: () -> Unit = {},
    vm: SanJuanMapViewModel = hiltViewModel()
) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    var showSheet by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showNoAdsPrompt by remember { mutableStateOf(false) }
    var showAttractionsModal by remember { mutableStateOf(false) }
    var selectedAttraction by remember { mutableStateOf<Attraction?>(null) }

    if (state.isLoading) {
        SkeletonMapLoading()
    }

    val selectedSector = state.sectors.firstOrNull { it.id == state.selectedSectorId }

    if (state.showInterstitialAd) {
        InterstitialAdDialog(onDismiss = vm::dismissAd)
    }

    if (showNoAdsPrompt) {
        NoAdsPaymentDialog(
            onDismiss = { showNoAdsPrompt = false },
            onConfirmPayment = {
                vm.disableAds()
                showNoAdsPrompt = false
            }
        )
    }

    val attractionsMap by vm.attractions.collectAsStateWithLifecycle()

    if (selectedSector != null) {
        SectorZoomDialog(
            sector = selectedSector,
            attractions = attractionsMap[selectedSector.id].orEmpty(),
            onDismiss = { vm.onSectorTapped(null) },
            onAttractionClick = { selectedAttraction = it }
        )
    }

    if (showAttractionsModal && selectedSector != null) {
        AttractionsGridDialog(
            sectorName = selectedSector.name,
            attractions = attractionsMap[selectedSector.id].orEmpty(),
            onDismiss = { showAttractionsModal = false },
            onAttractionClick = { selectedAttraction = it }
        )
    }

    if (selectedAttraction != null) {
        AttractionDetailsDialog(
            attraction = selectedAttraction!!,
            onDismiss = { selectedAttraction = null }
        )
    }

    state.showPremiumPrompt?.let { sector ->
        PremiumAreaDialog(
            sectorName = sector.name,
            onDismiss = vm::dismissPremiumPrompt,
            onWatchAd = { vm.watchAdForPremium(sector.id) },
            onBuy = { vm.buyPremiumAccess(sector.id) }
        )
    }

    Scaffold(
        containerColor = LocaPinSurface,
        topBar = {
            Surface(
                color = LocaPinCardBackground,
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(bottom = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { showNoAdsPrompt = true }) {
                            Icon(
                                imageVector = Icons.Default.Block,
                                contentDescription = "No Ads",
                                tint = LocaPinPrimary
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "CITY MAP OF",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontFamily = FontFamily.Serif,
                                    letterSpacing = 2.sp
                                ),
                                fontWeight = FontWeight.Bold,
                                color = LocaPinPrimary
                            )
                            Text(
                                text = "SAN JUAN",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontFamily = FontFamily.Serif,
                                    letterSpacing = 1.sp
                                ),
                                fontWeight = FontWeight.ExtraBold,
                                color = LocaPinPrimary
                            )
                        }

                        IconButton(onClick = {
                            vm.logout()
                            onLogout()
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Logout",
                                tint = LocaPinPrimary
                            )
                        }
                    }
                    
                    Surface(
                        color = LocaPinPrimary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = selectedSector?.name ?: "Tap a sector to explore attractions",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = LocaPinPrimary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        },
        bottomBar = {
            if (!state.isAdsDisabled) {
                Surface(
                    color = LocaPinSurface,
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    BannerAd()
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Map Section with a container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.1f)
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(LocaPinFieldBackground)
                    .border(BorderStroke(3.dp, LocaPinBorder), RoundedCornerShape(28.dp))
                    .padding(4.dp)
            ) {
                SanJuanMapCanvas(
                    sectors = state.sectors,
                    selectedSectorId = state.selectedSectorId,
                    attractions = attractionsMap[state.selectedSectorId].orEmpty(),
                    allAttractions = attractionsMap,
                    onSectorTapped = { sector ->
                        vm.onSectorTapped(sector?.id)
                    },
                    onAttractionTapped = { attraction ->
                        selectedAttraction = attraction
                    },
                    modifier = Modifier.fillMaxSize(),
                    scale = state.currentScale,
                    offset = state.currentOffset,
                    showLabels = state.showLabels,
                    onTransformChanged = vm::onTransformChanged
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Map Legend Section
            MapLegend(
                sectors = state.sectors,
                selectedSectorId = state.selectedSectorId,
                isVisible = state.isLegendVisible,
                showLabels = state.showLabels,
                onToggleVisibility = vm::toggleLegend,
                onToggleLabels = vm::toggleLabels,
                onSectorClick = { sectorId: String ->
                    vm.onSectorTapped(if (sectorId == state.selectedSectorId) null else sectorId)
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // City Detail Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = LocaPinCardBackground.copy(alpha = 0.9f)),
                border = BorderStroke(2.dp, LocaPinBorder)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Close, // Placeholder for a city icon if available
                        contentDescription = null,
                        tint = LocaPinPrimary,
                        modifier = Modifier.size(32.dp).alpha(0.3f)
                    )
                    Text(
                        text = "About San Juan City",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = LocaPinPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "The Historical Heart of Metro Manila. Home to the Pinaglabanan Shrine and the bustling Greenhills Center. Discover the rich heritage and modern vibrancy of the Philippines' smallest city.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LocaPinPrimary,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }

        if (showSheet && selectedSector != null) {
            val attractions = attractionsMap[selectedSector.id].orEmpty()
            ModalBottomSheet(
                onDismissRequest = { 
                    showSheet = false
                    vm.onSectorTapped(null)
                },
                sheetState = bottomSheetState,
                containerColor = LocaPinSurface,
                dragHandle = { BottomSheetDefaults.DragHandle(color = LocaPinPrimary) }
            ) {
                SectorBottomSheet(
                    sectorName = selectedSector.name,
                    attractionsCount = selectedSector.attractionsCount,
                    attractions = attractions,
                    onViewAttractions = { 
                        showSheet = false
                        showAttractionsModal = true
                    }
                )
            }
        }
    }
}


@Composable
fun AttractionsGridDialog(
    sectorName: String,
    attractions: List<Attraction>,
    onDismiss: () -> Unit,
    onAttractionClick: (Attraction) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = LocaPinSurface),
            border = BorderStroke(2.dp, LocaPinBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Attractions in $sectorName",
                        style = MaterialTheme.typography.titleLarge,
                        color = LocaPinPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = LocaPinPrimary)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(attractions) { attraction ->
                        AttractionCard(attraction = attraction, onClick = { onAttractionClick(attraction) })
                    }
                }
            }
        }
    }
}

@Composable
fun AttractionCard(attraction: Attraction, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = LocaPinFieldBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            AsyncImage(
                model = attraction.imageUrl,
                contentDescription = attraction.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = attraction.name,
                    style = MaterialTheme.typography.labelMedium,
                    color = LocaPinPrimary,
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )
                Text(
                    text = "Dist: ${attraction.distance}",
                    style = MaterialTheme.typography.labelSmall,
                    color = LocaPinSecondary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun AttractionDetailsDialog(attraction: Attraction, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(attraction.name, color = LocaPinPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AsyncImage(
                    model = attraction.imageUrl,
                    contentDescription = attraction.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(18.dp)
                        )
                        androidx.compose.foundation.layout.Spacer(Modifier.size(4.dp))
                        Text(
                            text = "%.1f".format(attraction.rating),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = LocaPinPrimary
                        )
                    }
                    Text(
                        text = "(${attraction.reviews} reviews)",
                        style = MaterialTheme.typography.bodySmall,
                        color = LocaPinPrimary.copy(alpha = 0.6f)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = "From STI Sta. Mesa:",
                        style = MaterialTheme.typography.labelMedium,
                        color = LocaPinPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = attraction.distance,
                        style = MaterialTheme.typography.labelMedium,
                        color = LocaPinSecondary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = attraction.knownFor,
                    style = MaterialTheme.typography.bodyLarge,
                    color = LocaPinPrimary,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = attraction.description ?: "Discover the beauty and history of ${attraction.name} in the heart of San Juan. This location offers a unique experience for tourists and locals alike.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LocaPinDark
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = LocaPinPrimary, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = LocaPinSurface
    )
}

@Composable
fun BannerAd() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        shape = MaterialTheme.shapes.extraSmall,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        AsyncImage(
            model = "https://placehold.jp/600x100.png?text=Visit+San+Juan+Tourism",
            contentDescription = "Banner Ad",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun InterstitialAdDialog(onDismiss: () -> Unit) {
    var timeLeft by remember { mutableIntStateOf(5) }
    
    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000L)
            timeLeft--
        }
    }

    Dialog(
        onDismissRequest = { if (timeLeft == 0) onDismiss() },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .aspectRatio(0.8f),
            shape = MaterialTheme.shapes.medium
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = "https://placehold.jp/800x1000.png?text=LocaPin+Premium+Experience",
                    contentDescription = "Interstitial Ad",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(40.dp),
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.6f),
                    onClick = { if (timeLeft == 0) onDismiss() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (timeLeft > 0) {
                            Text(
                                text = timeLeft.toString(),
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.4f))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Advertisement",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun NoAdsPaymentDialog(onDismiss: () -> Unit, onConfirmPayment: () -> Unit) {
    var showMockGooglePlay by remember { mutableStateOf(false) }

    if (showMockGooglePlay) {
        MockGooglePlayDialog(
            price = "₱149.00",
            onDismiss = { showMockGooglePlay = false },
            onPay = {
                onConfirmPayment()
                showMockGooglePlay = false
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Go Ad-Free!", color = LocaPinPrimary, fontWeight = FontWeight.Bold) },
        text = { Text("Disable all advertisements for a one-time fee of ₱149.00. Enjoy a cleaner exploration experience in San Juan.", color = LocaPinDark) },
        confirmButton = {
            Button(
                onClick = { showMockGooglePlay = true },
                colors = ButtonDefaults.buttonColors(containerColor = LocaPinSecondary)
            ) {
                Text("Pay Now", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Later", color = LocaPinPrimary)
            }
        },
        containerColor = LocaPinSurface
    )
}

@Composable
fun MockGooglePlayDialog(price: String, onDismiss: () -> Unit, onPay: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    AsyncImage(
                        model = "https://upload.wikimedia.org/wikipedia/commons/d/d0/Google_Play_Arrow_logo.svg",
                        contentDescription = "Google Play",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Google Play", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    "LocaPin: Ad-Free Version",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    price,
                    style = MaterialTheme.typography.displaySmall,
                    color = Color(0xFF01875f),
                    fontWeight = FontWeight.Black
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = onPay,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF01875f)),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Payment, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("1-TAP BUY", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
                
                TextButton(onClick = onDismiss, modifier = Modifier.padding(top = 8.dp)) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun SectorZoomDialog(
    sector: MapSector,
    attractions: List<Attraction>,
    onDismiss: () -> Unit,
    onAttractionClick: (Attraction) -> Unit
) {
    val pinPainter = painterResource(id = R.drawable.pin_location)
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { onDismiss() }
                },
            contentAlignment = Alignment.Center
        ) {
            val parsedPath = remember(sector.pathData) {
                try {
                    PathParser.createPathFromPathData(sector.pathData)
                } catch (e: Exception) {
                    android.graphics.Path()
                }
            }
            
            val bounds = remember(parsedPath) {
                val b = RectF()
                parsedPath.computeBounds(b, true)
                b
            }

            var svgBoundsInRoot by remember { mutableStateOf(RectF()) }
            var currentScale by remember { mutableStateOf(1f) }
            var currentTx by remember { mutableStateOf(0f) }
            var currentTy by remember { mutableStateOf(0f) }

            Box(
                modifier = Modifier
                    .fillMaxSize(0.9f)
                    .pointerInput(attractions, currentScale, currentTx, currentTy) {
                        detectTapGestures { tap ->
                            // Check pins first
                            val tappedAttraction = attractions.find { attraction ->
                                // FIX: Coordinates in SVG space are (longitude, latitude) -> (X, Y)
                                val originalX = attraction.longitude.toFloat()
                                val originalY = attraction.latitude.toFloat()

                                // Use same snap-to-center logic
                                var pinX = originalX
                                var pinY = originalY
                                
                                val region = android.graphics.Region()
                                val rectF = android.graphics.RectF()
                                parsedPath.computeBounds(rectF, true)
                                region.setPath(parsedPath, android.graphics.Region(rectF.left.toInt(), rectF.top.toInt(), rectF.right.toInt(), rectF.bottom.toInt()))
                                
                                if (!region.contains(pinX.toInt(), pinY.toInt())) {
                                    val centerX = bounds.centerX()
                                    val centerY = bounds.centerY()
                                    pinX = centerX
                                    pinY = centerY
                                }

                                val px = pinX * currentScale + currentTx
                                val py = pinY * currentScale + currentTy
                                val dist = Offset(tap.x - px, tap.y - py).getDistance()
                                dist < 80f // Increased hit radius for pins in dialog
                            }
                            
                            if (tappedAttraction != null) {
                                onAttractionClick(tappedAttraction)
                            } else if (!svgBoundsInRoot.contains(tap.x, tap.y)) {
                                onDismiss()
                            }
                        }
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    if (bounds.width() > 0) {
                        val padding = 40f
                        val scaleX = (size.width - padding * 2) / bounds.width()
                        val scaleY = (size.height - padding * 2) / bounds.height()
                        val scale = minOf(scaleX, scaleY)
                        
                        val tx = (size.width - bounds.width() * scale) / 2 - bounds.left * scale
                        val ty = (size.height - bounds.height() * scale) / 2 - bounds.top * scale
                        
                        currentScale = scale
                        currentTx = tx
                        currentTy = ty

                        // Update bounds for hit testing
                        svgBoundsInRoot = RectF(
                            tx + bounds.left * scale,
                            ty + bounds.top * scale,
                            tx + bounds.right * scale,
                            ty + bounds.bottom * scale
                        )

                        withTransform({
                            translate(tx, ty)
                            scale(scale, scale, pivot = Offset.Zero)
                        }) {
                            val composePath = parsedPath.asComposePath()
                            drawPath(
                                path = composePath,
                                color = sector.fillColor,
                                style = Fill
                            )
                            drawStreetGrid(
                                path = composePath,
                                color = Color.Black.copy(alpha = 0.05f),
                                rotation = sector.gridRotation,
                                density = sector.gridDensity
                            )
                            drawPath(
                                path = composePath,
                                color = Color.Black.copy(alpha = 0.5f),
                                style = Stroke(width = 2f / scale)
                            )
                        }

                        // Draw Pins on the same coordinate system
                        attractions.forEach { attraction ->
                            // FIX: Coordinates in SVG space are (longitude, latitude) -> (X, Y)
                            val originalX = attraction.longitude.toFloat()
                            val originalY = attraction.latitude.toFloat()

                            // Use same snap-to-center logic
                            var pinX = originalX
                            var pinY = originalY
                            
                            val region = android.graphics.Region()
                            val rectF = android.graphics.RectF()
                            parsedPath.computeBounds(rectF, true)
                            region.setPath(parsedPath, android.graphics.Region(rectF.left.toInt(), rectF.top.toInt(), rectF.right.toInt(), rectF.bottom.toInt()))
                            
                            if (!region.contains(pinX.toInt(), pinY.toInt())) {
                                val centerX = bounds.centerX()
                                val centerY = bounds.centerY()
                                pinX = centerX
                                pinY = centerY
                            }

                            val px = pinX * scale + tx
                            val py = pinY * scale + ty
                            
                            withTransform({
                                translate(px, py)
                                // Increase pin size for visibility (was 3f scale, 24f size)
                                scale(4f, 4f, pivot = Offset.Zero)
                                translate(-12f, -24f) // Center of pin
                            }) {
                                with(pinPainter) {
                                    draw(size = androidx.compose.ui.geometry.Size(24f, 24f))
                                }
                            }
                        }
                    }
                }
                
                // Overlay Barangay Name
                Text(
                    text = sector.name,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    color = LocaPinPrimary.copy(alpha = 0.2f),
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 48.dp)
                )
            }
        }
    }
}

@Composable
fun MapLegend(
    sectors: List<MapSector>,
    selectedSectorId: String?,
    isVisible: Boolean,
    showLabels: Boolean,
    onToggleVisibility: () -> Unit,
    onToggleLabels: () -> Unit,
    onSectorClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = LocaPinCardBackground.copy(alpha = 0.7f)),
        border = BorderStroke(1.dp, LocaPinBorder.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggleVisibility),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Map Legend",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = LocaPinPrimary
                )
                Icon(
                    imageVector = if (isVisible) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isVisible) "Hide Legend" else "Show Legend",
                    tint = LocaPinPrimary
                )
            }
            
            AnimatedVisibility(
                visible = isVisible,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggleLabels() }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = showLabels,
                            onCheckedChange = { onToggleLabels() },
                            colors = CheckboxDefaults.colors(checkedColor = LocaPinPrimary)
                        )
                        Text(
                            "Show Barangay Labels",
                            style = MaterialTheme.typography.bodyMedium,
                            color = LocaPinDark
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // FlowRow would be better but keeping it simple with nested Rows or a custom layout
                    // Since we have many sectors, let's use a simple wrapping grid-like approach
                    val chunkedSectors = sectors.chunked(2)
                    chunkedSectors.forEach { rowSectors ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowSectors.forEach { sector ->
                                LegendItem(
                                    sector = sector,
                                    isSelected = sector.id == selectedSectorId,
                                    onClick = { onSectorClick(sector.id) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (rowSectors.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun PremiumAreaDialog(
    sectorName: String,
    onDismiss: () -> Unit,
    onWatchAd: () -> Unit,
    onBuy: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFD700))
                Spacer(Modifier.width(8.dp))
                Text("Premium Area", color = LocaPinPrimary, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Text(
                "Access to $sectorName is restricted to premium users. Would you like to buy permanent access or watch a short ad for 5 minutes of temporary access?",
                color = LocaPinDark
            )
        },
        confirmButton = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onBuy,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = LocaPinSecondary)
                ) {
                    Text("Buy Permanent Access (₱199.00)", color = Color.White, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onWatchAd,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = LocaPinPrimary)
                ) {
                    Text("Watch Ad for 5m Access", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Maybe Later", color = LocaPinPrimary)
            }
        },
        containerColor = LocaPinSurface
    )
}

@Composable
fun SkeletonMapLoading() {
    val transition = rememberInfiniteTransition(label = "skeleton")
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LocaPinSurface)
            .statusBarsPadding()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Header Skeleton
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(40.dp)
                    .alpha(alpha)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Gray.copy(alpha = 0.2f))
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(24.dp)
                    .alpha(alpha)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Gray.copy(alpha = 0.2f))
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Map Area Skeleton
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .alpha(alpha)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.Gray.copy(alpha = 0.1f))
                    .border(2.dp, LocaPinBorder.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Legend Skeleton
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .alpha(alpha)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Gray.copy(alpha = 0.1f))
            )
        }
    }
}

@Composable
fun LegendItem(
    sector: MapSector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) LocaPinPrimary.copy(alpha = 0.1f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(sector.fillColor)
                .border(1.dp, if (isSelected) Color.Black else Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
        )
        Text(
            text = sector.name,
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) LocaPinPrimary else LocaPinDark,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1
        )
    }
}
