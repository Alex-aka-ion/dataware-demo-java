package com.example.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayConfig {

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("product-service", r -> r.path("/api/products/**")
                       // .uri("http://localhost:8081"))
                        .uri("http://product-service:8081"))
                .route("order-service", r -> r.path("/api/orders/**")
                      //  .uri("http://localhost:8082"))
                        .uri("http://order-service:8082"))
                .build();
    }
}
