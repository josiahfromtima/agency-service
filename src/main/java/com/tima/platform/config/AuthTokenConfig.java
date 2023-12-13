package com.tima.platform.config;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/6/23
 */
public class AuthTokenConfig {
    private AuthTokenConfig() {}
    public static Mono<JwtAuthenticationToken> authenticatedToken(ServerRequest request){
        return request
                .principal()
                .cast(JwtAuthenticationToken.class);
    }
}
