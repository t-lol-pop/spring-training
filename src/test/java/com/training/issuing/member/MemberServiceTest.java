package com.training.issuing.member;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.training.issuing.domain.Member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Test
    @DisplayName("registerは新規会員をリポジトリに保存し、ACTIVE状態のレスポンスを返す")
    void registerSavesNewActiveMemberAndReturnsResponse() {
        MemberService memberService = new MemberService(memberRepository);
        MemberRegisterRequest request = new MemberRegisterRequest("山田太郎");
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MemberResponse response = memberService.register(request);

        assertThat(response.name()).isEqualTo("山田太郎");
        assertThat(response.status()).isEqualTo("ACTIVE");
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    @DisplayName("findByIdで存在しないIDを指定すると、MemberNotFoundExceptionが投げられる")
    void findByIdThrowsWhenMemberDoesNotExist() {
        MemberService memberService = new MemberService(memberRepository);
        when(memberRepository.findById("not-exist-id")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.findById("not-exist-id"))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    @DisplayName("findByIdで存在するIDを指定すると、その会員のレスポンスが返る")
    void findByIdReturnsResponseWhenMemberExists() {
        MemberService memberService = new MemberService(memberRepository);
        Member existing = new Member("M001", "鈴木花子", LocalDate.of(2026, 1, 1));
        when(memberRepository.findById("M001")).thenReturn(Optional.of(existing));

        MemberResponse response = memberService.findById("M001");

        assertThat(response.id()).isEqualTo("M001");
        assertThat(response.name()).isEqualTo("鈴木花子");
        assertThat(response.status()).isEqualTo("ACTIVE");
    }
}
