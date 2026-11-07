package com.abika.expense.adapter.in.web;

import com.abika.expense.application.port.in.AuthUseCase;
import com.abika.expense.application.port.in.RegisterCommand;
import com.abika.expense.domain.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
class AuthController {

    private final AuthUseCase auth;

    AuthController(AuthUseCase auth) {
        this.auth = auth;
    }

    @PostMapping("/auth/register")
    @ResponseStatus(HttpStatus.CREATED)
    TokenResponse register(@Valid @RequestBody RegisterRequest request) {
        return new TokenResponse(auth.register(new RegisterCommand(request.email(), request.password(), request.name())));
    }

    @PostMapping("/auth/login")
    TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return new TokenResponse(auth.login(request.email(), request.password()));
    }

    @GetMapping("/me")
    MeResponse me(@AuthenticationPrincipal Jwt jwt) {
        User user = auth.me(CurrentUser.id(jwt));
        return new MeResponse(user.id(), user.email(), user.name());
    }
}
