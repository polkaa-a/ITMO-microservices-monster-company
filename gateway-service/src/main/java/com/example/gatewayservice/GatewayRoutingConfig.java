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
                               new Config(List.of("ADMIN","SCARER"))
                        )))
                        .uri("lb://user-service"))
                .route(route -> route.path("/roles/**")
                        .filters(f -> f.filter(authFactory.apply(
                                new Config(List.of("ADMIN"))
                        )))
                        .uri("lb://user-service"))
                .route(route -> route.path("/infections/**")
                        .uri("lb://infection-service"))
                .route(route -> route.path("/infected-things/**")
                        .uri("lb://infection-service"))
                .route(route -> route.path("/doors/**")
                        .uri("lb://child-service"))
                .route(route -> route.path("/child/**")
                        .uri("lb://child-service"))
                .route(route -> route.path("/auth")
                        .uri("lb://user-service"))
                .build();
    }
}


