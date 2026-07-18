package com.training.issuing.onboarding;

public interface ChannelBonusPolicy {

    boolean supports(String channel);

    int calculateBonusPoints();
}
