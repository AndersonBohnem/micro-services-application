package org.insertcoin.gatewayservice.configs;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayConfig {

    @Bean
    RouteLocator getGatewayRouter(RouteLocatorBuilder builder) {
        return builder.routes()
                .route( p -> p
                        .path("/products/**")
                        .uri("lb://product-service")
                )
                .route( p -> p
                        .path("/ws/products/**")
                        .uri("lb://product-service")
                )
                .route( p -> p
                        .path("/currency/**")
                        .uri("lb://currency-service")
                )
                .route(p -> p
                        .path("/greeting/**")
                        .uri("lb://greeting-service"))
                .route(p -> p
                        .path("/auth/**")
                        .uri("lb://auth-service"))
                .route(p -> p
                        .path("/ws/orders/**")
                        .uri("lb://order-service"))
                .build();
    }
}
