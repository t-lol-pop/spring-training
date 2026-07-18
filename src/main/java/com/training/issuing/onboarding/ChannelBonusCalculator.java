package com.training.issuing.onboarding;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class ChannelBonusCalculator {

    private final List<ChannelBonusPolicy> policies;

    public ChannelBonusCalculator(List<ChannelBonusPolicy> policies) {
        this.policies = policies;
    }

    public int calculate(String channel) {
        return policies.stream()
                .filter(policy -> policy.supports(channel))
                .findFirst()
                .map(ChannelBonusPolicy::calculateBonusPoints)
                .orElse(0);
    }
}
