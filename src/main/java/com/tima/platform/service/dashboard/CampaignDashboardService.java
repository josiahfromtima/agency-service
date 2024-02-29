package com.tima.platform.service.dashboard;

import com.tima.platform.model.api.AppResponse;
import com.tima.platform.model.api.request.analytics.MainKpi;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.List;

import static com.tima.platform.model.security.TimaAuthority.ADMIN_BRAND_INFLUENCER;
import static com.tima.platform.util.AppUtil.buildAppResponse;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 2/21/24
 */
@Service
@RequiredArgsConstructor
public class CampaignDashboardService {
    private final LoggerHelper log = LoggerHelper.newInstance(CampaignDashboardService.class.getName());
    private final CampaignAnalyticsKpiService kpiService;
    private final CampaignAnalyticsInteractionService interactionService;
    private final CampaignAnalyticsAudienceService audienceService;

    private static final String CAMPAIGN_MSG = "Campaign analytic dashboard request executed successfully";

    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> getKpi(String publicId) {
        log.info("Get MainKpi for campaign ", publicId);
        return getAccountsAndFollowers(publicId)
                .flatMap(this::buildKpi)
                .map(mainKpi -> buildAppResponse(mainKpi, CAMPAIGN_MSG));
    }

    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> getInteractionSummary(String publicId) {
        log.info("Get Interaction Summary for campaign ", publicId);
        return interactionService.getSummary(publicId)
                .map(summary -> buildAppResponse(summary, CAMPAIGN_MSG));

    }

    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> getDistributionSummary(String publicId) {
        log.info("Get Distribution Summary for campaign ", publicId);
        return audienceService.getDistributionSummary(publicId)
                .map(summary -> buildAppResponse(summary, CAMPAIGN_MSG));
    }

    private Mono<Tuple2<List<String>, Long>> getAccountsAndFollowers(String publicId) {
        return Mono.zip(kpiService.getAccounts(publicId), kpiService.getFollowers(publicId));
    }

    private Mono<MainKpi> buildKpi(Tuple2<List<String>, Long> kpis) {
        return Mono.just(
                MainKpi.builder()
                        .accounts(kpis.getT1().size())
                        .followers(kpis.getT2())
                        .build()
        );
    }
}
