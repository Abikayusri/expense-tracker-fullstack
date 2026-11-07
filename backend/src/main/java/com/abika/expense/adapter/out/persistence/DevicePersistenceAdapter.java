package com.abika.expense.adapter.out.persistence;

import com.abika.expense.application.port.out.DeviceTokenRepository;
import com.abika.expense.domain.DeviceToken;
import com.abika.expense.domain.Platform;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DevicePersistenceAdapter implements DeviceTokenRepository {

    private final NamedParameterJdbcTemplate jdbc;

    DevicePersistenceAdapter(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void upsert(DeviceToken token) {
        jdbc.update("""
                        INSERT INTO device_tokens (user_id, fcm_token, platform)
                        VALUES (:userId, :fcmToken, :platform)
                        ON CONFLICT (fcm_token) DO UPDATE SET user_id = EXCLUDED.user_id, platform = EXCLUDED.platform""",
                Map.of("userId", token.userId(), "fcmToken", token.fcmToken(), "platform", token.platform().name()));
    }

    @Override
    public void deleteByUserAndToken(Long userId, String fcmToken) {
        jdbc.update("DELETE FROM device_tokens WHERE user_id = :userId AND fcm_token = :fcmToken",
                Map.of("userId", userId, "fcmToken", fcmToken));
    }

    @Override
    public List<DeviceToken> findByUser(Long userId) {
        return jdbc.query("SELECT id, user_id, fcm_token, platform FROM device_tokens WHERE user_id = :userId ORDER BY id",
                Map.of("userId", userId),
                (rs, row) -> new DeviceToken(rs.getLong("id"), rs.getLong("user_id"), rs.getString("fcm_token"),
                        Platform.valueOf(rs.getString("platform"))));
    }
}
