package com.abika.expense.domain;

import java.time.Instant;
import java.util.Locale;

public record User(Long id, String email, String passwordHash, String name, Instant createdAt) {

    public static String normalizeEmail(String raw) {
        return raw.trim().toLowerCase(Locale.ROOT);
    }
}
