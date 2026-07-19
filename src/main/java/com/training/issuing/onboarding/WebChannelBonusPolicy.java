package com.training.issuing.onboarding;

import org.springframework.stereotype.Component;

@Component
public class WebChannelBonusPolicy implements ChannelBonusPolicy {

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.WEB;
    }

    @Override
    public int calculateBonusPoints() {
        return 100;
    }
}
