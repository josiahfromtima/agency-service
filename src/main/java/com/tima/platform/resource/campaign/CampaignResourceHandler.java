package com.tima.platform.resource.campaign;

import com.fasterxml.jackson.databind.JsonNode;
import com.tima.platform.config.AuthTokenConfig;
import com.tima.platform.config.CustomValidator;
import com.tima.platform.model.api.ApiResponse;
import com.tima.platform.model.api.response.CampaignRegistrationRecord;
import com.tima.platform.model.api.response.InfluencerApplicationRecord;
import com.tima.platform.model.constant.RegistrationType;
import com.tima.platform.service.CampaignAggregateService;
import com.tima.platform.service.CampaignRegistrationService;
import com.tima.platform.service.InfluencerApplicationService;
import com.tima.platform.service.aws.s3.AwsS3Service;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static com.tima.platform.model.api.ApiResponse.*;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/20/23
 */
@Service
@RequiredArgsConstructor
public class CampaignResourceHandler {
    LoggerHelper log = LoggerHelper.newInstance(CampaignResourceHandler.class.getName());
    private final AwsS3Service awsS3Service;
    private final CampaignRegistrationService registrationService;
    private final InfluencerApplicationService applicationService;
    private final CampaignAggregateService aggregateService;
    private final CustomValidator validator;

    @Value("${aws.s3.resource.thumbnail}")
    private String thumbnailFolder;

    private static final String X_FORWARD_FOR = "X-Forwarded-For";

    /**
     *  This section is the user generated signed URL
     */
    public Mono<ServerResponse> getSignedThumbnailPicture(ServerRequest request)  {
        String keyName = request.pathVariable("keyName");
        String extension = request.pathVariable("extension");
        log.info("Get Signed Thumbnail Picture URL Requested ", request.headers().firstHeader(X_FORWARD_FOR));
        return buildServerResponse(awsS3Service.getSignedUrl(thumbnailFolder, keyName, extension));
    }

