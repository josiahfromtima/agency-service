package com.tima.platform.resource.social_media;

import com.tima.platform.config.AuthTokenConfig;
import com.tima.platform.config.CustomValidator;
import com.tima.platform.model.api.ApiResponse;
import com.tima.platform.model.api.request.ClientSelectedSocialMedia;
import com.tima.platform.model.api.response.SocialMediaRecord;
import com.tima.platform.model.constant.DemographicType;
import com.tima.platform.service.social.ClientSocialMediaService;
import com.tima.platform.service.social.SocialMediaService;
import com.tima.platform.service.social.insight.ClientBusinessInsightService;
import com.tima.platform.service.social.instagram.InstagramApiService;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static com.tima.platform.model.api.ApiResponse.buildServerResponse;
import static com.tima.platform.util.AppUtil.buildAppResponse;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/12/23
 */
@Service
@RequiredArgsConstructor
public class SocialMediaResourceHandler {
    LoggerHelper log = LoggerHelper.newInstance(SocialMediaResourceHandler.class.getName());
    private final InstagramApiService instagramApiService;
    private final SocialMediaService socialMediaService;
    private final ClientSocialMediaService mediaService;
    private final ClientBusinessInsightService insightService;
    private final CustomValidator validator;
    private static final String TOKEN_PARAM = "token";
    private static final String X_FORWARD_FOR = "X-Forwarded-For";

    /**
     *  This section marks the industries activities
     */
    public Mono<ServerResponse> getAccount(ServerRequest request)  {
        String token = request.headers().firstHeader(TOKEN_PARAM);
        log.info("Get IG Account Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return buildServerResponse(instagramApiService.getAccount(token));
    }
    public Mono<ServerResponse> getBusinessAccount(ServerRequest request)  {
        String token = request.headers().firstHeader(TOKEN_PARAM);
        String pageId = request.pathVariable("pageId");
        log.info("Get IG Business Account Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return buildServerResponse(instagramApiService.getBusinessAccount(token, pageId));
    }
    public Mono<ServerResponse> getBusinessBasicReport(ServerRequest request)  {
        String token = request.headers().firstHeader(TOKEN_PARAM);
        String handle = request.pathVariable("handle");
        log.info("Get IG Account Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return instagramApiService.getBusinessDiscovery(token, handle)
                .map(basicBusinessInsight -> Mono.just(buildAppResponse(basicBusinessInsight, "Graph Api")))
                .flatMap(ApiResponse::buildServerResponse);
    }
    public Mono<ServerResponse> getLongLivedToken(ServerRequest request)  {
        String token = request.headers().firstHeader(TOKEN_PARAM);
        log.info("Get Long Lived Token Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return instagramApiService.getLTTLAccessToken(token)
                .map(longLivedToken -> Mono.just(buildAppResponse(longLivedToken, "Graph Api")))
                .flatMap(ApiResponse::buildServerResponse);
    }

    /**
     *  This section marks the social media activities
     */
    public Mono<ServerResponse> getAllSocialMediaPublic(ServerRequest request)  {
        log.info("Get All Social Media Public Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return buildServerResponse(socialMediaService.getSocialMedias());
    }

    public Mono<ServerResponse> getAllSocialMediaAdmin(ServerRequest request)  {
        log.info("Get All Social Media Admin Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return buildServerResponse(socialMediaService.getAllSocialMedia());
    }
    public Mono<ServerResponse> addSocialMedia(ServerRequest request)  {
        Mono<SocialMediaRecord> recordMono = request.bodyToMono(SocialMediaRecord.class)
                .doOnNext(validator::validateEntries);
        log.info("Add Social Media Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return recordMono
                .map(socialMediaService::addSocialMedia)
                .flatMap(ApiResponse::buildServerResponse);
    }

    public Mono<ServerResponse> editSocialMedia(ServerRequest request)  {
        Mono<SocialMediaRecord> recordMono = request.bodyToMono(SocialMediaRecord.class);
        log.info("Edit Social Media Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return recordMono
                .map(socialMediaService::editSocialMedia)
                .flatMap(ApiResponse::buildServerResponse);
    }
    public Mono<ServerResponse> removeSocialMedia(ServerRequest request)  {
        String name = request.pathVariable("name");
        log.info("Delete Social Media Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return buildServerResponse(socialMediaService.deleteSocialMedia(name));
    }

    /**
     *  This section marks the user selected social media activities
     */
    public Mono<ServerResponse> getLinkedSocialMedia(ServerRequest request)  {
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        log.info("Get Linked Social Media accounts Requested::", request.headers().firstHeader(X_FORWARD_FOR));
        return jwtAuthToken
                .map(ApiResponse::getPublicIdFromToken)
                .map(mediaService::getUserSocialMedia)
                .flatMap(ApiResponse::buildServerResponse);
    }

    public Mono<ServerResponse> addSocialMediaAccount(ServerRequest request)  {
        String publicId = request.pathVariable("publicId");
        Mono<ClientSelectedSocialMedia> monoRecord = request.bodyToMono(ClientSelectedSocialMedia.class)
                .doOnNext(validator::validateEntries);
        log.info("Link Social Media to account Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return monoRecord
                .map(media -> mediaService.addUserSocialMedia(publicId, media))
                .flatMap(ApiResponse::buildServerResponse);
    }
    public Mono<ServerResponse> removeLinkedSocialMedia(ServerRequest request)  {
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        String name = request.pathVariable("name");
        log.info("Delete Linked Social Media Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return jwtAuthToken
                .map(ApiResponse::getPublicIdFromToken)
                .map(id -> mediaService.deleteUserSocialMedia(id, name))
                .flatMap(ApiResponse::buildServerResponse);
    }

    /**
     *  This section marks the user selected social media activities
     */
    public Mono<ServerResponse> getBasicInsight(ServerRequest request)  {
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        String mediaName = request.pathVariable("name");
        log.info("Get User Social Media insight Requested::", request.headers().firstHeader(X_FORWARD_FOR));
        return jwtAuthToken
                .map(ApiResponse::getPublicIdFromToken)
                .map(id ->  insightService.getBasicInsights(id, mediaName))
                .flatMap(ApiResponse::buildServerResponse);
    }

    public Mono<ServerResponse> getBusinessBasicInsight(ServerRequest request)  {
        String mediaName = request.pathVariable("name");
        String mediaHandle = request.queryParam("handle").orElse("");
        log.info("Get Other Business Social Media insight Requested::",
                request.headers().firstHeader(X_FORWARD_FOR));
        return buildServerResponse(insightService.getBusinessBasicInsights(mediaName, mediaHandle));
    }

    public Mono<ServerResponse> getBusinessClientInsight(ServerRequest request)  {
        String publicId = request.pathVariable("publicId");
        String mediaName = request.pathVariable("name");
        String type = request.queryParam("type").orElse(DemographicType.AGE_GENDER.name());
        log.info("Get Business Client Social Media insight Requested:: ", mediaName,
                request.headers().firstHeader(X_FORWARD_FOR));
        return buildServerResponse(insightService.getBusinessInsights(mediaName, publicId, type));
    }
    public Mono<ServerResponse> getDemographicTypes(ServerRequest request)  {
        log.info("Get insight Demographic Types Requested:: ", request.headers().firstHeader(X_FORWARD_FOR));
        return buildServerResponse(insightService.getDemographicTypes());
    }


}
