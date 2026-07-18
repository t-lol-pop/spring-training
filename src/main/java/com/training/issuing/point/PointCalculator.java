package com.training.issuing.point;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

@Component
public class PointCalculator {

    private final PointProperties pointProperties;

    public PointCalculator(PointProperties pointProperties) {
        this.pointProperties = pointProperties;
    }

    public long calculate(long purchaseAmount) {
        BigDecimal points = BigDecimal.valueOf(purchaseAmount).multiply(pointProperties.rate());
        return points.longValue();
    }
}
