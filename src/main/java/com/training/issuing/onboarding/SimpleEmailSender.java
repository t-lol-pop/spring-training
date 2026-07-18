package com.training.issuing.onboarding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SimpleEmailSender implements EmailSender {

    private static final Logger log = LoggerFactory.getLogger(SimpleEmailSender.class);

    @Override
    public void send(String to, String message) {
        log.info("[EMAIL] to={}, message={}", to, message);
    }
}
