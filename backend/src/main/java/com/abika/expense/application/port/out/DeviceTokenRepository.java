package com.abika.expense.application.port.out;

import com.abika.expense.domain.DeviceToken;

import java.util.List;

public interface DeviceTokenRepository {

    /** Inserts the token, or moves it to this user if another account registered it before (same phone, new login). */
    void upsert(DeviceToken token);

    void deleteByUserAndToken(Long userId, String fcmToken);

    List<DeviceToken> findByUser(Long userId);
}
