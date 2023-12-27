package com.tima.platform.service;

import com.tima.platform.converter.CampaignRegistrationConverter;
import com.tima.platform.domain.CampaignRegistration;
import com.tima.platform.model.api.AppResponse;
import com.tima.platform.repository.CampaignRegistrationRepository;
import com.tima.platform.repository.InfluencerApplicationRepository;
import com.tima.platform.repository.projection.TopCampaign;
import com.tima.platform.util.AppUtil;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.tima.platform.model.security.TimaAuthority.ADMIN_BRAND_INFLUENCER;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/27/23
 */
@Service
@RequiredArgsConstructor
public class CampaignAggregateService {
    private final LoggerHelper log = LoggerHelper.newInstance(CampaignRegistrationService.class.getName());
    private final CampaignRegistrationRepository registrationRepository;
    private final InfluencerApplicationRepository applicationRepository;

    private static final String CAMPAIGN_MSG = "Campaign request executed successfully";
    private static final int TOP_SIZE = 3;

    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> getTopRegistrations() {
        log.info("Get Top Campaigns  ");
        return getTopCampaigns()
                .collectList()
                .map(this::getTopRegistrationList)
                .flatMap(Flux::collectList)
                .map(CampaignRegistrationConverter::mapToRecords)
                .map(campaignRecords -> AppUtil.buildAppResponse(campaignRecords, CAMPAIGN_MSG));
    }



    private Flux<TopCampaign> getTopCampaigns() {
        return applicationRepository.getTopCampaign(TOP_SIZE, TopCampaign.class);
    }

    private Flux<CampaignRegistration> getTopRegistrationList(List<TopCampaign> topCampaigns) {
        return registrationRepository
                .findByIdIn(getIds(topCampaigns));
    }

    private List<Integer> getIds(List<TopCampaign> topCampaigns) {
        return topCampaigns.stream()
                .map(TopCampaign::campaign)
                .toList();
    }
}