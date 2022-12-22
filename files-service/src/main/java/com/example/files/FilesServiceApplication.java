package com.example.files;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class FilesServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilesServiceApplication.class, args);
    }

}
