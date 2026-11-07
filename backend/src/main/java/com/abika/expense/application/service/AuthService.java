package com.abika.expense.application.service;

import com.abika.expense.application.port.in.AuthUseCase;
import com.abika.expense.application.port.in.RegisterCommand;
import com.abika.expense.application.port.out.PasswordHasher;
import com.abika.expense.application.port.out.TokenIssuer;
import com.abika.expense.application.port.out.UserRepository;
import com.abika.expense.domain.User;
import com.abika.expense.domain.exception.ConflictException;
import com.abika.expense.domain.exception.InvalidCredentialsException;
import com.abika.expense.domain.exception.NotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

public class AuthService implements AuthUseCase {

    private final UserRepository users;
    private final PasswordHasher passwordHasher;
    private final TokenIssuer tokenIssuer;
    private final Clock clock;

    public AuthService(UserRepository users, PasswordHasher passwordHasher, TokenIssuer tokenIssuer, Clock clock) {
        this.users = users;
        this.passwordHasher = passwordHasher;
        this.tokenIssuer = tokenIssuer;
        this.clock = clock;
    }

    @Override
    @Transactional
    public String register(RegisterCommand command) {
        String email = User.normalizeEmail(command.email());
        if (users.existsByEmail(email)) {
            throw new ConflictException("Email sudah terdaftar");
        }
        User saved = users.save(new User(null, email, passwordHasher.hash(command.password()),
                command.name().trim(), clock.instant()));
        return tokenIssuer.issue(saved.id());
    }

    @Override
    @Transactional(readOnly = true)
    public String login(String email, String password) {
        // Same message for unknown email and wrong password, so attackers can't probe which emails exist.
        User user = users.findByEmail(User.normalizeEmail(email))
                .filter(u -> passwordHasher.matches(password, u.passwordHash()))
                .orElseThrow(() -> new InvalidCredentialsException("Email atau password salah"));
        return tokenIssuer.issue(user.id());
    }

    @Override
    @Transactional(readOnly = true)
    public User me(Long userId) {
        return users.findById(userId)
                .orElseThrow(() -> new NotFoundException("User tidak ditemukan"));
    }
}
