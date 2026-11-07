package com.abika.expense.application;

import com.abika.expense.application.fake.FakePasswordHasher;
import com.abika.expense.application.fake.InMemoryUserRepository;
import com.abika.expense.application.port.in.RegisterCommand;
import com.abika.expense.application.service.AuthService;
import com.abika.expense.domain.User;
import com.abika.expense.domain.exception.ConflictException;
import com.abika.expense.domain.exception.InvalidCredentialsException;
import com.abika.expense.domain.exception.NotFoundException;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;

class AuthServiceTest {

    private static final Instant NOW = Instant.parse("2026-11-07T10:00:00Z");

    private final InMemoryUserRepository users = new InMemoryUserRepository();
    private final AuthService service = new AuthService(
            users, new FakePasswordHasher(), userId -> "token-" + userId, Clock.fixed(NOW, ZoneOffset.UTC));

    @Test
    void register_returns_token_and_stores_normalized_email() {
        String token = service.register(new RegisterCommand("Budi@Mail.com", "rahasia123", "Budi"));

        assertThat(token).isEqualTo("token-1");
        assertThat(users.all()).singleElement().satisfies(u -> {
            assertThat(u.email()).isEqualTo("budi@mail.com");
            assertThat(u.passwordHash()).isEqualTo("hashed:rahasia123");
            assertThat(u.name()).isEqualTo("Budi");
            assertThat(u.createdAt()).isEqualTo(NOW);
        });
    }

    @Test
    void register_duplicate_email_case_insensitive_conflicts() {
        service.register(new RegisterCommand("Budi@Mail.com", "rahasia123", "Budi"));

        assertThatThrownBy(() -> service.register(new RegisterCommand("BUDI@mail.com", "lainlain1", "Budi 2")))
                .isInstanceOf(ConflictException.class);
        assertThat(users.all()).hasSize(1);
    }

    @Test
    void login_is_case_insensitive() {
        service.register(new RegisterCommand("Budi@Mail.com", "rahasia123", "Budi"));

        assertThat(service.login("budi@mail.com", "rahasia123")).isEqualTo("token-1");
    }

    @Test
    void login_wrong_password_and_unknown_email_throw_same_exception() {
        service.register(new RegisterCommand("budi@mail.com", "rahasia123", "Budi"));

        Throwable wrongPassword = catchThrowable(() -> service.login("budi@mail.com", "salah12345"));
        Throwable unknownEmail = catchThrowable(() -> service.login("siapa@mail.com", "rahasia123"));

        assertThat(wrongPassword).isInstanceOf(InvalidCredentialsException.class);
        assertThat(unknownEmail).isInstanceOf(InvalidCredentialsException.class);
        assertThat(wrongPassword.getMessage()).isEqualTo(unknownEmail.getMessage());
    }

    @Test
    void me_returns_user() {
        service.register(new RegisterCommand("budi@mail.com", "rahasia123", "Budi"));

        User me = service.me(1L);

        assertThat(me.email()).isEqualTo("budi@mail.com");
    }

    @Test
    void me_unknown_user_not_found() {
        assertThatThrownBy(() -> service.me(99L)).isInstanceOf(NotFoundException.class);
    }
}
