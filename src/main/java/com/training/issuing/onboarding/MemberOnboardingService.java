package com.training.issuing.onboarding;

import java.time.LocalDate;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.training.issuing.domain.Member;
import com.training.issuing.member.MemberRepository;

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

    public String onboard(String name, String channel) {
        log.info("onboard start: name={}, channel={}", name, channel);

        String id = UUID.randomUUID().toString();
        Member member = new Member(id, name, LocalDate.now());
        memberRepository.save(member);
        log.info("member saved: id={}", id);

        int bonusPoints = channelBonusCalculator.calculate(channel);
        log.info("bonus points calculated: {}", bonusPoints);

        emailSender.send(name, "ご登録ありがとうございます。" + bonusPoints + "ポイントを付与しました。");
        smsSender.send(name, "登録完了のお知らせ");
        log.info("onboard end: id={}", id);

        return id;
    }
}
