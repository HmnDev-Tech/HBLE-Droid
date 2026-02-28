package com.bledroid.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.core.content.edit
import com.bledroid.engine.BleAdvertiserEngine
import com.bledroid.generators.*
import com.bledroid.models.AdvertisementSet
import com.bledroid.models.SpamType
import com.bledroid.service.SpamForegroundService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

enum class ThemeColor {
    DYNAMIC, DEFAULT, BLUE, RED, GREEN, PURPLE
}

class BleDroidViewModel(application: Application) : AndroidViewModel(application) {
    val engine = BleAdvertiserEngine(application)

    // Generators
    val fastPairGen = FastPairGenerator()
    val samsungBudsGen = SamsungBudsGenerator()
    val samsungWatchGen = SamsungWatchGenerator()
    val appleDeviceGen = AppleDevicePopupGenerator()
    val appleActionGen = AppleActionModalGenerator()
    val swiftPairGen = SwiftPairGenerator()
    val lovespouseGen = LovespouseGenerator()

    // All advertisement sets by type
    private val _fastPairSets = MutableStateFlow(fastPairGen.generate().toMutableList())
    val fastPairSets = _fastPairSets.asStateFlow()

    private val _samsungBudsSets = MutableStateFlow(samsungBudsGen.generate().toMutableList())
    val samsungBudsSets = _samsungBudsSets.asStateFlow()

    private val _samsungWatchSets = MutableStateFlow(samsungWatchGen.generate().toMutableList())
    val samsungWatchSets = _samsungWatchSets.asStateFlow()

    private val _appleDeviceSets = MutableStateFlow(appleDeviceGen.generate().toMutableList())
    val appleDeviceSets = _appleDeviceSets.asStateFlow()

    private val _appleActionSets = MutableStateFlow(appleActionGen.generate().toMutableList())
    val appleActionSets = _appleActionSets.asStateFlow()

    private val _swiftPairSets = MutableStateFlow(swiftPairGen.generate().toMutableList())
    val swiftPairSets = _swiftPairSets.asStateFlow()

    private val _lovespouseSets = MutableStateFlow(lovespouseGen.generate().toMutableList())
    val lovespouseSets = _lovespouseSets.asStateFlow()

    // Mixed All — combines all generators, shuffled
    private val _mixAllSets = MutableStateFlow(buildMixAllSets())
    val mixAllSets = _mixAllSets.asStateFlow()

    private fun buildMixAllSets(): MutableList<AdvertisementSet> {
        val all = mutableListOf<AdvertisementSet>()
        all.addAll(fastPairGen.generate())
        all.addAll(samsungBudsGen.generate())
        all.addAll(samsungWatchGen.generate())
        all.addAll(appleDeviceGen.generate())
        all.addAll(appleActionGen.generate())
        all.addAll(swiftPairGen.generate())
        all.addAll(lovespouseGen.generate())
        all.shuffle()
        return all
    }

    fun reshuffleMixAll() {
        _mixAllSets.value = buildMixAllSets()
    }

    // Active spam type
    private val _activeSpamType = MutableStateFlow<SpamType?>(null)
    val activeSpamType = _activeSpamType.asStateFlow()

    private val prefs = application.getSharedPreferences("bledroid_settings", Context.MODE_PRIVATE)

    // Settings
    private val _intervalMs = MutableStateFlow(prefs.getLong("intervalMs", 100L))
    val intervalMs = _intervalMs.asStateFlow()

    private val _txPower = MutableStateFlow(prefs.getInt("txPower", 3)) // HIGH
    val txPower = _txPower.asStateFlow()

    private val _useForegroundService = MutableStateFlow(prefs.getBoolean("useForegroundService", true))
    val useForegroundService = _useForegroundService.asStateFlow()

    private val _themeColor = MutableStateFlow(
        runCatching { ThemeColor.valueOf(prefs.getString("themeColor", "DYNAMIC") ?: "DYNAMIC") }.getOrDefault(ThemeColor.DYNAMIC)
    )
    val themeColor = _themeColor.asStateFlow()

    private val _useOledTheme = MutableStateFlow(prefs.getBoolean("useOledTheme", false))
    val useOledTheme = _useOledTheme.asStateFlow()

    private val _isControlBarExpanded = MutableStateFlow(true)
    val isControlBarExpanded = _isControlBarExpanded.asStateFlow()

    private val _showCustomInterval = MutableStateFlow(prefs.getBoolean("showCustomInterval", false))
    val showCustomInterval = _showCustomInterval.asStateFlow()

    private val _pendingNavRoute = MutableStateFlow<String?>(null)
    val pendingNavRoute = _pendingNavRoute.asStateFlow()

    init {
        engine.intervalMs = _intervalMs.value
        engine.txPower = _txPower.value
    }

