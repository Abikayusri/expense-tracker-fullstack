package com.abika.expense.config;

import com.abika.expense.application.port.in.AuthUseCase;
import com.abika.expense.application.port.out.PasswordHasher;
import com.abika.expense.application.port.out.TokenIssuer;
import com.abika.expense.application.port.out.UserRepository;
import com.abika.expense.application.service.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class UseCaseConfig {

    @Bean
    Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    AuthUseCase authUseCase(UserRepository users, PasswordHasher passwordHasher, TokenIssuer tokenIssuer, Clock clock) {
        return new AuthService(users, passwordHasher, tokenIssuer, clock);
    }
}
