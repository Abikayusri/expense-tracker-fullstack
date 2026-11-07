package com.abika.expense.data.auth.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class DeviceRequestDto(val fcmToken: String, val platform: String)
