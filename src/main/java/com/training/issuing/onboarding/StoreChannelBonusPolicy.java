package com.training.issuing.onboarding;

import org.springframework.stereotype.Component;

@Component
public class StoreChannelBonusPolicy implements ChannelBonusPolicy {

    @Override
    public boolean supports(String channel) {
        return "STORE".equals(channel);
    }

    @Override
    public int calculateBonusPoints() {
        return 50;
    }
}
