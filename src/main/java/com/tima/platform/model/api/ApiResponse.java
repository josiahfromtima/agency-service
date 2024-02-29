package com.tima.platform.model.api;

import com.tima.platform.model.api.request.JwtRecord;
import com.tima.platform.util.CampaignSearchSetting;
import com.tima.platform.util.InfluencerSearchSetting;
import com.tima.platform.util.ReportSettings;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/19/23
 */
public class ApiResponse {
    private ApiResponse() {}

    public static Mono<ServerResponse> buildServerResponse(Mono<AppResponse> response) {
        try {
            return response
                    .flatMap(ServerResponse.ok()::bodyValue)
                    .switchIfEmpty(ServerResponse.badRequest().build());
        }catch (Exception e) {
            return ServerResponse.badRequest().build();
        }
    }

    public static String getPublicIdFromToken(JwtAuthenticationToken jwtToken) {
        return jwtToken.getTokenAttributes()
                .getOrDefault("public_id", "")
                .toString();
    }

    public static String getToken(JwtAuthenticationToken jwtToken) {
        return jwtToken.getToken().getTokenValue();
    }

    public static JwtRecord getJwtRecord(JwtAuthenticationToken jwtToken) {
        return JwtRecord.builder()
                .publicId(getPublicIdFromToken(jwtToken))
                .token(getToken(jwtToken))
                .build();
    }

    public static ReportSettings reportSettings(ServerRequest request) {
        return ReportSettings.instance()
                .page(Integer.parseInt(request.queryParam("page").orElse("0")))
                .size(Integer.parseInt(request.queryParam("size").orElse("10")))
                .sortIn(request.queryParam("sortIn").orElse("asc"))
                .sortBy(request.queryParam("sortBy").orElse("createdOn"));
    }

    public static CampaignSearchSetting searchSettings(ServerRequest request) {
        return CampaignSearchSetting.instance()
                .category(request.queryParam("category").orElse(""))
                .audienceSize(request.queryParam("size").orElse(""))
                .audienceAge(request.queryParam("ageGroup").orElse(""))
                .audienceLocation(request.queryParam("location").orElse(""))
                .audienceGender(request.queryParam("gender").orElse(""))
                .socialMediaPlatform(request.queryParam("socialMedia").orElse(""))
                .costPerPost(Long.parseLong(request.queryParam("costPerPost").orElse("0L")));
    }
    public static InfluencerSearchSetting searchSettingsProfile(ServerRequest request) {
        return InfluencerSearchSetting.instance()
                .email(request.queryParam("email").orElse(""))
                .name(request.queryParam("name").orElse(""))
                .username(request.queryParam("username").orElse(""));
    }
}
