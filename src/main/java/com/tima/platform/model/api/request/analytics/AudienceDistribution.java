package com.tima.platform.model.api.request.analytics;

import lombok.Builder;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 2/23/24
 */
@Builder
public record AudienceDistribution(String name, long value, long value2) {}
