package com.training.issuing.onboarding;

import org.springframework.stereotype.Component;

@Component
public class AppChannelBonusPolicy implements ChannelBonusPolicy {

    @Override
    public boolean supports(String channel) {
        return "APP".equals(channel);
    }

    @Override
    public int calculateBonusPoints() {
        return 200;
    }
}
