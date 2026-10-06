package it.campersamu.audioswitchertest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import it.campersamu.audioswitchertest.ui.theme.AudioSwitcherTestTheme

class MainActivity : ComponentActivity(), ShizukuManager.ShizukuListener {
    private lateinit var shizukuManager: ShizukuManager
    private var shizukuStatus by mutableStateOf("Initializing...")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shizukuManager = ShizukuManager(this)
        shizukuManager.onCreate()
        
        enableEdgeToEdge()
        setContent {
            AudioSwitcherTestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    androidx.compose.foundation.layout.Column(modifier = Modifier.padding(innerPadding)) {
                        androidx.compose.foundation.layout.Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Text(text = "Shizuku: $shizukuStatus", style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
                            if (shizukuStatus != "Permission Granted") {
                                Button(onClick = { shizukuManager.requestPermission() }) {
                                    Text("Grant")
                                }
                            }
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            AudioSwitcherApp()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        shizukuManager.onDestroy()
    }

    override fun onShizukuAvailable() {
        shizukuStatus = "Shizuku Available"
    }

    override fun onShizukuUnavailable() {
        shizukuStatus = "Shizuku Unavailable"
    }

    override fun onPermissionGranted() {
        shizukuStatus = "Permission Granted"
    }

    override fun onPermissionDenied() {
        shizukuStatus = "Permission Denied"
    }
}
