package com.training.issuing.onboarding;

import org.springframework.stereotype.Component;

@Component
public class StoreChannelBonusPolicy implements ChannelBonusPolicy {

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.STORE;
    }

    @Override
    public int calculateBonusPoints() {
        return 50;
    }
}
