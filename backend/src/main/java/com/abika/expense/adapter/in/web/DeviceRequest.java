package com.abika.expense.adapter.in.web;

import com.abika.expense.domain.Platform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DeviceRequest(@NotBlank @Size(max = 512) String fcmToken, @NotNull Platform platform) {
}
