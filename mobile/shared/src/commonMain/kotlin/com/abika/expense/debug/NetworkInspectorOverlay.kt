package com.abika.expense.debug

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.abika.expense.core.network.ApiConfig
import org.koin.compose.koinInject
import ro.cosminmihu.ktor.monitor.KtorMonitor

/** Debug builds only: a small button that opens KtorMonitor over the app. */
@Composable
fun BoxScope.NetworkInspectorOverlay() {
    val apiConfig = koinInject<ApiConfig>()
    if (!apiConfig.enableNetworkInspector) return
    var open by remember { mutableStateOf(false) }

    if (open) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
                KtorMonitor(modifier = Modifier.fillMaxSize())
                TextButton(onClick = { open = false }, modifier = Modifier.align(Alignment.TopEnd)) {
                    Text("Tutup")
                }
            }
        }
    } else {
        SmallFloatingActionButton(
            onClick = { open = true },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .safeDrawingPadding()
                .padding(12.dp)
                .semantics { contentDescription = "Buka network inspector" },
        ) {
            Text("API")
        }
    }
}
