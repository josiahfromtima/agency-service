package com.tima.platform.repository;

import com.tima.platform.domain.Industry;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/12/23
 */
public interface IndustryRepository extends ReactiveCrudRepository<Industry, Integer> {
    Mono<Industry> findByName(String name);
}