    fun setInterval(ms: Long) {
        _intervalMs.value = ms
        engine.intervalMs = ms
        prefs.edit { putLong("intervalMs", ms) }
    }
    
    fun setTxPower(power: Int) {
        _txPower.value = power
        engine.txPower = power
        prefs.edit { putInt("txPower", power) }
    }
    
    fun setUseForegroundService(use: Boolean) {
        _useForegroundService.value = use
        prefs.edit { putBoolean("useForegroundService", use) }
    }
    
    fun setThemeColor(color: ThemeColor) {
        _themeColor.value = color
        prefs.edit { putString("themeColor", color.name) }
    }

    fun setUseOledTheme(use: Boolean) {
        _useOledTheme.value = use
        prefs.edit { putBoolean("useOledTheme", use) }
    }

    fun setControlBarExpanded(expanded: Boolean) {
        _isControlBarExpanded.value = expanded
    }

    fun setShowCustomInterval(show: Boolean) {
        _showCustomInterval.value = show
        prefs.edit { putBoolean("showCustomInterval", show) }
    }

    fun navigateTo(route: String) {
        _pendingNavRoute.value = route
    }

    fun clearPendingNav() {
        _pendingNavRoute.value = null
    }

    fun toggleDeviceSelection(type: SpamType, index: Int) {
        val flow = getFlowForType(type)
        val list = flow.value.toMutableList()
        if (index in list.indices) {
            list[index] = list[index].copy(isSelected = !list[index].isSelected)
            flow.value = list
        }
    }

    fun selectAll(type: SpamType, selected: Boolean) {
        val flow = getFlowForType(type)
        flow.value = flow.value.map { it.copy(isSelected = selected) }.toMutableList()
    }

    fun startSpam(type: SpamType) {
        val sets = getFlowForType(type).value
        engine.start(sets)
        _activeSpamType.value = type

        val route = when (type) {
            SpamType.FAST_PAIR -> com.bledroid.ui.navigation.Routes.FAST_PAIR
            SpamType.APPLE_DEVICE_POPUP, SpamType.APPLE_ACTION_MODAL -> com.bledroid.ui.navigation.Routes.APPLE
            SpamType.SAMSUNG_BUDS, SpamType.SAMSUNG_WATCH -> com.bledroid.ui.navigation.Routes.SAMSUNG
            SpamType.SWIFT_PAIR -> com.bledroid.ui.navigation.Routes.SWIFT_PAIR
            SpamType.LOVESPOUSE_PLAY, SpamType.LOVESPOUSE_STOP -> com.bledroid.ui.navigation.Routes.LOVESPOUSE
            SpamType.MIXED_ALL -> com.bledroid.ui.navigation.Routes.MIX_ALL
        }

        val ctx = getApplication<Application>()
        if (_useForegroundService.value) {
            ctx.startForegroundService(
                Intent(ctx, SpamForegroundService::class.java).apply {
                    putExtra(SpamForegroundService.EXTRA_ROUTE, route)
                }
            )
        }

        // Update Live Notification with packet count every 2 seconds
        viewModelScope.launch {
            while (engine.isRunning.value) {
                kotlinx.coroutines.delay(2000)
                if (!engine.isRunning.value) break
                if (_useForegroundService.value) {
                    val intent = Intent(ctx, SpamForegroundService::class.java).apply {
                        action = SpamForegroundService.ACTION_UPDATE_PROGRESS
                        putExtra(SpamForegroundService.EXTRA_PACKETS, engine.packetsSent.value)
                        putExtra(SpamForegroundService.EXTRA_ROUTE, route)
                    }
                    ctx.startService(intent)
                }
            }
        }
    }

    fun stopSpam() {
        engine.stop()
        _activeSpamType.value = null
        _isControlBarExpanded.value = true  // Restore bar when stopped

        val ctx = getApplication<Application>()
        ctx.stopService(Intent(ctx, SpamForegroundService::class.java))
    }

    // --- Spam Radar ---
    fun startRadar() = engine.startScan()
    fun stopRadar() = engine.stopScan()

    private fun getFlowForType(type: SpamType): MutableStateFlow<MutableList<AdvertisementSet>> {
        return when (type) {
            SpamType.FAST_PAIR -> _fastPairSets
            SpamType.SAMSUNG_BUDS -> _samsungBudsSets
            SpamType.SAMSUNG_WATCH -> _samsungWatchSets
            SpamType.APPLE_DEVICE_POPUP -> _appleDeviceSets
            SpamType.APPLE_ACTION_MODAL -> _appleActionSets
            SpamType.SWIFT_PAIR -> _swiftPairSets
            SpamType.LOVESPOUSE_PLAY, SpamType.LOVESPOUSE_STOP -> _lovespouseSets
            SpamType.MIXED_ALL -> _mixAllSets
        }
    }

    override fun onCleared() {
        engine.destroy()
    }
}
