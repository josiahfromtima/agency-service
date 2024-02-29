package com.tima.platform.repository;

import com.tima.platform.domain.InfluencerApplication;
import com.tima.platform.model.constant.ApplicationStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static com.tima.platform.repository.projection.NativeSql.TOP_CAMPAIGN_STATEMENT;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/20/23
 */
public interface InfluencerApplicationRepository extends ReactiveCrudRepository<InfluencerApplication, Integer> {
    Flux<InfluencerApplication> findAllBy(Pageable pageable);
    Flux<InfluencerApplication> findByStatus(ApplicationStatus status, Pageable pageable);
    Flux<InfluencerApplication> findByStatusAndCampaignPublicId(ApplicationStatus status, String id, Pageable pageable);
    Flux<InfluencerApplication> findByStatusAndCampaignPublicId(ApplicationStatus status, String id);
    Flux<InfluencerApplication> findByCampaignPublicId(String campaignId);
    Flux<InfluencerApplication> findByCampaignId(Integer id, Pageable pageable);
    Flux<InfluencerApplication> findBySubmittedBy(String publicId, Pageable pageable);
    Flux<InfluencerApplication> findBySubmittedBy(String publicId);
    Flux<InfluencerApplication> findByApplicationDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
    Mono<InfluencerApplication> findByApplicationId(String appId);
    Flux<InfluencerApplication> findByFullNameContainingIgnoreCase(String name, Pageable pageable);


    @Query(TOP_CAMPAIGN_STATEMENT)
    <T> Flux<T> getTopCampaign(int top, Class<T> type);
}
