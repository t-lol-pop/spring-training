package com.training.issuing.onboarding;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OnboardRequest(
        @NotBlank(message = "氏名は必須です")
        @Size(max = 100, message = "氏名は100文字以内で入力してください") String name,
        @NotNull(message = "チャネルは必須です") Channel channel) {
}
