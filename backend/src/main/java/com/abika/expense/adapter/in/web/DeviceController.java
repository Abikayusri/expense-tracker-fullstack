package com.abika.expense.adapter.in.web;

import com.abika.expense.application.port.in.DeviceUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/devices")
class DeviceController {

    private final DeviceUseCase devices;

    DeviceController(DeviceUseCase devices) {
        this.devices = devices;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void register(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody DeviceRequest request) {
        devices.register(CurrentUser.id(jwt), request.fcmToken(), request.platform());
    }

    // Unknown tokens or tokens of another user are ignored, so logout can always call this safely.
    @DeleteMapping("/{fcmToken}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void unregister(@AuthenticationPrincipal Jwt jwt, @PathVariable String fcmToken) {
        devices.unregister(CurrentUser.id(jwt), fcmToken);
    }
}
