package com.example.gatewayservice.authorization;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
@Component
@Slf4j
public class RoleAuthGatewayFilterFactory extends
        AbstractGatewayFilterFactory<RoleAuthGatewayFilterFactory.Config> {

    private static final String AUTHORIZATION = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer";
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Autowired
    public RoleAuthGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            var request = exchange.getRequest();
            JwtUtil jwtUtil = new JwtUtil(jwtSecret);

            if (!request.getHeaders().containsKey(AUTHORIZATION)) {
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }

            final String token = this.getAuthHeader(request);

            if (token == null || !jwtUtil.validateToken(token))
                return this.onError(exchange, HttpStatus.UNAUTHORIZED);

            String role = (String) jwtUtil.getAllClaimsFromToken(token).get("role");

            if (!config.getRoles().contains(role)) {
                return this.onError(exchange, HttpStatus.FORBIDDEN);
            }

            return chain.filter(exchange);
        };
    }

    @Data
    @AllArgsConstructor
    public static class Config {
        private List<String> roles;
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    private String getAuthHeader(ServerHttpRequest request) {
        String bearer = request.getHeaders().getOrEmpty(AUTHORIZATION).get(0);
        if (bearer != null && bearer.startsWith(TOKEN_PREFIX)) {
            return bearer.substring(7);
        }
        return null;
    }

}
