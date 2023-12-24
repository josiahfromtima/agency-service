package com.tima.platform.repository;

import com.tima.platform.domain.CampaignRegistration;
import com.tima.platform.util.CampaignSearchSetting;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/19/23
 */
public interface CampaignRegistrationRepository extends ReactiveCrudRepository<CampaignRegistration, Integer> {
    Mono<CampaignRegistration> findByPublicId(String publicId);

    @Query("SELECT * FROM campaign_registration WHERE lower(influencer_category) LIKE :category OR " +
            "lower(content_type) LIKE :type OR " +
            "planned_budget BETWEEN :lower AND :upper  OR " +
            "lower(audience_age_group) LIKE :audience OR " +
            "status = :status")
    Flux<CampaignRegistration> getSearchResult(String category, String type, BigDecimal lower,
                                               BigDecimal upper, String audience, String status);


}
