package com.training.issuing.point;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class PointRunner implements CommandLineRunner {

    private final PointCalculator pointCalculator;

    public PointRunner(PointCalculator pointCalculator) {
        this.pointCalculator = pointCalculator;
    }

    @Override
    public void run(String... args) {
        long sampleAmount = 10_000L;
        long points = pointCalculator.calculate(sampleAmount);
        System.out.println(sampleAmount + "円の購入で" + points + "ポイント付与されます。");
    }
}
