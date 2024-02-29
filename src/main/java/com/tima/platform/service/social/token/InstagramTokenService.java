package com.tima.platform.service.social.token;

import com.tima.platform.model.api.response.instagram.DemographicStatistic;
import com.tima.platform.model.api.response.instagram.business.BasicBusinessInsight;
import com.tima.platform.model.api.response.instagram.insight.metrics.InsightMetrics;
import com.tima.platform.model.api.response.instagram.token.LongLivedAccessToken;
import com.tima.platform.service.social.insight.InsightService;
import com.tima.platform.service.social.insight.InstagramBusinessInsight;
import com.tima.platform.service.social.instagram.InstagramApiService;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 2/27/24
 */
@Service
@RequiredArgsConstructor
public class InstagramTokenService  implements TokenService<LongLivedAccessToken> {
    private final LoggerHelper log = LoggerHelper.newInstance(InstagramTokenService.class.getName());
    private final InstagramApiService apiService;
    @Override
    public Mono<LongLivedAccessToken> getLongLivedToken(String token) {
        log.info("Getting longed live token from facebook");
        return apiService.getLTTLAccessToken(token);
    }
}
