package com.training.issuing.point;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class PointCalculatorTest {

    @Test
    @DisplayName("付与率1%のとき、10000円の購入で100ポイント付与される")
    void calculatesPointsWithGivenRate() {
        PointProperties pointProperties = new PointProperties(new BigDecimal("0.01"));
        PointCalculator calculator = new PointCalculator(pointProperties);

        long points = calculator.calculate(10_000L);

        assertEquals(100L, points);
    }

    @Test
    @DisplayName("付与率5%のとき、10000円の購入で500ポイント付与される")
    void calculatesPointsWithDifferentRate() {
        PointProperties pointProperties = new PointProperties(new BigDecimal("0.05"));
        PointCalculator calculator = new PointCalculator(pointProperties);

        long points = calculator.calculate(10_000L);

        assertEquals(500L, points);
    }
}
