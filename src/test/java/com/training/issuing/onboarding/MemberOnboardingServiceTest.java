package com.training.issuing.onboarding;

import jakarta.validation.ConstraintViolationException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.training.issuing.member.MemberRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@Testcontainers
class MemberOnboardingServiceTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void configureDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MemberOnboardingService memberOnboardingService;

    @MockBean
    private MemberRepository memberRepository;

    @Test
    @DisplayName("正常な入力であれば会員登録が成功する")
    void onboardSucceedsWithValidRequest() {
        when(memberRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        String id = memberOnboardingService.onboard(new OnboardRequest("山田太郎", "WEB"));

        assertThat(id).isNotBlank();
    }

    @Test
    @DisplayName("氏名が空の場合はConstraintViolationExceptionが投げられる")
    void onboardThrowsWhenNameIsBlank() {
        assertThatThrownBy(() -> memberOnboardingService.onboard(new OnboardRequest("", "WEB")))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    @DisplayName("チャネルが空の場合はConstraintViolationExceptionが投げられる")
    void onboardThrowsWhenChannelIsBlank() {
        assertThatThrownBy(() -> memberOnboardingService.onboard(new OnboardRequest("山田太郎", "")))
                .isInstanceOf(ConstraintViolationException.class);
    }
}
