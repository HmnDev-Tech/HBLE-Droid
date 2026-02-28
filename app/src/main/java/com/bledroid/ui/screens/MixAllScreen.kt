package com.bledroid.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bledroid.models.SpamType
import com.bledroid.ui.BleDroidViewModel
import com.bledroid.ui.components.DeviceListWithControls

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MixAllScreen(
    viewModel: BleDroidViewModel,
    onBack: () -> Unit,
) {
    val sets by viewModel.mixAllSets.collectAsState()
    val isRunning by viewModel.engine.isRunning.collectAsState()
    val packetsSentState = viewModel.engine.packetsSent.collectAsState()
    val isControlBarExpanded by viewModel.isControlBarExpanded.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Mix All Spam", fontWeight = FontWeight.Bold)
                        Text(
                            "All types interleaved randomly",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.reshuffleMixAll() }) {
                        Icon(Icons.Default.Shuffle, "Reshuffle")
                    }
                },
            )
        },
    ) { paddingValues ->
        DeviceListWithControls(
            sets = sets,
            isRunning = isRunning,
            packetsSentProvider = { packetsSentState.value },
            onToggle = { viewModel.toggleDeviceSelection(SpamType.MIXED_ALL, it) },
            onStart = { viewModel.startSpam(SpamType.MIXED_ALL) },
            onStop = { viewModel.stopSpam() },
            onSelectAll = { viewModel.selectAll(SpamType.MIXED_ALL, true) },
            onDeselectAll = { viewModel.selectAll(SpamType.MIXED_ALL, false) },
            searchQuery = searchQuery,
            onSearchChange = { searchQuery = it },
            isControlBarExpanded = isControlBarExpanded,
            onExpandChange = { viewModel.setControlBarExpanded(it) },
            modifier = Modifier.padding(paddingValues),
        )
    }
}
