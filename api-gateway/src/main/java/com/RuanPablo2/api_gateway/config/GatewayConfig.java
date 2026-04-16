package com.RuanPablo2.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.rewritePath;

@Configuration
public class GatewayConfig {

    @Bean
    public RouterFunction<ServerResponse> ticketRoute() {
        System.out.println("🚦 [API GATEWAY] Registering a route using WebMVC DSL...");

        return route("ticket-service-route")
                .route(RequestPredicates.path("/**"), http())

                .before(rewritePath("/(?<segment>.*)", "/api/${segment}"))

                .before(uri("http://localhost:8080"))
                .build();
    }
}