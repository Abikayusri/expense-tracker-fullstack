package com.abika.expense.application.service;

import com.abika.expense.application.port.in.DeviceUseCase;
import com.abika.expense.application.port.out.DeviceTokenRepository;
import com.abika.expense.domain.DeviceToken;
import com.abika.expense.domain.Platform;
import org.springframework.transaction.annotation.Transactional;

public class DeviceService implements DeviceUseCase {

    private final DeviceTokenRepository devices;

    public DeviceService(DeviceTokenRepository devices) {
        this.devices = devices;
    }

    @Override
    @Transactional
    public void register(Long userId, String fcmToken, Platform platform) {
        devices.upsert(new DeviceToken(null, userId, fcmToken, platform));
    }

    @Override
    @Transactional
    public void unregister(Long userId, String fcmToken) {
        devices.deleteByUserAndToken(userId, fcmToken);
    }
}
