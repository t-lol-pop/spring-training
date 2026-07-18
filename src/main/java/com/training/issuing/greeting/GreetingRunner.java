package com.training.issuing.greeting;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class GreetingRunner implements CommandLineRunner {

    private final Greeter greeter;

    public GreetingRunner(Greeter greeter) {
        this.greeter = greeter;
    }

    @Override
    public void run(String... args) {
        System.out.println(greeter.greet("山田太郎"));
    }
}
