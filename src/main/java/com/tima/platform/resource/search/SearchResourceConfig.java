package com.tima.platform.resource.search;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/20/23
 */
@Configuration
public class SearchResourceConfig {
    public static final String API_V1_URL = "/v1";
    public static final String SEARCH_BASE = API_V1_URL + "/influencer/search";
    public static final String TOP_CATEGORIES = SEARCH_BASE + "/categories";
    public static final String INFLUENCER_CATEGORY = SEARCH_BASE + "/categories/{category}";
    public static final String TOP_INFLUENCER = SEARCH_BASE + "/top";
    public static final String NEW_INFLUENCER = SEARCH_BASE + "/latest";
    public static final String GET_INFLUENCER = SEARCH_BASE + "/id/{publicId}";
    public static final String GET_INFLUENCER_WITH_FILTER = SEARCH_BASE;

    @Bean
    public RouterFunction<ServerResponse> searchEndpointHandler(SearchResourceHandler handler) {
        return route()
                .GET(TOP_CATEGORIES, accept(MediaType.APPLICATION_JSON), handler::getTopCategories)
                .GET(INFLUENCER_CATEGORY, accept(MediaType.APPLICATION_JSON), handler::getInfluencersByCategory)
                .GET(TOP_INFLUENCER, accept(MediaType.APPLICATION_JSON), handler::getTopInfluencers)
                .GET(NEW_INFLUENCER, accept(MediaType.APPLICATION_JSON), handler::getNewInfluencers)
                .GET(GET_INFLUENCER, accept(MediaType.APPLICATION_JSON), handler::getInfluencer)
                .GET(GET_INFLUENCER_WITH_FILTER, accept(MediaType.APPLICATION_JSON), handler::getInfluencerByFilter)
                .build();
    }
}
