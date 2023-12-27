package com.tima.platform.resource.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.tima.platform.config.AuthTokenConfig;
import com.tima.platform.model.api.ApiResponse;
import com.tima.platform.model.api.response.ClientIndustryRecord;
import com.tima.platform.service.ClientIndustryService;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/26/23
 */
@Service
@RequiredArgsConstructor
public class ClientIndustryResourceHandler {
    LoggerHelper log = LoggerHelper.newInstance(ClientIndustryResourceHandler.class.getName());

    private final ClientIndustryService industryService;

    /**
     *  This section marks the industries activities
     */
    public Mono<ServerResponse> getClientIndustries(ServerRequest request)  {
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        log.info("Get Client Industries Requested", request.remoteAddress().orElse(null));
        return jwtAuthToken
                .map(ApiResponse::getPublicIdFromToken)
                .map(industryService::getIndustries)
                .flatMap(ApiResponse::buildServerResponse);
    }

    public Mono<ServerResponse> addClientIndustry(ServerRequest request)  {
        Mono<ClientIndustryRecord> recordMono = request.bodyToMono(ClientIndustryRecord.class);
        log.info("Registered new client industry Requested", request.remoteAddress().orElse(null));
        return  recordMono
                .map(industryService::addClientIndustry)
                .flatMap(ApiResponse::buildServerResponse);
    }
    public Mono<ServerResponse> updateIndustry(ServerRequest request)  {
        Mono<JsonNode> recordMono = request.bodyToMono(JsonNode.class);
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        log.info("Update Client industry Requested", request.remoteAddress().orElse(null));
        return jwtAuthToken
                .map(ApiResponse::getPublicIdFromToken)
                .flatMap(publicId -> recordMono.map(jsonNode ->
                        industryService.updateClientIndustry(jsonNode, publicId)))
                .flatMap(ApiResponse::buildServerResponse);
    }
    public Mono<ServerResponse> deleteIndustry(ServerRequest request)  {
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        log.info("Delete Industry Requested", request.remoteAddress().orElse(null));
        return jwtAuthToken
                .map(ApiResponse::getPublicIdFromToken)
                .map(industryService::deleteClientIndustry)
                .flatMap(ApiResponse::buildServerResponse);
    }
}
