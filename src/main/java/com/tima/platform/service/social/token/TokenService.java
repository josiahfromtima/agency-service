package com.tima.platform.service.social.token;

import reactor.core.publisher.Mono;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 2/27/24
 */
public interface TokenService <T> {

    Mono<T> getLongLivedToken(String token);
}
