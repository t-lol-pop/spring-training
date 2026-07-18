package com.training.issuing.onboarding;

import org.springframework.stereotype.Component;

@Component
public class WebChannelBonusPolicy implements ChannelBonusPolicy {

    @Override
    public boolean supports(String channel) {
        return "WEB".equals(channel);
    }

    @Override
    public int calculateBonusPoints() {
        return 100;
    }
}
