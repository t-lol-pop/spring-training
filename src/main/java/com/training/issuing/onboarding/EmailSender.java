package com.training.issuing.onboarding;

public interface EmailSender {

    void send(String to, String message);
}
