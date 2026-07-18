package com.training.issuing.onboarding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SimpleSmsSender implements SmsSender {

    private static final Logger log = LoggerFactory.getLogger(SimpleSmsSender.class);

    @Override
    public void send(String to, String message) {
        log.info("[SMS] to={}, message={}", to, message);
    }
}
