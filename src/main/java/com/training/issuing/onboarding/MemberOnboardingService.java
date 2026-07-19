package com.training.issuing.onboarding;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.training.issuing.domain.Member;
import com.training.issuing.member.MemberRepository;

@Validated
@Service
public class MemberOnboardingService {

    private static final Logger log = LoggerFactory.getLogger(MemberOnboardingService.class);

    private final MemberRepository memberRepository;
    private final ChannelBonusCalculator channelBonusCalculator;
    private final EmailSender emailSender;
    private final SmsSender smsSender;

    public MemberOnboardingService(
            MemberRepository memberRepository,
            ChannelBonusCalculator channelBonusCalculator,
            EmailSender emailSender,
            SmsSender smsSender) {
        this.memberRepository = memberRepository;
        this.channelBonusCalculator = channelBonusCalculator;
        this.emailSender = emailSender;
        this.smsSender = smsSender;
    }

    public String onboard(@Valid OnboardRequest request) {
        log.info("onboard start: name={}, channel={}", request.name(), request.channel());

        String id = UUID.randomUUID().toString();
        Member member = new Member(id, request.name(), LocalDate.now());
        memberRepository.save(member);
        log.info("member saved: id={}", id);

        int bonusPoints = channelBonusCalculator.calculate(request.channel());
        log.info("bonus points calculated: {}", bonusPoints);

        emailSender.send(request.name(), "ご登録ありがとうございます。" + bonusPoints + "ポイントを付与しました。");
        smsSender.send(request.name(), "登録完了のお知らせ");
        log.info("onboard end: id={}", id);

        return id;
    }
}
