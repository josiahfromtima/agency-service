package com.tima.platform.repository;

import com.tima.platform.domain.CampaignRegistration;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static com.tima.platform.repository.projection.NativeSql.CAMPAIGN_SEARCH_WITH_FILTER;
import static com.tima.platform.repository.projection.NativeSql.RECOMMENDED_CAMPAIGN_STATEMENT;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/19/23
 */
public interface CampaignRegistrationRepository extends ReactiveCrudRepository<CampaignRegistration, Integer> {
    Mono<CampaignRegistration> findByPublicId(String publicId);

    Flux<CampaignRegistration> findAllBy(Pageable pageable);

    Flux<CampaignRegistration> findByEndDateAfterOrEndDate(LocalDate today, LocalDate thisDay);
    Flux<CampaignRegistration> findByIdIn(List<Integer> ids);
    Flux<CampaignRegistration> findByBrandName(String name, Pageable pageable);
    Flux<CampaignRegistration> findByStatusLessThanEqual(short status, Pageable pageable);
    Flux<CampaignRegistration> findByBrandNameAndStatusLessThanEqual(String name, short status, Pageable pageable);

    Flux<CampaignRegistration> findByCreatedBy(String publicId);
    @Query(RECOMMENDED_CAMPAIGN_STATEMENT)
    Flux<CampaignRegistration> getRecommendedCampaign(String param1, String param2, String param3);

    @Query(CAMPAIGN_SEARCH_WITH_FILTER)
    Flux<CampaignRegistration> getSearchResult(String category, String size, String age, String location);


}
