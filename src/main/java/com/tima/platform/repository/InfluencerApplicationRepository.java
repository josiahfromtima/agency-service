package com.tima.platform.repository;

import com.tima.platform.domain.InfluencerApplication;
import com.tima.platform.model.constant.ApplicationStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/20/23
 */
public interface InfluencerApplicationRepository extends ReactiveCrudRepository<InfluencerApplication, Integer> {
    Flux<InfluencerApplication> findAllBy(Pageable pageable);
    Flux<InfluencerApplication> findByStatus(ApplicationStatus status, Pageable pageable);
    Flux<InfluencerApplication> findByCampaignId(Integer id, Pageable pageable);
    Flux<InfluencerApplication> findBySubmittedBy(String publicId, Pageable pageable);
    Flux<InfluencerApplication> findByApplicationDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    Mono<InfluencerApplication> findByApplicationId(String appId);
}
