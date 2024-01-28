package com.tima.platform.service.social.insight;

import com.tima.platform.model.api.request.ClientSelectedSocialMedia;
import reactor.core.publisher.Mono;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/27/24
 */
public interface InsightService<T> {
    Mono<T> getUserBasicBusinessInsight(ClientSelectedSocialMedia media, String token);
}
