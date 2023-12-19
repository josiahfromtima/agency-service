package com.tima.platform.resource.settings;

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
 * @Date: 12/19/23
 */
@Configuration
public class NotificationSettingResourceConfig {
    public static final String API_V1_URL = "/v1";
    public static final String UPDATE_SETTING = API_V1_URL + "/settings/toggle";
    public static final String CAMPAIGN_AUD_SETTING = API_V1_URL + "/settings/audience";
    public static final String CAMPAIGN_CREATIVE_SETTING = API_V1_URL + "/settings/creative";

    @Bean
    public RouterFunction<ServerResponse> settingEndpointHandler(NotificationSettingResourceHandler handler) {
        return route()
                .PATCH(UPDATE_SETTING, accept(MediaType.APPLICATION_JSON)
                        .and(contentType(MediaType.APPLICATION_JSON)), handler::updateSettings)
                .GET(CAMPAIGN_AUD_SETTING, accept(MediaType.APPLICATION_JSON), handler::getCampaignAudiences)
                .PUT(CAMPAIGN_AUD_SETTING, accept(MediaType.APPLICATION_JSON)
                        .and(contentType(MediaType.APPLICATION_JSON)), handler::updateCampaignAudiences)
                .DELETE(CAMPAIGN_AUD_SETTING, accept(MediaType.APPLICATION_JSON), handler::deleteCampaignAudiences)
                .GET(CAMPAIGN_CREATIVE_SETTING, accept(MediaType.APPLICATION_JSON), handler::getCampaignCreatives)
                .PUT(CAMPAIGN_CREATIVE_SETTING, accept(MediaType.APPLICATION_JSON)
                        .and(contentType(MediaType.APPLICATION_JSON)), handler::updateCampaignCreatives)
                .DELETE(CAMPAIGN_CREATIVE_SETTING, accept(MediaType.APPLICATION_JSON), handler::deleteCampaignCreatives)
                .build();
    }
}
