package com.tima.platform.resource.search;

import com.tima.platform.config.AuthTokenConfig;
import com.tima.platform.model.api.ApiResponse;
import com.tima.platform.service.search.InfluencerSearchService;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
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
public class SearchResourceHandler {
    LoggerHelper log = LoggerHelper.newInstance(SearchResourceHandler.class.getName());
    private final InfluencerSearchService searchService;
    private static final String X_FORWARD_FOR = "X-Forwarded-For";

    /**
     *  This section is the user generated signed URL
     */
    public Mono<ServerResponse> getTopCategories(ServerRequest request)  {
        log.info("Get Top Categories Requested ", request.headers().firstHeader(X_FORWARD_FOR));
        return buildServerResponse(searchService.getTopCategories());
    }

    public Mono<ServerResponse> getInfluencersByCategory(ServerRequest request)  {
        String category = request.pathVariable("category");
        log.info("Get Influencers By Category Requested ", request.headers().firstHeader(X_FORWARD_FOR));
        return buildServerResponse(searchService.getInfluencers(category));
    }

    public Mono<ServerResponse> getTopInfluencers(ServerRequest request)  {
        log.info("Get Top Influencers Requested ", request.headers().firstHeader(X_FORWARD_FOR));
        return buildServerResponse(searchService.getTopInfluencers(reportSettings(request)));
    }

    public Mono<ServerResponse> getNewInfluencers(ServerRequest request)  {
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        log.info("Get New Influencers Requested ", request.headers().firstHeader(X_FORWARD_FOR));
        return jwtAuthToken
                .map(ApiResponse::getToken)
                .map(searchService::getNewInfluencers)
                .flatMap(ApiResponse::buildServerResponse);
    }

    public Mono<ServerResponse> getInfluencer(ServerRequest request)  {
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        String influencerPublicId = request.pathVariable("publicId");
        log.info("Get Influencer Bio Requested ", request.headers().firstHeader(X_FORWARD_FOR));
        return jwtAuthToken
                .map(ApiResponse::getToken)
                .map(token -> searchService.getInfluencer(influencerPublicId, token))
                .flatMap(ApiResponse::buildServerResponse);
    }

    public Mono<ServerResponse> getInfluencerByFilter(ServerRequest request)  {
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        log.info("Get Influencer Bio Requested ", request.headers().firstHeader(X_FORWARD_FOR));
        return jwtAuthToken
                .map(ApiResponse::getToken)
                .map(token -> searchService.getInfluencerByFilter(token, searchSettingsProfile(request)))
                .flatMap(ApiResponse::buildServerResponse);
    }
}
