package com.tima.platform.model.api.request.analytics;

import lombok.Builder;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 2/21/24
 */
@Builder
public record AudienceDistributionSummary(String topCountry, String topCity, String topGender, String topAge) {}
