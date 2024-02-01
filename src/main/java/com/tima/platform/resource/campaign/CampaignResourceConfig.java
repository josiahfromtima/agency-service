package com.tima.platform.resource.campaign;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/20/23
 */
@Configuration
public class CampaignResourceConfig {
    public static final String API_V1_URL = "/v1";
    public static final String CAMPAIGN_BASE = API_V1_URL + "/campaigns";
    public static final String THUMBNAIL_PICTURE = CAMPAIGN_BASE + "/signed/url/thumbnail/{keyName}/{extension}";
    public static final String APPLICATION_BASE = API_V1_URL + "/applications";
    public static final String GET_TYPES = CAMPAIGN_BASE + "/segment";
    public static final String SEARCH_CAMPAIGNS = CAMPAIGN_BASE + "/search/filter";
    public static final String TOP_CAMPAIGNS = CAMPAIGN_BASE + "/search/top";
    public static final String RECOMMENDED_CAMPAIGNS = CAMPAIGN_BASE + "/search/recommendation";
    public static final String TOTAL_BUDGET_CAMPAIGNS = CAMPAIGN_BASE + "/budget/total";
    public static final String GET_CAMPAIGN = CAMPAIGN_BASE + "/{publicId}";
    public static final String GET_CAMPAIGN_BY_BRAND = CAMPAIGN_BASE + "/brand/{brand}";
    public static final String GET_CAMPAIGN_BY_STATUS = CAMPAIGN_BASE + "/search/status";
    public static final String CREATE_SEGMENT = CAMPAIGN_BASE + "/{segment}";
    public static final String GET_STATUS_TYPES = APPLICATION_BASE + "/statuses";
    public static final String GET_APPLICATION = APPLICATION_BASE + "/{applicationId}";
    public static final String GET_APPLICATION_BY_STATUS = APPLICATION_BASE + "/status/{status}";
    public static final String GET_APPLICATION_BY_STATUS_CAMPAIGN = APPLICATION_BASE + "/search/filter";
    public static final String GET_APPLICATION_BY_CAMPAIGN = APPLICATION_BASE + "/campaign/{campaignId}";
    public static final String GET_APPLICATION_BY_APPLICANT = APPLICATION_BASE + "/applicant/{applicantId}";
    public static final String GET_APPLICATION_BY_DATE = APPLICATION_BASE + "/date/{startDate}/{endDate}";
    public static final String REVIEW_APPLICATION = APPLICATION_BASE + "/review/{reviewStatus}";

    @Bean
    public RouterFunction<ServerResponse> campaignEndpointHandler(CampaignResourceHandler handler) {
        return route()
                .GET(GET_TYPES, accept(MediaType.APPLICATION_JSON), handler::getTypes)
                .GET(GET_CAMPAIGN, accept(MediaType.APPLICATION_JSON), handler::getRegistration)
                .GET(CAMPAIGN_BASE, accept(MediaType.APPLICATION_JSON), handler::getRegistrations)
                .GET(SEARCH_CAMPAIGNS, accept(MediaType.APPLICATION_JSON), handler::searchForCampaigns)
                .GET(TOP_CAMPAIGNS, accept(MediaType.APPLICATION_JSON), handler::topCampaigns)
                .GET(RECOMMENDED_CAMPAIGNS, accept(MediaType.APPLICATION_JSON), handler::recommendedCampaigns)
                .GET(THUMBNAIL_PICTURE, accept(MediaType.APPLICATION_JSON), handler::getSignedThumbnailPicture)
                .GET(TOTAL_BUDGET_CAMPAIGNS, accept(MediaType.APPLICATION_JSON), handler::getTotalBudget)
                .GET(GET_CAMPAIGN_BY_BRAND, accept(MediaType.APPLICATION_JSON), handler::getRegistrationByBrand)
                .GET(GET_CAMPAIGN_BY_STATUS, accept(MediaType.APPLICATION_JSON), handler::getRegistrationByStatus)
                .POST(CAMPAIGN_BASE, accept(MediaType.APPLICATION_JSON)
                        .and(contentType(MediaType.APPLICATION_JSON)), handler::addCampaign)
                .PUT(CREATE_SEGMENT, accept(MediaType.APPLICATION_JSON)
                        .and(contentType(MediaType.APPLICATION_JSON)), handler::addCampaignSegment)
                .PUT(CAMPAIGN_BASE, accept(MediaType.APPLICATION_JSON)
                        .and(contentType(MediaType.APPLICATION_JSON)), handler::editCampaign)
                .DELETE(GET_CAMPAIGN, accept(MediaType.APPLICATION_JSON), handler::deleteCampaign)
                .GET(GET_STATUS_TYPES, accept(MediaType.APPLICATION_JSON), handler::getStatuses)
                .GET(APPLICATION_BASE, accept(MediaType.APPLICATION_JSON), handler::getApplications)
                .GET(GET_APPLICATION, accept(MediaType.APPLICATION_JSON), handler::getApplication)
                .GET(GET_APPLICATION_BY_STATUS, accept(MediaType.APPLICATION_JSON), handler::getApplicationByStatus)
                .GET(GET_APPLICATION_BY_STATUS_CAMPAIGN, accept(MediaType.APPLICATION_JSON),
                        handler::getApplicationByStatusAndCampaign)
                .GET(GET_APPLICATION_BY_CAMPAIGN, accept(MediaType.APPLICATION_JSON),
                        handler::getApplicationByCampaignId)
                .GET(GET_APPLICATION_BY_APPLICANT, accept(MediaType.APPLICATION_JSON),
                        handler::getApplicationByInfluencers)
                .GET(GET_APPLICATION_BY_DATE, accept(MediaType.APPLICATION_JSON), handler::getApplicationByDateRange)
                .POST(APPLICATION_BASE, accept(MediaType.APPLICATION_JSON)
                        .and(contentType(MediaType.APPLICATION_JSON)), handler::addApplication)
                .PUT(REVIEW_APPLICATION, accept(MediaType.APPLICATION_JSON), handler::reviewApplication)
                .DELETE(GET_APPLICATION, accept(MediaType.APPLICATION_JSON), handler::deleteApplication)
                .build();
    }
}
