package com.training.issuing.onboarding;

import org.springframework.stereotype.Component;

@Component
public class AppChannelBonusPolicy implements ChannelBonusPolicy {

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.APP;
    }

    @Override
    public int calculateBonusPoints() {
        return 200;
    }
}
