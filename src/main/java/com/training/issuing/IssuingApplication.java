package com.training.issuing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.training.issuing.point.PointProperties;

@SpringBootApplication
@EnableConfigurationProperties(PointProperties.class)
public class IssuingApplication {

    public static void main(String[] args) {
        SpringApplication.run(IssuingApplication.class, args);
    }
}
