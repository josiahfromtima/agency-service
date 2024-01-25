package com.tima.platform.repository;

import com.tima.platform.domain.SocialMedia;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/25/24
 */
public interface SocialMediaRepository extends ReactiveCrudRepository<SocialMedia, Integer> {
    Mono<SocialMedia> findByName(String name);
}
