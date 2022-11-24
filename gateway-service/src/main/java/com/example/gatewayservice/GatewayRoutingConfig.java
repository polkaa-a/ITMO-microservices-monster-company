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
                .route(route -> route.path("/users/**")
                       .filters(f -> f.filter(authFactory.apply(
                               new Config(List.of("ADMIN"))
                        )))
                        .uri("lb://user-service"))
                .route(route -> route.path("/roles/**")
                        .filters(f -> f.filter(authFactory.apply(
                                new Config(List.of("ADMIN"))
                        )))
                        .uri("lb://user-service"))
                .route(route -> route.path("/infections/**")
                        .filters(f -> f.filter(authFactory.apply(
                                new Config(List.of("ADMIN, DISINFECTOR"))
                        )))
                        .uri("lb://infection-service"))
                .route(route -> route.path("/infected-things/**")
                        .filters(f -> f.filter(authFactory.apply(
                                new Config(List.of("ADMIN, DISINFECTOR"))
                        )))
                        .uri("lb://infection-service"))
                .route(route -> route.path("/doors/**")
                        .filters(f -> f.filter(authFactory.apply(
                                new Config(List.of("ADMIN"))
                        )))
                        .uri("lb://child-service"))
                .route(route -> route.path("/child/**")
                        .filters(f -> f.filter(authFactory.apply(
                                new Config(List.of("ADMIN", "SCARER", "SCARER ASSISTANT"))
                        )))
                        .uri("lb://child-service"))
                .route(route -> route.path("/auth")
                        .uri("lb://user-service"))
                .route(route -> route.path("/monsters/**")
                        .filters(f -> f.filter(authFactory.apply(
                                new Config(List.of("ADMIN", "SCARER"))
                        )))
                        .uri("lb://monster-service"))
                .route(route -> route.path("/cities/**")
                        .filters(f -> f.filter(authFactory.apply(
                                new Config(List.of("ADMIN"))
                        )))
                        .uri("lb://monster-service"))
                .route(route -> route.path("/electric-balloons/**")
                        .filters(f -> f.filter(authFactory.apply(
                                new Config(List.of("ADMIN"))
                        )))
                        .uri("lb://monster-service"))
                .route(route -> route.path("/fear-actions/**")
                        .filters(f -> f.filter(authFactory.apply(
                                new Config(List.of("ADMIN"))
                        )))
                        .uri("lb://monster-service"))
                .route(route -> route.path("/rewards/**")
                        .filters(f -> f.filter(authFactory.apply(
                                new Config(List.of("ADMIN"))
                        )))
                        .uri("lb://monster-service"))
                .build();
    }
}


