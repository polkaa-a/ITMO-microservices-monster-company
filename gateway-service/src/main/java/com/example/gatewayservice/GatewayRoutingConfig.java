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
                       .filters(f -> f.filter(authFactory.apply(
                               new Config(List.of("ADMIN","SCARER"))
                        )))
                        .uri("http://localhost:8081"))
                .route("user-service", route -> route.path("/roles/**")
                        .filters(f -> f.filter(authFactory.apply(
                                new Config(List.of("ADMIN"))
                        )))
                        .uri("http://localhost:8081"))
                .route("user-service", route -> route.path("/auth")
                        .uri("http://localhost:8081"))
                .build();
    }
}


