package com.training.issuing.member;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.training.issuing.domain.Member;

@Repository
public class MemberRepository {

    private final MemberJpaRepository memberJpaRepository;

    public MemberRepository(MemberJpaRepository memberJpaRepository) {
        this.memberJpaRepository = memberJpaRepository;
    }

    public Member save(Member member) {
        MemberJpaEntity saved = memberJpaRepository.save(MemberJpaEntity.fromDomain(member));
        return saved.toDomain();
    }

    public Optional<Member> findById(String id) {
        return memberJpaRepository.findById(id).map(MemberJpaEntity::toDomain);
    }
}
