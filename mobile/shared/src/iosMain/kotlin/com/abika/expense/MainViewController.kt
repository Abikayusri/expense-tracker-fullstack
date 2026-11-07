package com.abika.expense

import androidx.compose.ui.window.ComposeUIViewController
import com.abika.expense.core.network.ApiConfig
import platform.Foundation.NSBundle
import platform.UIKit.UIViewController
import kotlin.experimental.ExperimentalNativeApi

fun MainViewController(): UIViewController = ComposeUIViewController { App() }

@OptIn(ExperimentalNativeApi::class)
fun doInitKoinIos() {
    val baseUrl = NSBundle.mainBundle.objectForInfoDictionaryKey("ApiBaseUrl") as? String
    initKoin(ApiConfig(baseUrl = baseUrl ?: "http://localhost:8080", enableNetworkInspector = Platform.isDebugBinary))
}
