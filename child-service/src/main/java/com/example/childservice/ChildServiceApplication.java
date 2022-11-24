package com.example.childservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ChildServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChildServiceApplication.class, args);
    }

}
