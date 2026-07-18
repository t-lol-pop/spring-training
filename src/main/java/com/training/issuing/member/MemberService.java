package com.training.issuing.member;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.training.issuing.domain.Member;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public MemberResponse register(MemberRegisterRequest request) {
        String id = UUID.randomUUID().toString();
        Member member = new Member(id, request.name(), LocalDate.now());
        memberRepository.save(member);
        return MemberResponse.from(member);
    }

    public MemberResponse findById(String id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException(id));
        return MemberResponse.from(member);
    }
}
