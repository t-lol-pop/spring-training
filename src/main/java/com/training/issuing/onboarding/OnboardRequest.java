package com.training.issuing.onboarding;

import jakarta.validation.constraints.NotBlank;

public record OnboardRequest(
        @NotBlank(message = "氏名は必須です") String name,
        @NotBlank(message = "チャネルは必須です") String channel) {
}
