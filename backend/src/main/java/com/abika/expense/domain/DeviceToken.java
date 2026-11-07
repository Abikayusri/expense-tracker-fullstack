package com.abika.expense.domain;

public record DeviceToken(Long id, Long userId, String fcmToken, Platform platform) {
}
