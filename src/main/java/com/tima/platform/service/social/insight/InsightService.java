package com.tima.platform.service.social.insight;

import com.tima.platform.model.api.request.ClientSelectedSocialMedia;
import com.tima.platform.model.constant.DemographicType;
import reactor.core.publisher.Mono;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/27/24
 */
public interface InsightService<T, E> {
    Mono<T> getUserBasicBusinessInsight(ClientSelectedSocialMedia media, String token);
    Mono<E> getUserBasicBusinessInsight(ClientSelectedSocialMedia userId, String token, DemographicType type);
}
