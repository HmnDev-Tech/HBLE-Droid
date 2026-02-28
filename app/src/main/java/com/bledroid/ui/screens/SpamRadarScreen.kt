package com.bledroid.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bledroid.models.SpamRadarEntry
import com.bledroid.ui.BleDroidViewModel
import java.text.SimpleDateFormat
import java.util.*

private fun getSpamTypeColor(type: String): Color = when {
    type.contains("Fast Pair") -> Color(0xFF4CAF50)
    type.contains("Apple") -> Color(0xFFFF6B6B)
    type.contains("Samsung") -> Color(0xFF2196F3)
    type.contains("Swift Pair") -> Color(0xFF00BCD4)
    type.contains("Lovespouse") -> Color(0xFFE91E63)
    type.contains("Unknown") -> Color(0xFF9E9E9E)
    else -> Color(0xFFFF9800)
}

private fun getSpamTypeIcon(type: String) = when {
    type.contains("Fast Pair") -> Icons.Default.Bluetooth
    type.contains("Apple") -> Icons.Default.PhoneIphone
    type.contains("Samsung") -> Icons.Default.Watch
    type.contains("Swift Pair") -> Icons.Default.DesktopWindows
    type.contains("Lovespouse") -> Icons.Default.Favorite
    else -> Icons.Default.DeviceUnknown
}

private fun rssiToSignalLevel(rssi: Int): Int = when {
    rssi >= -50 -> 4  // Excellent
    rssi >= -65 -> 3  // Good
    rssi >= -80 -> 2  // Fair
    rssi >= -90 -> 1  // Weak
    else -> 0          // Very weak
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SpamRadarScreen(
    viewModel: BleDroidViewModel,
    onBack: () -> Unit,
) {
    val isScanning by viewModel.engine.isScanning.collectAsState()
    val scanResults by viewModel.engine.scanResults.collectAsState()

    // Radar pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "radar")
    val radarPulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "radarPulse",
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Spam Radar", fontWeight = FontWeight.Bold)
                        Text(
                            text = if (isScanning) "🔍 Scanning… ${scanResults.size} devices"
                            else "Ready to scan",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isScanning) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    if (isScanning) {
                        IconButton(onClick = { viewModel.stopRadar() }) {
                            Icon(Icons.Default.Stop, "Stop", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            // Radar visual header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (isScanning) {
                    // Pulsing radar circles
                    for (i in 0..2) {
                        val delay = i * 0.33f
                        val progress = ((radarPulse + delay) % 1f)
                        Box(
                            modifier = Modifier
                                .size((60 + progress * 80).dp)
                                .scale(1f)
                                .alpha(1f - progress)
                                .clip(CircleShape)
                                .background(
                                    MaterialTheme.colorScheme.primary.copy(
                                        alpha = 0.15f * (1f - progress)
                                    )
                                ),
                        )
                    }
                }

                // Center button
                Button(
                    onClick = {
                        if (isScanning) viewModel.stopRadar() else viewModel.startRadar()
                    },
                    modifier = Modifier.size(width = 160.dp, height = 56.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isScanning)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.primary
                    ),
                ) {
                    Icon(
                        imageVector = if (isScanning) Icons.Default.Stop else Icons.Default.Sensors,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (isScanning) "STOP" else "START SCAN",
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            // Type legend
            if (scanResults.isNotEmpty()) {
                val typeGroups = scanResults.groupBy { it.detectedType }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    typeGroups.keys.take(5).forEach { type ->
                        val count = typeGroups[type]?.size ?: 0
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = getSpamTypeColor(type).copy(alpha = 0.15f),
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(getSpamTypeColor(type))
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = "$count",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = getSpamTypeColor(type),
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
            }

            // Results list
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items(scanResults, key = { it.deviceAddress }) { entry ->
                    SpamRadarEntryCard(entry)
                }

                if (scanResults.isEmpty() && isScanning) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(48.dp),
                                    strokeWidth = 3.dp,
                                )
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    "Listening for BLE advertisements…",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }

                if (!isScanning && scanResults.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.Sensors,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                )
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    "Tap START SCAN to detect nearby BLE spam",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SpamRadarEntryCard(entry: SpamRadarEntry) {
    val typeColor = getSpamTypeColor(entry.detectedType)
    val signalLevel = rssiToSignalLevel(entry.rssi)
    val timeFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

    val cornerRadius = 16.dp

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(cornerRadius),
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Type icon
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(typeColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = getSpamTypeIcon(entry.detectedType),
                    contentDescription = null,
                    tint = typeColor,
                    modifier = Modifier.size(22.dp),
                )
            }

            Spacer(Modifier.width(12.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = entry.detectedType,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = typeColor,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = timeFormat.format(Date(entry.timestamp)),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    text = entry.deviceAddress,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (entry.manufacturerId != null) {
                    Text(
                        text = "MFR: 0x${"%04X".format(entry.manufacturerId)}",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    )
                }
            }

            // Signal strength
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    for (i in 1..4) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height((6 + i * 4).dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    if (i <= signalLevel)
                                        typeColor
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                                ),
                        )
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "${entry.rssi} dBm",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp,
                )
            }
        }
    }
}
