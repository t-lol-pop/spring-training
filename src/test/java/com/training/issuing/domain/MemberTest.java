package com.training.issuing.domain;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class MemberTest {

    @Test
    @DisplayName("会員を新規作成すると状態はACTIVEになり、カード発行可能と判定される")
    void newMemberIsCardIssuable() {
        Member member = new Member("M001", "山田太郎", LocalDate.of(2026, 1, 1));
        assertTrue(member.isCardIssuable());
    }

    @Test
    @DisplayName("一時停止した会員はカード発行不可と判定される")
    void suspendedMemberIsNotCardIssuable() {
        Member member = new Member("M002", "鈴木花子", LocalDate.of(2026, 1, 1));
        member.suspend();
        assertFalse(member.isCardIssuable());
    }

    @Test
    @DisplayName("退会した会員はカード発行不可と判定される")
    void withdrawnMemberIsNotCardIssuable() {
        Member member = new Member("M003", "佐藤次郎", LocalDate.of(2026, 1, 1));
        member.withdraw();
        assertFalse(member.isCardIssuable());
    }

    @Test
    @DisplayName("会員IDが同じであれば、他の属性が異なっていてもequalsでtrueになる")
    void sameIdMembersAreEqual() {
        Member a = new Member("M004", "田中一郎", LocalDate.of(2026, 1, 1));
        Member b = new Member("M004", "田中次郎", LocalDate.of(2026, 2, 1));
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    @DisplayName("会員IDが異なればequalsでfalseになる")
    void differentIdMembersAreNotEqual() {
        Member a = new Member("M005", "高橋三郎", LocalDate.of(2026, 1, 1));
        Member b = new Member("M006", "高橋三郎", LocalDate.of(2026, 1, 1));
        assertNotEquals(a, b);
    }
}
