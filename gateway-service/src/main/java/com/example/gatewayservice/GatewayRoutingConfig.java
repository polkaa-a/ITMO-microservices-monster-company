package com.example.gatewayservice;

import com.example.gatewayservice.authorization.RoleAuthGatewayFilterFactory;
import com.example.gatewayservice.authorization.RoleAuthGatewayFilterFactory.Config;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class GatewayRoutingConfig {
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder,
                                           RoleAuthGatewayFilterFactory authFactory) {
        return builder.routes()
                .route("user-service", route -> route.path("/users/**")
                        .uri("http://localhost:8081"))
                .route("user-service", route -> route.path("/roles/**")
                        .uri("http://localhost:8081"))
                .route("monster-service", route -> route.path("/monsters/**")
                        .uri("http://localhost:8082"))
                .route("monster-service", route -> route.path("/cities/**")
                        .uri("http://localhost:8082"))
                .route("monster-service", route -> route.path("/electric-balloons/**")
                        .uri("http://localhost:8082"))
                .route("monster-service", route -> route.path("/fear-actions/**")
                        .uri("http://localhost:8082"))
                .route("monster-service", route -> route.path("/rewards/**")
                        .uri("http://localhost:8082"))
                .build();
    }
}


