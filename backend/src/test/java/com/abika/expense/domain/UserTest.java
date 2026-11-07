package com.abika.expense.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void normalizeEmail_trims_and_lowercases() {
        assertThat(User.normalizeEmail("  Budi@Mail.COM ")).isEqualTo("budi@mail.com");
    }
}
