package com.training.issuing.member;

import java.time.LocalDate;

import com.training.issuing.domain.Member;

public record MemberResponse(
        String id,
        String name,
        LocalDate registeredDay,
        String status) {

    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getName(),
                member.getRegisteredDay(),
                member.getCurrentStatus().name());
    }
}
