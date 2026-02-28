package com.bledroid

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import android.annotation.SuppressLint
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.bledroid.ui.BleDroidViewModel
import com.bledroid.ui.navigation.Routes
import com.bledroid.ui.screens.*
import com.bledroid.ui.theme.BleDroidTheme

// Bottom nav destinations
data class NavDestination(
    val route: String,
    val icon: ImageVector,
    val label: String,
)

val bottomNavItems = listOf(
    NavDestination(Routes.DASHBOARD, Icons.Default.Home, "Home"),
    NavDestination("custom_ble", Icons.Default.WifiTethering, "Custom"),
    NavDestination(Routes.SETTINGS, Icons.Default.Settings, "Settings"),
)

class MainActivity : ComponentActivity() {
    private val viewModel: BleDroidViewModel by viewModels()

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { /* permissions handled reactively */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Unlock High Refresh Rate (120Hz/144Hz)
        val window = window
        window.attributes = window.attributes.apply {
            // Try to find the display mode with the highest refresh rate
            val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                display
            } else {
                @Suppress("DEPRECATION")
                windowManager.defaultDisplay
            }
            
            val modes = display?.supportedModes
            val bestMode = modes?.maxByOrNull { it.refreshRate }
            if (bestMode != null) {
                preferredDisplayModeId = bestMode.modeId
            }
        }
        
        requestNeededPermissions()
        enableEdgeToEdge()
        setContent {
            val themeColor by viewModel.themeColor.collectAsState()
            val useOled by viewModel.useOledTheme.collectAsState()
            val initialRoute = intent?.getStringExtra(com.bledroid.service.SpamForegroundService.EXTRA_ROUTE)
            BleDroidTheme(themeColor = themeColor, useOled = useOled) {
                BleDroidMainHost(viewModel = viewModel, initialRoute = initialRoute)
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        // Trigger recomposition with new route if app was already foreground
        val route = intent.getStringExtra(com.bledroid.service.SpamForegroundService.EXTRA_ROUTE)
        if (route != null) {
            viewModel.navigateTo(route)
        }
    }

    @SuppressLint("InlinedApi")
    private fun requestNeededPermissions() {
        val perms = mutableListOf(
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            perms.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        val missing = perms.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (missing.isNotEmpty()) permissionLauncher.launch(missing.toTypedArray())
    }
}

@Composable
fun BleDroidMainHost(viewModel: BleDroidViewModel, initialRoute: String? = null) {
    val pendingNavRoute by viewModel.pendingNavRoute.collectAsState()
    var navStack by remember {
        mutableStateOf(
            if (initialRoute != null) listOf(Routes.DASHBOARD, initialRoute)
            else listOf(Routes.DASHBOARD)
        )
    }
    val currentRoute = navStack.lastOrNull() ?: Routes.DASHBOARD

    // Handle navigateTo() calls from ViewModel (e.g. onNewIntent while in foreground)
    LaunchedEffect(pendingNavRoute) {
        val route = pendingNavRoute
        if (route != null) {
            navStack = listOf(Routes.DASHBOARD, route)
            viewModel.clearPendingNav()
        }
    }

    val popBackStack = {
        if (navStack.size > 1) {
            navStack = navStack.dropLast(1)
        } else {
            navStack = listOf(Routes.DASHBOARD)
        }
    }

    // Only show bottom bar on top-level routes
    val showBottomBar = bottomNavItems.any { it.route == currentRoute }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Crossfade(
            targetState = currentRoute,
            modifier = Modifier.fillMaxSize(),
            label = "screen_transition"
        ) { route ->
            when (route) {
                Routes.DASHBOARD -> DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToFastPair = { navStack = navStack + Routes.FAST_PAIR },
                    onNavigateToApple = { navStack = navStack + Routes.APPLE },
                    onNavigateToSamsung = { navStack = navStack + Routes.SAMSUNG },
                    onNavigateToSwiftPair = { navStack = navStack + Routes.SWIFT_PAIR },
                    onNavigateToLovespouse = { navStack = navStack + Routes.LOVESPOUSE },
                    onNavigateToMixAll = { navStack = navStack + Routes.MIX_ALL },
                    onNavigateToSpamRadar = { navStack = navStack + Routes.SPAM_RADAR },
                    onNavigateToSettings = { navStack = listOf(Routes.SETTINGS) }, // Swap base
                )
                Routes.FAST_PAIR -> FastPairScreen(viewModel = viewModel, onBack = popBackStack)
                Routes.APPLE -> AppleScreen(viewModel = viewModel, onBack = popBackStack)
                Routes.SAMSUNG -> SamsungScreen(viewModel = viewModel, onBack = popBackStack)
                Routes.SWIFT_PAIR -> SwiftPairScreen(viewModel = viewModel, onBack = popBackStack)
                Routes.LOVESPOUSE -> LovespouseScreen(viewModel = viewModel, onBack = popBackStack)
                Routes.MIX_ALL -> MixAllScreen(viewModel = viewModel, onBack = popBackStack)
                Routes.SPAM_RADAR -> SpamRadarScreen(viewModel = viewModel, onBack = popBackStack)
                "custom_ble" -> CustomBleScreen(viewModel = viewModel)
                Routes.SETTINGS -> SettingsScreen(viewModel = viewModel, onBack = popBackStack)
            }
        }

        if (showBottomBar) {
            ExpressiveBottomBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    if (route != currentRoute) {
                        navStack = listOf(route) // Reset stack to new top-level tab
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun ExpressiveBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Floating pill nav — no gray background, uses shadow only, larger footprint
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp), // Less side margin so the pill can be wider
        horizontalArrangement = Arrangement.Center,
    ) {
        Surface(
            shape = RoundedCornerShape(36.dp),
            color = MaterialTheme.colorScheme.surfaceContainer, // More solid, distinct from base background
            tonalElevation = 0.dp, // Disable tonal elevation to prevent dark mode gray tinting
            shadowElevation = 24.dp, // Very strong shadow for floating effect
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp), // Bigger inner padding
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                bottomNavItems.forEach { dest ->
                    val isSelected = currentRoute == dest.route
                    ExpressiveNavItem(
                        destination = dest,
                        isSelected = isSelected,
                        onClick = { onNavigate(dest.route) },
                    )
                }
            }
        }
    }
}

