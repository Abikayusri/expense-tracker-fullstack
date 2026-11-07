package com.abika.expense.core.network

/** Provided by each app: the emulator, the simulator and a phone reach the backend at different hosts. */
data class ApiConfig(
    val baseUrl: String,
    val enableNetworkInspector: Boolean,
)
