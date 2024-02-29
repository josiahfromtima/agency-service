package com.tima.platform.model.api.request.analytics;

import lombok.Builder;

import java.util.List;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 2/23/24
 */
@Builder
public record AudienceDistributionGraph(List<AudienceDistribution> ageRange,
                                        List<AudienceDistribution> genderPie,
                                        List<AudienceDistribution> country ) {}
