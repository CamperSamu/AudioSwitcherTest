package it.campersamu.audioswitchertest

import android.media.MediaRoute2Info
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.runtime.NavEntry

sealed class AudioAppRoute {
    object DeviceList : AudioAppRoute()
    data class DeviceDetail(val routeId: String, val name: String) : AudioAppRoute()
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun AudioSwitcherApp(viewModel: AudioViewModel = viewModel()) {
    val backStack = remember { mutableStateListOf<AudioAppRoute>(AudioAppRoute.DeviceList) }
    val routes by viewModel.availableRoutes.observeAsState(emptyList())
    
    LaunchedEffect(Unit) {
        viewModel.refreshRoutes()
    }

    val strategy = rememberListDetailSceneStrategy<AudioAppRoute>()

    NavDisplay(
        backStack = backStack,
        sceneStrategies = listOf(strategy),
        onBack = {
            if (backStack.size > 1) {
                backStack.removeAt(backStack.lastIndex)
            }
        },
        entryProvider = { key ->
            when (key) {
                is AudioAppRoute.DeviceList -> NavEntry(
                    key = key,
                    metadata = ListDetailSceneStrategy.listPane(
                        detailPlaceholder = {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Select an audio device", style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    )
                ) {
                    AudioDeviceListScreen(
                        routes = routes,
                        onRefresh = { viewModel.refreshRoutes() },
                        onDeviceClick = { route ->
                            viewModel.selectRoute(route)
                            backStack.add(AudioAppRoute.DeviceDetail(route.id, route.name.toString()))
                        }
                    )
                }
                is AudioAppRoute.DeviceDetail -> NavEntry(
                    key = key,
                    metadata = ListDetailSceneStrategy.detailPane()
                ) {
                    DeviceDetailScreen(key.name)
                }
            }
        }
    )
}

@Composable
fun AudioDeviceListScreen(
    routes: List<MediaRoute2Info>,
    onRefresh: () -> Unit,
    onDeviceClick: (MediaRoute2Info) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Media output",
                    style = MaterialTheme.typography.headlineSmall
                )
                IconButton(onClick = onRefresh) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh list"
                    )
                }
            }
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(routes, key = { it.id }) { route ->
                    AudioDeviceItem(
                        route = route,
                        onClick = { onDeviceClick(route) }
                    )
                }
            }
        }
    }
}

@Composable
fun AudioDeviceItem(
    route: MediaRoute2Info,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val icon: ImageVector = when (route.type) {
                MediaRoute2Info.TYPE_BUILTIN_SPEAKER -> Icons.Default.Phone
                MediaRoute2Info.TYPE_BLUETOOTH_A2DP -> Icons.Default.Bluetooth
                else -> Icons.Default.Speaker
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = route.name.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (route.description != null) {
                    Text(
                        text = route.description.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun DeviceDetailScreen(deviceName: String) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Speaker,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Connected to",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = deviceName,
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun AudioDeviceItemPreview() {
    it.campersamu.audioswitchertest.ui.theme.AudioSwitcherTestTheme {
        // Need to pass a mocked MediaRoute2Info but it's hard without Shizuku and actual runtime.
        // We'll skip the actual instance and just preview a static layout if we made a generic one.
        // But since AudioDeviceItem requires MediaRoute2Info, we can't easily preview it without mock.
    }
}
