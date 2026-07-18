package com.training.issuing.greeting;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class SimpleGreeter implements Greeter {

    @Override
    public String greet(String memberName) {
        return memberName + "さん、ようこそ。";
    }
}
