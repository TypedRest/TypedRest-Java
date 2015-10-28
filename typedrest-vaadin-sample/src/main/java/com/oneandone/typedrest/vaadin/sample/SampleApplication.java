package com.oneandone.typedrest.vaadin.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.web.*;

/**
 *
 */
@SpringBootApplication
@EnableAutoConfiguration
public class SampleApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }
}
