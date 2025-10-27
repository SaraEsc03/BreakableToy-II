package com.example.flightsapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application entry point.
 *
 * Placing the main class in package `com.flightsapp` ensures Spring Boot component scanning
 * covers all subpackages (client, controller, service). This avoids missing beans at startup.
 */
@SpringBootApplication
public class FlightsAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlightsAppApplication.class, args);
    }

}
