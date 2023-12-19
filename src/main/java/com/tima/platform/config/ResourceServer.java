package com.tima.platform.config;

import com.tima.platform.config.helper.CombinedClaimConverter;
import com.tima.platform.exception.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/6/23
 */
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class ResourceServer {
    private final CombinedClaimConverter converter;
    @Bean
    public SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) throws AppException {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(authorizeExchangeSpec ->
                        authorizeExchangeSpec
                                .pathMatchers(AUTH_WHITELIST)
                                .permitAll()
                                .anyExchange()
                                .authenticated()
                )
                .oauth2ResourceServer(oAuth2 -> oAuth2.jwt(jwtSpec ->
                        jwtSpec.jwtAuthenticationConverter(converter)));
        return http.build();
    }

    private static final String[] AUTH_WHITELIST = {
            "/v1/industries",
            "/v1/industries/influencer",
            "/v1/settings/audience",
            "/v1/settings/creative",
            // other public endpoints of your API may be appended to this array
    };
}
