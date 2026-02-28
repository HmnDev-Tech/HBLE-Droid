package com.bledroid.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bledroid.ui.BleDroidViewModel
import com.bledroid.ui.components.SpamCategoryCard
import com.bledroid.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DashboardScreen(
    viewModel: BleDroidViewModel,
    onNavigateToFastPair: () -> Unit,
    onNavigateToApple: () -> Unit,
    onNavigateToSamsung: () -> Unit,
    onNavigateToSwiftPair: () -> Unit,
    onNavigateToLovespouse: () -> Unit,
    onNavigateToMixAll: () -> Unit,
    onNavigateToSpamRadar: () -> Unit,
    onNavigateToSettings: () -> Unit,
) {
    val isRunning by viewModel.engine.isRunning.collectAsState()
    val packetsSent by viewModel.engine.packetsSent.collectAsState()
    val activeType by viewModel.activeSpamType.collectAsState()
    
    val fastPairSets by viewModel.fastPairSets.collectAsState()
    val appleDeviceSets by viewModel.appleDeviceSets.collectAsState()
    val appleActionSets by viewModel.appleActionSets.collectAsState()
    val samsungBudsSets by viewModel.samsungBudsSets.collectAsState()
    val samsungWatchSets by viewModel.samsungWatchSets.collectAsState()
    val swiftPairSets by viewModel.swiftPairSets.collectAsState()
    val lovespouseSets by viewModel.lovespouseSets.collectAsState()
    val mixAllSets by viewModel.mixAllSets.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
        title = {
                    Column {
                        Text(
                            "BleDroid",
                            style = MaterialTheme.typography.headlineMediumEmphasized,
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = if (isRunning) "📡 Spamming Active" else "Ready to spam",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isRunning) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Live stats card
            if (isRunning) {
                val infiniteTransition = rememberInfiniteTransition(label = "glow")
                val glowAlpha by infiniteTransition.animateFloat(
                    initialValue = 0.3f, targetValue = 0.8f,
                    animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse),
                    label = "glow"
                )

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.extraLarge),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = glowAlpha),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$packetsSent",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            Text("Packets Sent", style = MaterialTheme.typography.labelMedium)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = activeType?.label ?: "—",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary,
                            )
                            Text("Active Mode", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }

                FilledTonalButton(
                    onClick = { viewModel.stopSpam() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    shape = MaterialTheme.shapes.large,
                ) {
                    Icon(Icons.Default.Stop, null, Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("STOP ALL SPAM", fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(4.dp))
            }

            // Category cards
            Text(
                text = "Attack Vectors",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp),
            )

            SpamCategoryCard(
                title = "Mix All Spam",
                subtitle = "All types interleaved randomly — maximum chaos",
                icon = Icons.Default.Shuffle,
                deviceCount = mixAllSets.size,
                isActive = activeType == com.bledroid.models.SpamType.MIXED_ALL,
                onClick = onNavigateToMixAll,
            )

            SpamCategoryCard(
                title = "Google Fast Pair",
                subtitle = "Android devices — headphones, speakers, phones",
                icon = Icons.Default.Bluetooth,
                deviceCount = fastPairSets.size,
                isActive = activeType == com.bledroid.models.SpamType.FAST_PAIR,
                onClick = onNavigateToFastPair,
            )

            SpamCategoryCard(
                title = "Apple Continuity",
                subtitle = "iOS popups — AirPods, Beats, Vision Pro",
                icon = Icons.Default.PhoneIphone,
                deviceCount = appleDeviceSets.size + appleActionSets.size,
                isActive = activeType == com.bledroid.models.SpamType.APPLE_DEVICE_POPUP || activeType == com.bledroid.models.SpamType.APPLE_ACTION_MODAL,
                onClick = onNavigateToApple,
            )

            SpamCategoryCard(
                title = "Samsung Easy Setup",
                subtitle = "Galaxy Buds & Watch popups",
                icon = Icons.Default.Watch,
                deviceCount = samsungBudsSets.size + samsungWatchSets.size,
                isActive = activeType == com.bledroid.models.SpamType.SAMSUNG_BUDS || activeType == com.bledroid.models.SpamType.SAMSUNG_WATCH,
                onClick = onNavigateToSamsung,
            )

            SpamCategoryCard(
                title = "Windows Swift Pair",
                subtitle = "Windows 10/11 Bluetooth popups",
                icon = Icons.Default.DesktopWindows,
                deviceCount = swiftPairSets.size,
                isActive = activeType == com.bledroid.models.SpamType.SWIFT_PAIR,
                onClick = onNavigateToSwiftPair,
            )

            SpamCategoryCard(
                title = "Lovespouse",
                subtitle = "IoT toy control — play & stop modes",
                icon = Icons.Default.Favorite,
                deviceCount = lovespouseSets.size,
                isActive = activeType == com.bledroid.models.SpamType.LOVESPOUSE_PLAY,
                onClick = onNavigateToLovespouse,
            )

            // Tools section
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Tools",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp),
            )

            SpamCategoryCard(
                title = "Spam Radar",
                subtitle = "Detect & classify incoming BLE spam nearby",
                icon = Icons.Default.Sensors,
                deviceCount = 0,
                isActive = false,
                onClick = onNavigateToSpamRadar,
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}
