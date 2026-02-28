package com.bledroid.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.bledroid.models.SpamType
import com.bledroid.ui.BleDroidViewModel
import com.bledroid.ui.components.DeviceListWithControls

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FastPairScreen(
    viewModel: BleDroidViewModel,
    onBack: () -> Unit,
) {
    val sets by viewModel.fastPairSets.collectAsState()
    val isRunning by viewModel.engine.isRunning.collectAsState()
    val packetsSentState = viewModel.engine.packetsSent.collectAsState()
    val isControlBarExpanded by viewModel.isControlBarExpanded.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Google Fast Pair") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
            )
        },
    ) { paddingValues ->
        DeviceListWithControls(
            sets = sets,
            isRunning = isRunning,
            packetsSentProvider = { packetsSentState.value },
            onToggle = { viewModel.toggleDeviceSelection(SpamType.FAST_PAIR, it) },
            onStart = { viewModel.startSpam(SpamType.FAST_PAIR) },
            onStop = { viewModel.stopSpam() },
            onSelectAll = { viewModel.selectAll(SpamType.FAST_PAIR, true) },
            onDeselectAll = { viewModel.selectAll(SpamType.FAST_PAIR, false) },
            searchQuery = searchQuery,
            onSearchChange = { searchQuery = it },
            isControlBarExpanded = isControlBarExpanded,
            onExpandChange = { viewModel.setControlBarExpanded(it) },
            modifier = Modifier.padding(paddingValues),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppleScreen(
    viewModel: BleDroidViewModel,
    onBack: () -> Unit,
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val deviceSets by viewModel.appleDeviceSets.collectAsState()
    val actionSets by viewModel.appleActionSets.collectAsState()
    val isRunning by viewModel.engine.isRunning.collectAsState()
    val packetsSentState = viewModel.engine.packetsSent.collectAsState()
    val isControlBarExpanded by viewModel.isControlBarExpanded.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Apple Continuity") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            PrimaryTabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Device Popups") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Action Modals") })
            }

            val currentType = if (selectedTab == 0) SpamType.APPLE_DEVICE_POPUP else SpamType.APPLE_ACTION_MODAL
            val currentSets = if (selectedTab == 0) deviceSets else actionSets

            DeviceListWithControls(
                sets = currentSets,
                isRunning = isRunning,
                packetsSentProvider = { packetsSentState.value },
                onToggle = { viewModel.toggleDeviceSelection(currentType, it) },
                onStart = { viewModel.startSpam(currentType) },
                onStop = { viewModel.stopSpam() },
                onSelectAll = { viewModel.selectAll(currentType, true) },
                onDeselectAll = { viewModel.selectAll(currentType, false) },
                searchQuery = searchQuery,
                onSearchChange = { searchQuery = it },
                isControlBarExpanded = isControlBarExpanded,
                onExpandChange = { viewModel.setControlBarExpanded(it) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SamsungScreen(
    viewModel: BleDroidViewModel,
    onBack: () -> Unit,
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val budsSets by viewModel.samsungBudsSets.collectAsState()
    val watchSets by viewModel.samsungWatchSets.collectAsState()
    val isRunning by viewModel.engine.isRunning.collectAsState()
    val packetsSentState = viewModel.engine.packetsSent.collectAsState()
    val isControlBarExpanded by viewModel.isControlBarExpanded.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Samsung Easy Setup") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            PrimaryTabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Galaxy Buds") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Galaxy Watch") })
            }

            val currentType = if (selectedTab == 0) SpamType.SAMSUNG_BUDS else SpamType.SAMSUNG_WATCH
            val currentSets = if (selectedTab == 0) budsSets else watchSets

            DeviceListWithControls(
                sets = currentSets,
                isRunning = isRunning,
                packetsSentProvider = { packetsSentState.value },
                onToggle = { viewModel.toggleDeviceSelection(currentType, it) },
                onStart = { viewModel.startSpam(currentType) },
                onStop = { viewModel.stopSpam() },
                onSelectAll = { viewModel.selectAll(currentType, true) },
                onDeselectAll = { viewModel.selectAll(currentType, false) },
                searchQuery = searchQuery,
                onSearchChange = { searchQuery = it },
                isControlBarExpanded = isControlBarExpanded,
                onExpandChange = { viewModel.setControlBarExpanded(it) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwiftPairScreen(
    viewModel: BleDroidViewModel,
    onBack: () -> Unit,
) {
    val sets by viewModel.swiftPairSets.collectAsState()
    val isRunning by viewModel.engine.isRunning.collectAsState()
    val packetsSentState = viewModel.engine.packetsSent.collectAsState()
    val isControlBarExpanded by viewModel.isControlBarExpanded.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Windows Swift Pair") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
            )
        },
    ) { paddingValues ->
        DeviceListWithControls(
            sets = sets,
            isRunning = isRunning,
            packetsSentProvider = { packetsSentState.value },
            onToggle = { viewModel.toggleDeviceSelection(SpamType.SWIFT_PAIR, it) },
            onStart = { viewModel.startSpam(SpamType.SWIFT_PAIR) },
            onStop = { viewModel.stopSpam() },
            onSelectAll = { viewModel.selectAll(SpamType.SWIFT_PAIR, true) },
            onDeselectAll = { viewModel.selectAll(SpamType.SWIFT_PAIR, false) },
            searchQuery = searchQuery,
            onSearchChange = { searchQuery = it },
            isControlBarExpanded = isControlBarExpanded,
            onExpandChange = { viewModel.setControlBarExpanded(it) },
            modifier = Modifier.padding(paddingValues),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LovespouseScreen(
    viewModel: BleDroidViewModel,
    onBack: () -> Unit,
) {
    val sets by viewModel.lovespouseSets.collectAsState()
    val isRunning by viewModel.engine.isRunning.collectAsState()
    val packetsSentState = viewModel.engine.packetsSent.collectAsState()
    val isControlBarExpanded by viewModel.isControlBarExpanded.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lovespouse") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
            )
        },
    ) { paddingValues ->
        DeviceListWithControls(
            sets = sets,
            isRunning = isRunning,
            packetsSentProvider = { packetsSentState.value },
            onToggle = { viewModel.toggleDeviceSelection(SpamType.LOVESPOUSE_PLAY, it) },
            onStart = { viewModel.startSpam(SpamType.LOVESPOUSE_PLAY) },
            onStop = { viewModel.stopSpam() },
            onSelectAll = { viewModel.selectAll(SpamType.LOVESPOUSE_PLAY, true) },
            onDeselectAll = { viewModel.selectAll(SpamType.LOVESPOUSE_PLAY, false) },
            searchQuery = searchQuery,
            onSearchChange = { searchQuery = it },
            isControlBarExpanded = isControlBarExpanded,
            onExpandChange = { viewModel.setControlBarExpanded(it) },
            modifier = Modifier.padding(paddingValues),
        )
    }
}