@Composable
fun ExpressiveNavItem(
    destination: NavDestination,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "navScale",
    )

    val containerColor by animateColorAsState(
        targetValue = if (isSelected)
            MaterialTheme.colorScheme.primaryContainer
        else
            Color.Transparent,
        animationSpec = tween(250, easing = FastOutSlowInEasing),
        label = "navColor",
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected)
            MaterialTheme.colorScheme.onPrimaryContainer
        else
            MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(250, easing = FastOutSlowInEasing),
        label = "navContentColor",
    )

    val horizontalPadding by animateDpAsState(
        targetValue = if (isSelected) 18.dp else 14.dp,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 550f),
        label = "navPadding",
    )

    Surface(
        onClick = onClick,
        modifier = Modifier.scale(scale).height(48.dp),
        shape = RoundedCornerShape(28.dp),
        color = containerColor,
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = horizontalPadding)
                .animateContentSize(animationSpec = spring(dampingRatio = 0.8f, stiffness = 550f)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = destination.icon,
                contentDescription = destination.label,
                tint = contentColor,
                modifier = Modifier.size(26.dp), // Larger icon
            )
            AnimatedVisibility(
                visible = isSelected,
                enter = expandHorizontally(animationSpec = spring(dampingRatio = 0.8f, stiffness = 550f)) + fadeIn(animationSpec = tween(200)),
                exit = shrinkHorizontally(animationSpec = spring(dampingRatio = 0.8f, stiffness = 550f)) + fadeOut(animationSpec = tween(150)),
            ) {
                Text(
                    text = destination.label,
                    modifier = Modifier.padding(start = 10.dp), // More space between icon and text
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = contentColor,
                    fontSize = 15.sp, // Larger font
                )
            }
        }
    }
}
