package com.tima.platform.repository;

import com.tima.platform.domain.ClientIndustry;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/26/23
 */
public interface ClientIndustryRepository extends ReactiveCrudRepository<ClientIndustry, Integer> {
    Mono<ClientIndustry> findByUserPublicId(String publicId);
}
