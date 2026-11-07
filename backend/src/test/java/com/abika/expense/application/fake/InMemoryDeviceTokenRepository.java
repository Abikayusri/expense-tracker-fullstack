package com.abika.expense.application.fake;

import com.abika.expense.application.port.out.DeviceTokenRepository;
import com.abika.expense.domain.DeviceToken;

import java.util.ArrayList;
import java.util.List;

public class InMemoryDeviceTokenRepository implements DeviceTokenRepository {

    private final List<DeviceToken> tokens = new ArrayList<>();

    @Override
    public void upsert(DeviceToken token) {
        tokens.removeIf(t -> t.fcmToken().equals(token.fcmToken()));
        tokens.add(token);
    }

    @Override
    public void deleteByUserAndToken(Long userId, String fcmToken) {
        tokens.removeIf(t -> t.userId().equals(userId) && t.fcmToken().equals(fcmToken));
    }

    @Override
    public List<DeviceToken> findByUser(Long userId) {
        return tokens.stream().filter(t -> t.userId().equals(userId)).toList();
    }
}
