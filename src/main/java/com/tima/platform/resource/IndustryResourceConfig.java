package com.tima.platform.resource;

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
public class IndustryResourceConfig {
    public static final String API_V1_URL = "/v1";
    public static final String INDUSTRY_BASE = API_V1_URL + "/industries";
    public static final String INFLUENCER_CAT_BASE = API_V1_URL + "/industries/influencer";
    public static final String GET_INDUSTRY = INDUSTRY_BASE;
    public static final String UPDATE_INDUSTRY = INDUSTRY_BASE;
    public static final String DELETE_INDUSTRY = INDUSTRY_BASE + "/{name}";
    public static final String GET_CATEGORY = INFLUENCER_CAT_BASE;
    public static final String UPDATE_CATEGORY = INFLUENCER_CAT_BASE;
    public static final String DELETE_CATEGORY = INFLUENCER_CAT_BASE + "/{name}";

    @Bean
    public RouterFunction<ServerResponse> industryEndpointHandler(IndustryResourceHandler handler) {
        return route()
                .GET(GET_INDUSTRY, accept(MediaType.APPLICATION_JSON), handler::getIndustries)
                .GET(GET_CATEGORY, accept(MediaType.APPLICATION_JSON), handler::getInfluencerCategories)
                .POST(INDUSTRY_BASE, accept(MediaType.APPLICATION_JSON)
                        .and(contentType(MediaType.APPLICATION_JSON)), handler::addNewIndustry)
                .POST(INFLUENCER_CAT_BASE, accept(MediaType.APPLICATION_JSON)
                        .and(contentType(MediaType.APPLICATION_JSON)), handler::addNewInfluencerCategory)
                .PUT(UPDATE_INDUSTRY, accept(MediaType.APPLICATION_JSON)
                        .and(contentType(MediaType.APPLICATION_JSON)), handler::updateIndustry)
                .PUT(UPDATE_CATEGORY, accept(MediaType.APPLICATION_JSON)
                        .and(contentType(MediaType.APPLICATION_JSON)), handler::updateInfluencerCategory)
                .DELETE(DELETE_INDUSTRY, accept(MediaType.APPLICATION_JSON), handler::deleteIndustry)
                .DELETE(DELETE_CATEGORY, accept(MediaType.APPLICATION_JSON), handler::deleteInfluencerCategory)
                .build();
    }
}
