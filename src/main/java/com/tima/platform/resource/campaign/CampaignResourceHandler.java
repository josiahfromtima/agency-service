package com.tima.platform.resource.campaign;

import com.fasterxml.jackson.databind.JsonNode;
import com.tima.platform.config.AuthTokenConfig;
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

    @Value("${aws.s3.resource.thumbnail}")
    private String thumbnailFolder;

    /**
     *  This section is the user generated signed URL
     */
    public Mono<ServerResponse> getSignedThumbnailPicture(ServerRequest request)  {
        String keyName = request.pathVariable("keyName");
        log.info("Get Signed Thumbnail Picture URL Requested ", request.remoteAddress().orElse(null));
        return buildServerResponse(awsS3Service.getSignedUrl(thumbnailFolder, keyName));
    }

    /**
     *  This section marks the campaign registration activities
     */
    public Mono<ServerResponse> getTypes(ServerRequest request)  {
        log.info("Get Available Registration Types Requested", request.remoteAddress().orElse(null));
        return buildServerResponse(registrationService.getRegistrationTypes());
    }

    public Mono<ServerResponse> getRegistrations(ServerRequest request)  {
        log.info("Get Registered Campaigns Requested", request.remoteAddress().orElse(null));
        return buildServerResponse(registrationService.getCampaignRegistrations());
    }

    public Mono<ServerResponse> getRegistration(ServerRequest request)  {
        String publicId = request.pathVariable("publicId");
        log.info("Get Registered Campaign Requested", request.remoteAddress().orElse(null));
        return buildServerResponse(registrationService.getCampaignRegistration(publicId));
    }

    public Mono<ServerResponse> addCampaign(ServerRequest request)  {
        Mono<CampaignRegistrationRecord> recordMono = request.bodyToMono(CampaignRegistrationRecord.class);
        log.info("Create Campaign (Full) Requested", request.remoteAddress().orElse(null));
        return recordMono
                .map(registrationService::addCampaignRegistration)
                .flatMap(ApiResponse::buildServerResponse);
    }
    public Mono<ServerResponse> addCampaignSegment(ServerRequest request)  {
        Mono<JsonNode> recordMono = request.bodyToMono(JsonNode.class);
        String type = request.pathVariable("segment");
        log.info("Create Campaign (Part) Requested ", request.remoteAddress().orElse(null), " ", type);
        return recordMono
                .map(jsonNode -> registrationService.addCampaignRegistration(jsonNode, RegistrationType.valueOf(type)))
                .flatMap(ApiResponse::buildServerResponse);
    }

    public Mono<ServerResponse> editCampaign(ServerRequest request)  {
        Mono<CampaignRegistrationRecord> recordMono = request.bodyToMono(CampaignRegistrationRecord.class);
        log.info("Edit Campaign (Full) Requested", request.remoteAddress().orElse(null));
        return recordMono
                .map(registrationService::updateCampaignRegistration)
                .flatMap(ApiResponse::buildServerResponse);
    }
    public Mono<ServerResponse> deleteCampaign(ServerRequest request)  {
        String publicId = request.pathVariable("publicId");
        log.info("Delete Campaign (Full) Requested", publicId, request.remoteAddress().orElse(null));
        return buildServerResponse(registrationService.deleteCampaignRegistration(publicId));
    }

    public Mono<ServerResponse> searchForCampaigns(ServerRequest request)  {
        log.info("Search for Campaigns Requested ", request.remoteAddress().orElse(null));
        return buildServerResponse(registrationService.getRegistrationsBySearch(searchSettings(request)));
    }

    public Mono<ServerResponse> topCampaigns(ServerRequest request)  {
        log.info("Get Top Campaigns Requested ", request.remoteAddress().orElse(null));
        return buildServerResponse(aggregateService.getTopRegistrations());
    }

    public Mono<ServerResponse> recommendedCampaigns(ServerRequest request)  {
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        log.info("Get Recommended Campaigns Requested ", request.remoteAddress().orElse(null));
        return jwtAuthToken
                .map(ApiResponse::getPublicIdFromToken)
                .map(aggregateService::getRecommendedRegistrations)
                .flatMap(ApiResponse::buildServerResponse);
    }

    /**
     *  This section marks the influencer campaign application activities
     */
    public Mono<ServerResponse> getStatuses(ServerRequest request)  {
        log.info("Get Application Statuses Requested", request.remoteAddress().orElse(null));
        return buildServerResponse(applicationService.getApplicationStatuses());
    }

    public Mono<ServerResponse> getApplications(ServerRequest request)  {
        log.info("Get Influencer Applications Requested", request.remoteAddress().orElse(null));
        return buildServerResponse(applicationService.getApplications(reportSettings(request)));
    }

    public Mono<ServerResponse> getApplication(ServerRequest request)  {
        String applicationId = request.pathVariable("applicationId");
        log.info("Get Influencer Application Requested", request.remoteAddress().orElse(null));
        return buildServerResponse(applicationService.getApplication(applicationId));
    }

    public Mono<ServerResponse> getApplicationByStatus(ServerRequest request)  {
        String status = request.pathVariable("status");
        log.info("Get Influencer Application By Status Requested", request.remoteAddress().orElse(null));
        return buildServerResponse(applicationService.getApplicationsByStatus(status, reportSettings(request)) );
    }

    public Mono<ServerResponse> getApplicationByCampaignId(ServerRequest request)  {
        String campaignId = request.pathVariable("campaignId");
        log.info("Get Application By Campaign Public Id Requested", request.remoteAddress().orElse(null));
        return buildServerResponse(applicationService.getApplicationsByCampaignId(campaignId, reportSettings(request)) );
    }

    public Mono<ServerResponse> getApplicationByInfluencers(ServerRequest request)  {
        String applicantId = request.pathVariable("applicantId");
        log.info("Get Application By Influencer Requested", request.remoteAddress().orElse(null));
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
        Mono<InfluencerApplicationRecord> recordMono = request.bodyToMono(InfluencerApplicationRecord.class);
        log.info("Add New Application Requested", request.remoteAddress().orElse(null));
        return jwtAuthToken
                .map(ApiResponse::getJwtRecord)
                .flatMap(jwt -> recordMono.map(applicationRecord ->
                        applicationService.addApplication(applicationRecord, jwt.publicId(), jwt.token())))
                .flatMap(ApiResponse::buildServerResponse);
    }

    public Mono<ServerResponse> reviewApplication(ServerRequest request)  {
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        Mono<InfluencerApplicationRecord> recordMono = request.bodyToMono(InfluencerApplicationRecord.class);
        String reviewStatus = request.pathVariable("reviewStatus");
        log.info("Review Application Requested", request.remoteAddress().orElse(null));
        return jwtAuthToken
                .map(ApiResponse::getPublicIdFromToken)
                .flatMap(publicId -> recordMono.map(applicationRecord ->
                        applicationService.reviewApplication (applicationRecord, publicId, reviewStatus)))
                .flatMap(ApiResponse::buildServerResponse);
    }

    public Mono<ServerResponse> deleteApplication(ServerRequest request)  {
        String applicationId = request.pathVariable("applicationId");
        log.info("Delete Application Requested", applicationId, request.remoteAddress().orElse(null));
        return buildServerResponse(applicationService.deleteApplication(applicationId));
    }
}
