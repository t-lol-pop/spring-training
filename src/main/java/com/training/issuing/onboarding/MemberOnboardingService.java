package com.training.issuing.onboarding;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.training.issuing.domain.Member;
import com.training.issuing.member.MemberRepository;

@Service
public class MemberOnboardingService {

    private final MemberRepository memberRepository;

    public MemberOnboardingService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public String onboard(String name, String channel) {
        System.out.println("[LOG] onboard start: name=" + name + ", channel=" + channel);

        if (name == null || name.length() == 0) {
            System.out.println("[LOG] validation failed: name is empty");
            throw new IllegalArgumentException("name is required");
        }
        if (name.length() > 100) {
            System.out.println("[LOG] validation failed: name too long");
            throw new IllegalArgumentException("name is too long");
        }

        String id = UUID.randomUUID().toString();
        Member member = new Member(id, name, LocalDate.now());
        memberRepository.save(member);
        System.out.println("[LOG] member saved: id=" + id);

        int bonusPoints;
        if (channel.equals("WEB")) {
            bonusPoints = 100;
        } else if (channel.equals("APP")) {
            bonusPoints = 200;
        } else if (channel.equals("STORE")) {
            bonusPoints = 50;
        } else {
            bonusPoints = 0;
        }
        System.out.println("[LOG] bonus points calculated: " + bonusPoints);

        EmailSender emailSender = new EmailSender();
        emailSender.send(name, "ご登録ありがとうございます。" + bonusPoints + "ポイントを付与しました。");
        System.out.println("[LOG] email sent");

        SmsSender smsSender = new SmsSender();
        smsSender.send(name, "登録完了のお知らせ");
        System.out.println("[LOG] sms sent");

        System.out.println("[LOG] onboard end: id=" + id);
        return id;
    }
}
