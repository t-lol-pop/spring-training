package com.training.issuing.member;

import jakarta.validation.constraints.NotBlank;

public record MemberRegisterRequest(
        @NotBlank(message = "氏名は必須です") String name) {
}