    /**
     *  This section marks the campaign registration activities
     */
    public Mono<ServerResponse> getTypes(ServerRequest request)  {
        log.info("Get Available Registration Types Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return buildServerResponse(registrationService.getRegistrationTypes());
    }

    public Mono<ServerResponse> getRegistrations(ServerRequest request)  {
        log.info("Get Registered Campaigns Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return buildServerResponse(registrationService.getCampaignRegistrations(reportSettings(request)));
    }

    public Mono<ServerResponse> getRegistration(ServerRequest request)  {
        String publicId = request.pathVariable("publicId");
        log.info("Get Registered Campaign Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return buildServerResponse(registrationService.getCampaignRegistration(publicId));
    }

    public Mono<ServerResponse> getRegistrationByBrand(ServerRequest request)  {
        String brandName = request.pathVariable("brand");
        log.info("Get Registered Campaign By Brand Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return buildServerResponse(
                registrationService.getCampaignRegistrationByBrand(brandName, reportSettings(request)));
    }

    public Mono<ServerResponse> addCampaign(ServerRequest request)  {
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        Mono<CampaignRegistrationRecord> recordMono = request.bodyToMono(CampaignRegistrationRecord.class)
                .doOnNext(validator::validateEntries)
                .doOnNext(crRecord -> validator.validateEntries(crRecord.overview()))
                .doOnNext(crRecord -> validator.validateEntries(crRecord.influencer()))
                .doOnNext(crRecord -> validator.validateEntries(crRecord.creative()));
        log.info("Create Campaign (Full) Requested  ", request.headers().firstHeader(X_FORWARD_FOR), " ", request.headers());
        return jwtAuthToken
                .map(ApiResponse::getPublicIdFromToken)
                .flatMap(id -> recordMono
                        .map(registration ->  registrationService.addCampaignRegistration(registration, id))
                ).flatMap(ApiResponse::buildServerResponse);
    }
    public Mono<ServerResponse> addCampaignSegment(ServerRequest request)  {
        Mono<JsonNode> recordMono = request.bodyToMono(JsonNode.class);
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        String type = request.pathVariable("segment");
        log.info("Create Campaign (Part) Requested ", request.headers().firstHeader(X_FORWARD_FOR), " ", type);
        return jwtAuthToken
                .map(ApiResponse::getPublicIdFromToken)
                .flatMap(id -> recordMono
                        .map(jsonNode -> registrationService.addCampaignRegistration(
                                jsonNode, RegistrationType.valueOf(type), id) )
                ).flatMap(ApiResponse::buildServerResponse);
    }

    public Mono<ServerResponse> editCampaign(ServerRequest request)  {
        Mono<CampaignRegistrationRecord> recordMono = request.bodyToMono(CampaignRegistrationRecord.class).doOnNext(validator::validateEntries)
                .doOnNext(crRecord -> validator.validateEntries(crRecord.overview()))
                .doOnNext(crRecord -> validator.validateEntries(crRecord.influencer()))
                .doOnNext(crRecord -> validator.validateEntries(crRecord.creative()));
        log.info("Edit Campaign (Full) Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return recordMono
                .map(registrationService::updateCampaignRegistration)
                .flatMap(ApiResponse::buildServerResponse);
    }
    public Mono<ServerResponse> deleteCampaign(ServerRequest request)  {
        String publicId = request.pathVariable("publicId");
        log.info("Delete Campaign (Full) Requested", publicId, request.headers().firstHeader(X_FORWARD_FOR));
        return buildServerResponse(registrationService.deleteCampaignRegistration(publicId));
    }

    public Mono<ServerResponse> searchForCampaigns(ServerRequest request)  {
        log.info("Search for Campaigns Requested ", request.headers().firstHeader(X_FORWARD_FOR));
        return buildServerResponse(registrationService.getRegistrationsBySearch(searchSettings(request)));
    }

    public Mono<ServerResponse> topCampaigns(ServerRequest request)  {
        log.info("Get Top Campaigns Requested ", request.headers().firstHeader(X_FORWARD_FOR));
        return buildServerResponse(aggregateService.getTopRegistrations());
    }

    public Mono<ServerResponse> recommendedCampaigns(ServerRequest request)  {
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        log.info("Get Recommended Campaigns Requested ", request.headers().firstHeader(X_FORWARD_FOR));
        return jwtAuthToken
                .map(ApiResponse::getPublicIdFromToken)
                .map(aggregateService::getRecommendedRegistrations)
                .flatMap(ApiResponse::buildServerResponse);
    }

    public Mono<ServerResponse> getTotalBudget(ServerRequest request)  {
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        log.info("Get Total Budget Requested ", request.headers().firstHeader(X_FORWARD_FOR));
        return jwtAuthToken
                .map(ApiResponse::getPublicIdFromToken)
                .map(aggregateService::getRegistrationBudgetByUserId)
                .flatMap(ApiResponse::buildServerResponse);
    }

    /**
     *  This section marks the influencer campaign application activities
     */
    public Mono<ServerResponse> getStatuses(ServerRequest request)  {
        log.info("Get Application Statuses Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return buildServerResponse(applicationService.getApplicationStatuses());
    }

    public Mono<ServerResponse> getApplications(ServerRequest request)  {
        log.info("Get Influencer Applications Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return buildServerResponse(applicationService.getApplications(reportSettings(request)));
    }

    public Mono<ServerResponse> getApplication(ServerRequest request)  {
        String applicationId = request.pathVariable("applicationId");
        log.info("Get Influencer Application Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return buildServerResponse(applicationService.getApplication(applicationId));
    }

    public Mono<ServerResponse> getApplicationByStatus(ServerRequest request)  {
        String status = request.pathVariable("status");
        log.info("Get Influencer Application By Status Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return buildServerResponse(applicationService.getApplicationsByStatus(status, reportSettings(request)) );
    }
    public Mono<ServerResponse> getApplicationByStatusAndCampaign(ServerRequest request)  {
        String status = request.queryParam("status").orElse("PENDING");
        String campaignId = request.queryParam("campaignId").orElse("");
        log.info("Get Influencer Application By Status and Campaign Requested",
                request.headers().firstHeader(X_FORWARD_FOR));
        return buildServerResponse(
                applicationService.getApplicationsByStatusAndCampaign(status, campaignId, reportSettings(request)) );
    }

    public Mono<ServerResponse> getApplicationByCampaignId(ServerRequest request)  {
        String campaignId = request.pathVariable("campaignId");
        log.info("Get Application By Campaign Public Id Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return buildServerResponse(applicationService.getApplicationsByCampaignId(campaignId, reportSettings(request)) );
    }

    public Mono<ServerResponse> getApplicationByInfluencers(ServerRequest request)  {
        String applicantId = request.pathVariable("applicantId");
        log.info("Get Application By Influencer Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return buildServerResponse(applicationService.getApplicationsByApplicant(applicantId, reportSettings(request)) );
    }

    public Mono<ServerResponse> getApplicationByDateRange(ServerRequest request)  {
        String startDate = request.pathVariable("startDate");
        String endDate = request.pathVariable("endDate");
        log.info("Get Application By Date Range Requested", startDate, " - ", endDate);
        return buildServerResponse(
                applicationService.getApplicationsByDateRange(startDate, endDate, reportSettings(request)) );
    }

    public Mono<ServerResponse> addApplication(ServerRequest request)  {
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        Mono<InfluencerApplicationRecord> recordMono = request.bodyToMono(InfluencerApplicationRecord.class)
                .doOnNext(validator::validateEntries);
        log.info("Add New Application Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return jwtAuthToken
                .map(ApiResponse::getJwtRecord)
                .flatMap(jwt -> recordMono.map(applicationRecord ->
                        applicationService.addApplication(applicationRecord, jwt.publicId(), jwt.token())))
                .flatMap(ApiResponse::buildServerResponse);
    }

    public Mono<ServerResponse> reviewApplication(ServerRequest request)  {
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        String appId = request.queryParam("applicationId").orElse("");
        String reviewStatus = request.pathVariable("reviewStatus");
        log.info("Review Application Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return jwtAuthToken
                .map(ApiResponse::getPublicIdFromToken)
                .map(publicId -> applicationService.reviewApplication (appId, publicId, reviewStatus))
                .flatMap(ApiResponse::buildServerResponse);
    }

    public Mono<ServerResponse> deleteApplication(ServerRequest request)  {
        String applicationId = request.pathVariable("applicationId");
        log.info("Delete Application Requested", applicationId, request.headers().firstHeader(X_FORWARD_FOR));
        return buildServerResponse(applicationService.deleteApplication(applicationId));
    }
}
