package com.abika.expense.application.port.in;

import com.abika.expense.domain.Platform;

public interface DeviceUseCase {

    void register(Long userId, String fcmToken, Platform platform);

    void unregister(Long userId, String fcmToken);
}
