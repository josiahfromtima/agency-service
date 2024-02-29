package com.tima.platform.service.social.insight;

import com.tima.platform.model.api.request.ClientSelectedSocialMedia;
import com.tima.platform.model.api.response.instagram.DemographicStatistic;
import com.tima.platform.model.api.response.instagram.business.BasicBusinessInsight;
import com.tima.platform.model.api.response.instagram.insight.metrics.InsightMetrics;
import com.tima.platform.model.constant.DemographicType;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 2/25/24
 */
@Service
@RequiredArgsConstructor
public class DefaultSocialBusinessInsight
        implements InsightService<BasicBusinessInsight, List<DemographicStatistic>, List<InsightMetrics>>{

    private final LoggerHelper log = LoggerHelper.newInstance(DefaultSocialBusinessInsight.class.getName());
    @Override
    public Mono<BasicBusinessInsight> getUserBasicBusinessInsight(ClientSelectedSocialMedia media, String token) {
        log.error("Default media called for Basic Business Insight");
        return Mono.empty();
    }

    @Override
    public Mono<List<DemographicStatistic>> getUserBasicBusinessInsight(ClientSelectedSocialMedia userId,
                                                                        String token,
                                                                        DemographicType type) {
        log.error("Default media called for Demographic Statistic");
        return Mono.empty();
    }

    @Override
    public Mono<List<InsightMetrics>> getUserBusinessInsightMetrics(ClientSelectedSocialMedia userId, String token) {
        log.error("Default media called for Insight Metrics");
        return Mono.empty();
    }
}
