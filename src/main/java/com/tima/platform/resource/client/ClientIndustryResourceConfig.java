package com.tima.platform.resource.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/26/23
 */
@Configuration
public class ClientIndustryResourceConfig {
    public static final String API_V1_URL = "/v1";
    public static final String CLIENT_BASE = API_V1_URL + "/user/industry";

    @Bean
    public RouterFunction<ServerResponse> clientEndpointHandler(ClientIndustryResourceHandler handler) {
        return route()
                .PUT(CLIENT_BASE, accept(MediaType.APPLICATION_JSON)
                        .and(contentType(MediaType.APPLICATION_JSON)), handler::updateIndustry)
                .DELETE(CLIENT_BASE, accept(MediaType.APPLICATION_JSON), handler::deleteIndustry)
                .GET(CLIENT_BASE, accept(MediaType.APPLICATION_JSON), handler::getClientIndustries)
                .build();
    }
}
