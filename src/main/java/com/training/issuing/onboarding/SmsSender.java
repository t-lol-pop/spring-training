package com.training.issuing.onboarding;

public class SmsSender {

    public void send(String to, String message) {
        System.out.println("[SMS] to=" + to + ", message=" + message);
    }
}
