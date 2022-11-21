package com.example.gatewayservice;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutingConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service", route -> route.path("/users/**")
                        .uri("http://localhost:8081"))
                .route("user-service", route -> route.path("/roles/**")
                        .uri("http://localhost:8081"))
                .route("infection-service", route -> route.path("/infections/**")
                        .uri("http://localhost:8083"))
                .build();
    }
}
