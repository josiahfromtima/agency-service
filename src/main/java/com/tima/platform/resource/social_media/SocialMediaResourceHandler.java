package com.tima.platform.resource.social_media;

import com.tima.platform.config.CustomValidator;
import com.tima.platform.model.api.ApiResponse;
import com.tima.platform.model.api.response.SocialMediaRecord;
import com.tima.platform.service.social.SocialMediaService;
import com.tima.platform.service.social.instagram.InstagramApiService;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
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
    private final CustomValidator validator;
    private static final String TOKEN_PARAM = "token";

    /**
     *  This section marks the industries activities
     */
    public Mono<ServerResponse> getAccount(ServerRequest request)  {
        String token = request.headers().firstHeader(TOKEN_PARAM);
        log.info("Get IG Account Requested", request.remoteAddress().orElse(null));
        return buildServerResponse(instagramApiService.getAccount(token));
    }
    public Mono<ServerResponse> getBusinessAccount(ServerRequest request)  {
        String token = request.headers().firstHeader(TOKEN_PARAM);
        String pageId = request.pathVariable("pageId");
        log.info("Get IG Business Account Requested", request.remoteAddress().orElse(null));
        return buildServerResponse(instagramApiService.getBusinessAccount(token, pageId));
    }
    public Mono<ServerResponse> getBusinessBasicReport(ServerRequest request)  {
        String token = request.headers().firstHeader(TOKEN_PARAM);
        String id = request.headers().firstHeader("instagram_id");
        String handle = request.pathVariable("handle");
        log.info("Get IG Account Requested", request.remoteAddress().orElse(null));
        return buildServerResponse(instagramApiService.getBusinessDiscovery(token, id, handle));
    }
    public Mono<ServerResponse> getLongLivedToken(ServerRequest request)  {
        String token = request.headers().firstHeader(TOKEN_PARAM);
        log.info("Get Long Lived Token Requested", request.remoteAddress().orElse(null));
        return instagramApiService.getLTTLAccessToken(token)
                .map(longLivedToken -> Mono.just(buildAppResponse(longLivedToken, "Graph Api")))
                .flatMap(ApiResponse::buildServerResponse);
    }

    /**
     *  This section marks the instagram social media activities
     */
    public Mono<ServerResponse> getAllSocialMediaPublic(ServerRequest request)  {
        log.info("Get All Social Media Public Requested", request.remoteAddress().orElse(null));
        return buildServerResponse(socialMediaService.getSocialMedias());
    }

    public Mono<ServerResponse> getAllSocialMediaAdmin(ServerRequest request)  {
        log.info("Get All Social Media Admin Requested", request.remoteAddress().orElse(null));
        return buildServerResponse(socialMediaService.getAllSocialMedia());
    }
    public Mono<ServerResponse> addSocialMedia(ServerRequest request)  {
        Mono<SocialMediaRecord> recordMono = request.bodyToMono(SocialMediaRecord.class)
                .doOnNext(validator::validateEntries);
        log.info("Add Social Media Requested", request.remoteAddress().orElse(null));
        return recordMono
                .map(socialMediaService::addSocialMedia)
                .flatMap(ApiResponse::buildServerResponse);
    }

    public Mono<ServerResponse> editSocialMedia(ServerRequest request)  {
        Mono<SocialMediaRecord> recordMono = request.bodyToMono(SocialMediaRecord.class);
        log.info("Edit Social Media Requested", request.remoteAddress().orElse(null));
        return recordMono
                .map(socialMediaService::editSocialMedia)
                .flatMap(ApiResponse::buildServerResponse);
    }
    public Mono<ServerResponse> removeSocialMedia(ServerRequest request)  {
        String name = request.pathVariable("name");
        log.info("Delete Social Media Requested", request.remoteAddress().orElse(null));
        return buildServerResponse(socialMediaService.deleteSocialMedia(name));
    }

}
