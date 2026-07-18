package com.training.issuing.member;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.training.issuing.domain.Member;

@Entity
@Table(name = "members")
public class MemberJpaEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(name = "registered_day", nullable = false)
    private LocalDate registeredDay;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Member.Status status;

    protected MemberJpaEntity() {
        // JPAがリフレクションでインスタンス化するためのデフォルトコンストラクタ
    }

    private MemberJpaEntity(String id, String name, LocalDate registeredDay, Member.Status status) {
        this.id = id;
        this.name = name;
        this.registeredDay = registeredDay;
        this.status = status;
    }

    public static MemberJpaEntity fromDomain(Member member) {
        return new MemberJpaEntity(
                member.getId(),
                member.getName(),
                member.getRegisteredDay(),
                member.getCurrentStatus());
    }

    public Member toDomain() {
        Member member = new Member(id, name, registeredDay);
        switch (status) {
            case SUSPENDED -> member.suspend();
            case WITHDRAWN -> member.withdraw();
            default -> {
                // ACTIVEはMemberのコンストラクタが既定値として設定するため何もしない
            }
        }
        return member;
    }
}
