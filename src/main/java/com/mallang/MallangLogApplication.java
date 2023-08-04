package com.mallang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class MallangLogApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallangLogApplication.class, args);
    }
}
