package com.tima.platform.repository;

import com.tima.platform.domain.ClientSocialMedia;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/25/24
 */
public interface ClientSocialMediaRepository extends ReactiveCrudRepository<ClientSocialMedia, Integer> {
    Mono<ClientSocialMedia> findByUserId(String publicId);
}
