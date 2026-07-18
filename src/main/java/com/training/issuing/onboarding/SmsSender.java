package com.training.issuing.onboarding;

public interface SmsSender {

    void send(String to, String message);
}
