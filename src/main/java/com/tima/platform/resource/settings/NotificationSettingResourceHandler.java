package com.tima.platform.resource.settings;

import com.fasterxml.jackson.databind.JsonNode;
import com.tima.platform.config.AuthTokenConfig;
import com.tima.platform.config.CustomValidator;
import com.tima.platform.model.api.ApiResponse;
import com.tima.platform.model.api.response.CampaignAudienceRecord;
import com.tima.platform.model.api.response.CampaignCreativeRecord;
import com.tima.platform.service.CampaignAudienceService;
import com.tima.platform.service.CampaignCreativeService;
import com.tima.platform.service.NotificationSettingService;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static com.tima.platform.model.api.ApiResponse.buildServerResponse;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/19/23
 */
@Service
@RequiredArgsConstructor
public class NotificationSettingResourceHandler {
    LoggerHelper log = LoggerHelper.newInstance(NotificationSettingResourceHandler.class.getName());
    private final NotificationSettingService settingService;
    private final CampaignAudienceService audienceService;
    private final CampaignCreativeService creativeService;
    private final CustomValidator validator;

    /**
     *  This section marks the notification setting activities
     */
    public Mono<ServerResponse> updateSettings(ServerRequest request)  {
        Mono<JsonNode> recordMono = request.bodyToMono(JsonNode.class);
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        log.info("Update Notification Settings Requested", request.remoteAddress().orElse(null));
        return jwtAuthToken
                .map(ApiResponse::getPublicIdFromToken)
                .map(publicId ->  recordMono.flatMap(restRecord ->
                        settingService.requestNotificationSettingsUpdate(publicId, restRecord)) )
                .flatMap(ApiResponse::buildServerResponse);
    }

    /**
     *  This section marks the campaign audience setting activities
     */
    public Mono<ServerResponse> getCampaignAudiences(ServerRequest request)  {
        log.info("Get Available Campaign Audience Requested", request.remoteAddress().orElse(null));
        return buildServerResponse(audienceService.getCampaignAudiences());
    }

    public Mono<ServerResponse> updateCampaignAudiences(ServerRequest request)  {
        Mono<CampaignAudienceRecord> recordMono = request.bodyToMono(CampaignAudienceRecord.class)
                .doOnNext(validator::validateEntries);
        log.info("Create or Edit Campaign Audience Requested", request.remoteAddress().orElse(null));
        return recordMono
                .map(audienceService::addOrUpdateCampaignAudience)
                .flatMap(ApiResponse::buildServerResponse);
    }

    public Mono<ServerResponse> deleteCampaignAudiences(ServerRequest request)  {
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        log.info("Delete Campaign Audience Requested", request.remoteAddress().orElse(null));
        return jwtAuthToken
                .map(ApiResponse::getPublicIdFromToken)
                .map(audienceService::deleteCampaignAudience)
                .flatMap(ApiResponse::buildServerResponse);
    }/**
     *  This section marks the campaign creative setting activities
     */
    public Mono<ServerResponse> getCampaignCreatives(ServerRequest request)  {
        log.info("Get Available Campaign Creative Requested", request.remoteAddress().orElse(null));
        return buildServerResponse(creativeService.getCampaignCreatives());
    }

    public Mono<ServerResponse> updateCampaignCreatives(ServerRequest request)  {
        Mono<CampaignCreativeRecord> recordMono = request.bodyToMono(CampaignCreativeRecord.class)
                .doOnNext(validator::validateEntries);
        log.info("Create or Edit Campaign Creative Requested", request.remoteAddress().orElse(null));
        return recordMono
                .map(creativeService::addOrUpdateCampaignCreative)
                .flatMap(ApiResponse::buildServerResponse);
    }

    public Mono<ServerResponse> deleteCampaignCreatives(ServerRequest request)  {
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        log.info("Delete Campaign Creative Requested", request.remoteAddress().orElse(null));
        return jwtAuthToken
                .map(ApiResponse::getPublicIdFromToken)
                .map(creativeService::deleteCampaignCreative)
                .flatMap(ApiResponse::buildServerResponse);
    }
}
