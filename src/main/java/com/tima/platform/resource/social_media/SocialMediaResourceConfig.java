package com.tima.platform.resource.social_media;

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
 * @Date: 12/12/23
 */
@Configuration
public class SocialMediaResourceConfig {
    public static final String API_V1_URL = "/v1";
    public static final String SM_BASE = API_V1_URL + "/social-media";
    public static final String SM_USER = SM_BASE + "/linked";
    public static final String DELETE_SM_USER = SM_BASE + "/linked/{name}";
    public static final String SM_USER_ADD_EDIT = SM_USER + "/user/{publicId}";
    public static final String GET_SM = API_V1_URL + "/social-media/_public";
    public static final String DELETE_SM = API_V1_URL + "/social-media/{name}";
    public static final String GET_SM_INSIGHT = SM_BASE + "/{name}/insight";
    public static final String GET_SM_BIZ_INSIGHT = SM_BASE + "/{name}/insight/explore";
    public static final String IG_SM_BASE = API_V1_URL + "/meta";
    public static final String IG_SM_ACCOUNT = IG_SM_BASE + "/account";
    public static final String IG_SM_BA = IG_SM_BASE + "/ig-account/{pageId}";
    public static final String IG_SM_BUSINESS_DISCOVERY = IG_SM_BASE + "/business/{handle}";
    public static final String IG_LONG_LIVED_TOKEN = IG_SM_BASE + "/generate-token";

    @Bean
    public RouterFunction<ServerResponse> socialEndpointHandler(SocialMediaResourceHandler handler) {
        return route()
                .GET(IG_SM_ACCOUNT, accept(MediaType.APPLICATION_JSON), handler::getAccount)
                .GET(IG_SM_BA, accept(MediaType.APPLICATION_JSON), handler::getBusinessAccount)
                .GET(IG_SM_BUSINESS_DISCOVERY, accept(MediaType.APPLICATION_JSON), handler::getBusinessBasicReport)
                .GET(IG_LONG_LIVED_TOKEN, accept(MediaType.APPLICATION_JSON), handler::getLongLivedToken)
                .GET(GET_SM, accept(MediaType.APPLICATION_JSON), handler::getAllSocialMediaPublic)
                .GET(GET_SM_INSIGHT, accept(MediaType.APPLICATION_JSON), handler::getBasicInsight)
                .GET(GET_SM_BIZ_INSIGHT, accept(MediaType.APPLICATION_JSON), handler::getBusinessBasicInsight)
                .GET(SM_BASE, accept(MediaType.APPLICATION_JSON), handler::getAllSocialMediaAdmin)
                .POST(SM_BASE, accept(MediaType.APPLICATION_JSON)
                        .and(contentType(MediaType.APPLICATION_JSON)), handler::addSocialMedia)
                .PUT(SM_BASE, accept(MediaType.APPLICATION_JSON)
                        .and(contentType(MediaType.APPLICATION_JSON)), handler::editSocialMedia)
                .DELETE(DELETE_SM, accept(MediaType.APPLICATION_JSON), handler::removeSocialMedia)
                .GET(SM_USER, accept(MediaType.APPLICATION_JSON), handler::getLinkedSocialMedia)
                .PUT(SM_USER_ADD_EDIT, accept(MediaType.APPLICATION_JSON)
                        .and(contentType(MediaType.APPLICATION_JSON)), handler::addSocialMediaAccount)
                .DELETE(DELETE_SM_USER, accept(MediaType.APPLICATION_JSON), handler::removeLinkedSocialMedia)
                .build();
    }
}
