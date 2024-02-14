package com.tima.platform.model.api.response.instagram.insight.metrics;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 2/13/24
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record MetricType(long value) {}
