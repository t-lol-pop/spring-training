package com.training.issuing.onboarding;

public interface ChannelBonusPolicy {

    boolean supports(Channel channel);

    int calculateBonusPoints();
}
