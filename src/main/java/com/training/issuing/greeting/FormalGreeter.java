package com.training.issuing.greeting;

import org.springframework.stereotype.Component;

@Component
public class FormalGreeter implements Greeter {

    @Override
    public String greet(String memberName) {
        return memberName + "様、心よりお待ちしておりました。";
    }
}
