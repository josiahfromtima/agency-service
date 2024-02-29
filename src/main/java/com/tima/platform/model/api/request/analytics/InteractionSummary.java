package com.tima.platform.model.api.request.analytics;

import lombok.Builder;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 2/21/24
 */
@Builder
public record InteractionSummary(long engagement, long reach,
                                 long impressions, long likes, long comments, long shared) {}
