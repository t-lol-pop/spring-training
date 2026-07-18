package com.training.issuing.point;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "issuing.point")
public record PointProperties(
        @DecimalMin(value = "0.0", inclusive = true) BigDecimal rate) {
}
