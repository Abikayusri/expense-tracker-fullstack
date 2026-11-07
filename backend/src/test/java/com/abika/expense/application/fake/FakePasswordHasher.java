package com.abika.expense.application.fake;

import com.abika.expense.application.port.out.PasswordHasher;

public class FakePasswordHasher implements PasswordHasher {

    @Override
    public String hash(String raw) {
        return "hashed:" + raw;
    }

    @Override
    public boolean matches(String raw, String hash) {
        return hash.equals(hash(raw));
    }
}
