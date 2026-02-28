package com.bledroid.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.animation.*
import androidx.compose.ui.unit.dp
import com.bledroid.ui.BleDroidViewModel
import com.bledroid.ui.ThemeColor

@Composable
fun SettingsGroupItem(
    title: String,
    subtitle: String? = null,
    isFirst: Boolean,
    isLast: Boolean,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    bottomContent: @Composable (() -> Unit)? = null,
) {
    val cornerRadius = 24.dp
    val connectionRadius = 4.dp

    val shape = when {
        isFirst && isLast -> androidx.compose.foundation.shape.RoundedCornerShape(cornerRadius)
        isFirst -> androidx.compose.foundation.shape.RoundedCornerShape(
            topStart = cornerRadius, topEnd = cornerRadius,
            bottomStart = connectionRadius, bottomEnd = connectionRadius
        )
        isLast -> androidx.compose.foundation.shape.RoundedCornerShape(
            topStart = connectionRadius, topEnd = connectionRadius,
            bottomStart = cornerRadius, bottomEnd = cornerRadius
        )
        else -> androidx.compose.foundation.shape.RoundedCornerShape(connectionRadius)
    }

    Surface(
        shape = shape,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        onClick = { onClick?.invoke() },
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    if (subtitle != null) {
                        Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                if (trailing != null) trailing()
            }
            if (bottomContent != null) {
                Spacer(Modifier.height(12.dp))
                bottomContent()
            }
        }
    }
}

@Composable
fun SettingsGroupHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: BleDroidViewModel,
    onBack: () -> Unit,
) {
    val intervalMs by viewModel.intervalMs.collectAsState()
    val txPower by viewModel.txPower.collectAsState()
    val useForeground by viewModel.useForegroundService.collectAsState()
    val themeColor by viewModel.themeColor.collectAsState()
    val useOled by viewModel.useOledTheme.collectAsState()

    val showCustomInterval by viewModel.showCustomInterval.collectAsState()
    // Do NOT use remember(intervalMs) — that resets the field on every value change!
    var customIntervalText by remember { mutableStateOf(intervalMs.toString()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            
            SettingsGroupHeader("Bluetooth Payload")

            SettingsGroupItem(
                title = "Advertising Interval",
                subtitle = "Time between advertisement packets: ${intervalMs}ms",
                isFirst = true,
                isLast = false,
                trailing = {
                    IconButton(onClick = {
                        if (!showCustomInterval) {
                            // Populate with current value when opening the editor
                            customIntervalText = intervalMs.toString()
                        }
                        viewModel.setShowCustomInterval(!showCustomInterval)
                    }) {
                        Icon(if (showCustomInterval) Icons.Default.Done else Icons.Default.Edit, "Edit")
                    }
                },
                bottomContent = {
                    AnimatedContent(targetState = showCustomInterval, label = "interval_input") { isCustom ->
                        if (isCustom) {
                            OutlinedTextField(
                                value = customIntervalText,
                                onValueChange = {
                                    customIntervalText = it
                                    // Save on every valid keystroke — safe because remember{} (not remember(intervalMs){}) won't reset
                                    val v = it.toLongOrNull()
                                    if (v != null && v in 10..10000) viewModel.setInterval(v)
                                },
                                label = { Text("Custom Interval (ms)") },
                                placeholder = { Text("e.g. 150") },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = androidx.compose.ui.text.input.ImeAction.Done
                                ),
                                keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                                    onDone = {
                                        val v = customIntervalText.toLongOrNull()
                                        if (v != null && v in 10..10000) viewModel.setInterval(v)
                                        viewModel.setShowCustomInterval(false)
                                    }
                                ),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                singleLine = true,
                                shape = MaterialTheme.shapes.medium,
                            )
                        } else {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Slider(
                                    value = intervalMs.toFloat(),
                                    onValueChange = { viewModel.setInterval(it.toLong()) },
                                    valueRange = 20f..1000f,
                                    steps = 19,
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text("20ms (Fast)", style = MaterialTheme.typography.labelSmall)
                                    Text("1000ms (Slow)", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            )

            SettingsGroupItem(
                title = "TX Power Level",
                subtitle = "Higher = further range, more visible",
                isFirst = false,
                isLast = true,
                bottomContent = {
                    val powerLabels = listOf("ULow", "Low", "Medium", "High")
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        powerLabels.forEachIndexed { index, label ->
                            SegmentedButton(
                                selected = txPower == index,
                                onClick = { viewModel.setTxPower(index) },
                                shape = SegmentedButtonDefaults.itemShape(index, powerLabels.size),
                            ) {
                                Text(label, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            )

            SettingsGroupHeader("Personalization")

            SettingsGroupItem(
                title = "Foreground Service",
                subtitle = "Keep spam running when app is in background",
                isFirst = true,
                isLast = false,
                onClick = { viewModel.setUseForegroundService(!useForeground) },
                trailing = {
                    Switch(
                        checked = useForeground,
                        onCheckedChange = { viewModel.setUseForegroundService(it) },
                    )
                }
            )

            SettingsGroupItem(
                title = "OLED Pure Black Theme",
                subtitle = "Uses absolute black instead of dark grey for the background. Only active when device is in Dark Mode.",
                isFirst = false,
                isLast = false,
                onClick = { viewModel.setUseOledTheme(!useOled) },
                trailing = {
                    Switch(
                        checked = useOled,
                        onCheckedChange = { viewModel.setUseOledTheme(it) },
                    )
                }
            )

            SettingsGroupItem(
                title = "Theme Color",
                subtitle = "Customize the app aesthetic",
                isFirst = false,
                isLast = true,
                bottomContent = {
                     Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                         SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                             val options = listOf(ThemeColor.DYNAMIC to "You", ThemeColor.DEFAULT to "Cyan", ThemeColor.BLUE to "Blue")
                             options.forEachIndexed { i, (color, label) ->
                                 SegmentedButton(
                                     selected = themeColor == color,
                                     onClick = { viewModel.setThemeColor(color) },
                                     shape = SegmentedButtonDefaults.itemShape(i, 3),
                                 ) { Text(label, style = MaterialTheme.typography.labelSmall) }
                             }
                         }
                         SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                             val options = listOf(ThemeColor.RED to "Red", ThemeColor.GREEN to "Green", ThemeColor.PURPLE to "Purple")
                             options.forEachIndexed { i, (color, label) ->
                                 SegmentedButton(
                                     selected = themeColor == color,
                                     onClick = { viewModel.setThemeColor(color) },
                                     shape = SegmentedButtonDefaults.itemShape(i, 3),
                                 ) { Text(label, style = MaterialTheme.typography.labelSmall) }
                             }
                         }
                     }
                }
            )

            SettingsGroupHeader("About")

            SettingsGroupItem(
                title = "About BleDroid v1.5.0",
                subtitle = "BLE advertisement spam tool with Material 3 Expressive design. Supports Google Fast Pair, Apple Continuity, Samsung Easy Setup, Windows Swift Pair, Lovespouse protocols. New: Mix All Spam mode & built-in Spam Radar detector.",
                isFirst = true,
                isLast = true,
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}
